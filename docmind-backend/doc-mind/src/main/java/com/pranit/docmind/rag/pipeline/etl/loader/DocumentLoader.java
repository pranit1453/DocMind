package com.pranit.docmind.rag.pipeline.etl.loader;

import org.springframework.ai.document.Document;

import java.util.List;

public interface DocumentLoader {

    void load(List<Document> documents);
}
