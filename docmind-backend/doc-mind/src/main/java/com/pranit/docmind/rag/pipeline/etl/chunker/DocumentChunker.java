package com.pranit.docmind.rag.pipeline.etl.chunker;

import org.springframework.ai.document.Document;

import java.util.List;

@FunctionalInterface
public interface DocumentChunker {

    List<Document> chunk(List<Document> documents);
}

