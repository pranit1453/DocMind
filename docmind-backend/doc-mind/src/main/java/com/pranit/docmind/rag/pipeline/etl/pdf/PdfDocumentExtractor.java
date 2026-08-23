package com.pranit.docmind.rag.pipeline.etl.pdf;

import com.pranit.docmind.rag.pipeline.etl.Extractor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class PdfDocumentExtractor implements Extractor {

    @Override
    public List<Document> extract(final Resource resource) {
        final var pdfReader = new PagePdfDocumentReader(
                resource, PdfDocumentReaderConfig.builder()
                .withPageTopMargin(0)
                .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                        .withNumberOfTopTextLinesToDelete(0)
                        .build())
                .withPagesPerDocument(1)
                .build());
        return pdfReader.read();
    }
}
