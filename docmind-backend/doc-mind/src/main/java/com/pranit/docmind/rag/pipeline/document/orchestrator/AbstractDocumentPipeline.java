package com.pranit.docmind.rag.pipeline.document.orchestrator;

import com.pranit.docmind.constant.DocMetadata;
import com.pranit.docmind.entities.entity.DocumentMetadata;
import com.pranit.docmind.rag.pipeline.document.DocumentPipeline;
import com.pranit.docmind.rag.pipeline.etl.chunker.DocumentChunker;
import com.pranit.docmind.rag.pipeline.etl.loader.DocumentLoader;
import com.pranit.docmind.rag.pipeline.etl.parser.Extractor;
import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public abstract class AbstractDocumentPipeline implements DocumentPipeline {

    private final Extractor extractor;
    private final DocumentChunker chunker;
    private final DocumentLoader loader;

    protected AbstractDocumentPipeline(
            final Extractor extractor,
            final DocumentChunker chunker,
            final DocumentLoader loader) {
        this.extractor = extractor;
        this.chunker = chunker;
        this.loader = loader;
    }

    @Override
    public final long process(final DocumentMetadata metadata, final Resource resource) {
        final var documents = extractor.extract(resource);
        final var chunks = chunker.chunk(documents);
        final var enrichedChunks = enrich(metadata, chunks);
        loader.load(enrichedChunks);
        return enrichedChunks.size();
    }

    private List<Document> enrich(final DocumentMetadata metadata, final List<Document> chunks) {
        List<Document> enrichedChunks = new ArrayList<>(chunks.size());
        for (var index = 0; index < chunks.size(); index++) {
            final var chunk = chunks.get(index);
            final var enrichedMetadata = new LinkedHashMap<>(chunk.getMetadata());

            enrichedMetadata.put(DocMetadata.DOCUMENT_ID, metadata.getDocumentId());
            enrichedMetadata.put(DocMetadata.FILE_NAME, metadata.getFileName());
            enrichedMetadata.put(DocMetadata.CHUNK_INDEX, index);
            enrichedMetadata.put(DocMetadata.TOTAL_CHUNKS, chunks.size());

            Optional.ofNullable(chunk.getMetadata().get(DocMetadata.LEGACY_PAGE_NUMBER))
                    .or(() -> Optional.ofNullable(chunk.getMetadata().get(DocMetadata.PAGE_NUMBER)))
                    .ifPresent(pageNumber -> enrichedMetadata.put(DocMetadata.PAGE_NUMBER, pageNumber));

            enrichedChunks.add(new Document(chunk.getText(), enrichedMetadata));
        }
        return enrichedChunks;
    }
}

