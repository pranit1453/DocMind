package com.pranit.docmind.rag.pipeline.document.enricher;

import org.springframework.ai.document.Document;

import java.util.List;

public interface DocumentMetadataEnricher {

    List<Document> enrich(List<Document> documents);
}
