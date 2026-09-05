package com.pranit.docmind.document.service.impl;

import com.pranit.docmind.aop.annotation.LogExecution;
import com.pranit.docmind.authentication.exception.UserNotExistsException;
import com.pranit.docmind.authentication.repository.UserRepository;
import com.pranit.docmind.document.dto.DocumentResponse;
import com.pranit.docmind.document.exception.DocumentAlreadyExistsException;
import com.pranit.docmind.document.exception.DocumentProcessingException;
import com.pranit.docmind.document.repository.DocumentRepository;
import com.pranit.docmind.document.service.DocumentStatusService;
import com.pranit.docmind.document.service.DocumentUploadService;
import com.pranit.docmind.entities.constant.FileStatus;
import com.pranit.docmind.entities.entity.DocumentMetadata;
import com.pranit.docmind.entities.entity.User;
import com.pranit.docmind.helper.SecurityContext;
import com.pranit.docmind.rag.pipeline.factory.DocumentPipelineFactory;
import com.pranit.docmind.wrapper.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentUploadServiceImpl implements DocumentUploadService {

    private final DocumentRepository documentRepository;
    private final DocumentPipelineFactory factory;
    private final DocumentStatusService documentStatusService;
    private final UserRepository userRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    @LogExecution
    public ApiResponse<DocumentResponse> uploadDocument(final MultipartFile file) {
        final UUID userId = SecurityContext.getCurrentUserId();
        final User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with id {}", userId);
                    return new UserNotExistsException("User not found");
                });
        if (documentRepository.existsByFileNameAndUser_UserId(file.getOriginalFilename(), userId)) {
            log.warn("File {} already exists", file.getOriginalFilename());
            throw new DocumentAlreadyExistsException("File already exists");
        }
        DocumentMetadata metadata = DocumentMetadata.builder()
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .fileStatus(FileStatus.UPLOADING)
                .chunksCreated(0)
                .user(user)
                .build();
        metadata = documentRepository.save(metadata);
        final Resource resource = file.getResource();
        documentStatusService.markProcessing(metadata.getDocumentId());
        final long chunkSize = factory.getPipeline(metadata, resource);
        if (chunkSize <= 0) {
            documentStatusService.markFailed(metadata.getDocumentId());
            throw new DocumentProcessingException("No chunks were created");
        }
        metadata.setChunksCreated(chunkSize);
        metadata.setFileStatus(FileStatus.INDEXED);
        final DocumentResponse response = DocumentResponse.builder()
                .documentId(metadata.getDocumentId())
                .fileName(metadata.getFileName())
                .fileSize(metadata.getFileSize())
                .status(metadata.getFileStatus())
                .chunksCreated(metadata.getChunksCreated())
                .createdAt(metadata.getCreatedAt())
                .build();
        return ApiResponse.<DocumentResponse>builder()
                .status(true)
                .message("Document uploaded and indexed successfully")
                .data(response)
                .timestamp(Instant.now())
                .build();
    }
}
