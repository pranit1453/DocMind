package com.pranit.docmind.rag.pipeline.document.json;

import com.pranit.docmind.entities.constant.DocumentType;
import com.pranit.docmind.rag.pipeline.document.DocumentPipeline;
import com.pranit.docmind.rag.pipeline.etl.json.JsonDocumentExtractor;
import com.pranit.docmind.rag.pipeline.etl.json.JsonDocumentLoader;
import com.pranit.docmind.rag.pipeline.etl.json.JsonDocumentTransformer;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JsonDocumentPipeline implements DocumentPipeline {

    private final JsonDocumentExtractor extractor;
    private final JsonDocumentTransformer transformer;
    private final JsonDocumentLoader loader;

    @Override
    public DocumentType getFileType() {
        return DocumentType.JSON;
    }

    @Override
    public long process(final UUID documentId, final Resource resource) {
        final var documents = extractor.extract(resource);
        final var transformed = transformer.transform(documents);
        loader.load(documentId, transformed);
        return transformed.size();
    }
}
