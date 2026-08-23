package com.pranit.docmind.rag.module.postretrieval;

import org.jspecify.annotations.NonNull;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.postretrieval.document.DocumentPostProcessor;

import java.util.List;

public class DefaultDocumentPostProcessor implements DocumentPostProcessor {

    @Override
    public @NonNull List<Document> process(@NonNull Query query, @NonNull List<Document> documents) {
        return List.of();
    }
}
