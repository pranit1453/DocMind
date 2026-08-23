package com.pranit.docmind.rag.pipeline.document.pdf;

import com.pranit.docmind.entities.constant.DocumentType;
import com.pranit.docmind.rag.pipeline.document.DocumentPipeline;
import com.pranit.docmind.rag.pipeline.etl.pdf.PdfDocumentExtractor;
import com.pranit.docmind.rag.pipeline.etl.pdf.PdfDocumentLoader;
import com.pranit.docmind.rag.pipeline.etl.pdf.PdfDocumentTransformer;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PdfDocumentPipeline implements DocumentPipeline {

    private final PdfDocumentExtractor extractor;
    private final PdfDocumentTransformer transformer;
    private final PdfDocumentLoader loader;

    @Override
    public DocumentType getFileType() {
        return DocumentType.PDF;
    }

    @Override
    public long process(final UUID documentId, final Resource resource) {
        final var documents = extractor.extract(resource);
        final var transformed = transformer.transform(documents);
        loader.load(documentId, transformed);
        return transformed.size();
    }
}
