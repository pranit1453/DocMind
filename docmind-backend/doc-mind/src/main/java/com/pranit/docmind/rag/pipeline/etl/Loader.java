package com.pranit.docmind.rag.pipeline.etl;

import org.springframework.ai.document.Document;

import java.util.List;
import java.util.UUID;

public interface Loader {

    void load(UUID documentId, List<Document> documents);
}
