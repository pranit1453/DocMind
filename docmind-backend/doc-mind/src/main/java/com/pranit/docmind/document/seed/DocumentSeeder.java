package com.pranit.docmind.document.seed;

import com.pranit.docmind.authentication.repository.UserRepository;
import com.pranit.docmind.document.exception.DocumentProcessingException;
import com.pranit.docmind.document.repository.DocumentRepository;
import com.pranit.docmind.document.repository.SeedHistoryRepository;
import com.pranit.docmind.document.service.DocumentStatusService;
import com.pranit.docmind.entities.constant.FileStatus;
import com.pranit.docmind.entities.entity.DocumentMetadata;
import com.pranit.docmind.entities.entity.SeedHistory;
import com.pranit.docmind.entities.entity.User;
import com.pranit.docmind.properties.AdminSeedProperties;
import com.pranit.docmind.rag.pipeline.factory.DocumentPipelineFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentSeeder {

    private final DocumentPipelineFactory factory;
    private final SeedHistoryRepository seedHistoryRepository;
    private final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
    private final DocumentRepository documentRepository;
    private final DocumentStatusService documentStatusService;
    private final UserRepository userRepository;
    private final AdminSeedProperties properties;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    @Async
    @EventListener(ApplicationReadyEvent.class)
    public void seedDocuments() throws IOException, ExecutionException, InterruptedException {
        Resource[] resources = resourceResolver.getResources("classpath:file/*");
        final List<Future<?>> futures = new ArrayList<>();

        for (Resource resource : resources) {
            final String seedName = resource.getFilename();
            if (seedName == null) continue;
            if (seedHistoryRepository.existsBySeedName(seedName)) {
                log.info("Already seeded: {}", seedName);
                continue;
            }
            log.info("Seeding document: {}", seedName);
            futures.add(executor.submit(() -> {
                try {
                    uploadFileAndProcessIndexing(resource, seedName);
                    log.info("Document seeded successfully: {}", seedName);
                } catch (IOException e) {
                    log.error("Failed to seed document: {} | {}", seedName, e.getMessage());
                    throw new RuntimeException(e);
                }
            }));
        }
        for (Future<?> future : futures) future.get();
        log.info("Document seeding complete.");
        executor.close();
    }

    private void uploadFileAndProcessIndexing(final Resource resource, final String seedName) throws IOException {
        try {
            final User user = userRepository.findByUsername(properties.username())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            DocumentMetadata metadata = DocumentMetadata.builder()
                    .fileName(seedName)
                    .fileSize(resource.contentLength())
                    .fileStatus(FileStatus.UPLOADING)
                    .chunksCreated(0)
                    .user(user)
                    .build();
            metadata = documentRepository.save(metadata);
            documentStatusService.markProcessing(metadata.getDocumentId());
            final long chunkSize = factory.getPipeline(metadata, resource);
            if (chunkSize <= 0) {
                documentStatusService.markFailed(metadata.getDocumentId());
                throw new DocumentProcessingException("No chunks were created: " + resource.getFilename());
            }
            metadata.setChunksCreated(chunkSize);
            metadata.setFileStatus(FileStatus.INDEXED);
            documentRepository.save(metadata);
            final SeedHistory history = SeedHistory.builder()
                    .seedName(seedName)
                    .seededAt(Instant.now())
                    .build();
            seedHistoryRepository.save(history);
        } catch (Exception e) {
            throw new DocumentProcessingException("Failed to process: " + seedName + "{" + e.getMessage() + "}");
        }
    }
}
