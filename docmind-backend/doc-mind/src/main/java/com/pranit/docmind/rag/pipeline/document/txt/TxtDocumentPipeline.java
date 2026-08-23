package com.pranit.docmind.rag.pipeline.document.txt;

import com.pranit.docmind.entities.constant.DocumentType;
import com.pranit.docmind.rag.pipeline.document.DocumentPipeline;
import com.pranit.docmind.rag.pipeline.etl.txt.TxtDocumentExtractor;
import com.pranit.docmind.rag.pipeline.etl.txt.TxtDocumentLoader;
import com.pranit.docmind.rag.pipeline.etl.txt.TxtDocumentTransformer;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TxtDocumentPipeline implements DocumentPipeline {

    private final TxtDocumentExtractor extractor;
    private final TxtDocumentTransformer transformer;
    private final TxtDocumentLoader loader;

    @Override
    public DocumentType getFileType() {
        return DocumentType.TXT;
    }

    @Override
    public long process(final UUID documentId, final Resource resource) {
        final var documents = extractor.extract(resource);
        final var transformed = transformer.transform(documents);
        loader.load(documentId, transformed);
        return transformed.size();
    }
}
