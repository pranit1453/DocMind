package com.pranit.docmind.rag.pipeline.etl.loader.impl;

import com.pranit.docmind.rag.pipeline.etl.loader.DocumentLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public final class VectorStoreLoader implements DocumentLoader {

    private final VectorStore vectorStore;

    @Override
    public void load(final List<Document> documents) {
        vectorStore.add(documents);
    }
}

