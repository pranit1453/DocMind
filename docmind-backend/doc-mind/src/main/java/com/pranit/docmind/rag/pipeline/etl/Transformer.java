package com.pranit.docmind.rag.pipeline.etl;

import org.springframework.ai.document.Document;

import java.util.List;

@FunctionalInterface
public interface Transformer {

    List<Document> transform(List<Document> documents);
}
