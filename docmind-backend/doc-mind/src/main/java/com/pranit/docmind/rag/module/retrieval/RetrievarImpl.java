package com.pranit.docmind.rag.module.retrieval;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.rag.retrieval.join.ConcatenationDocumentJoiner;
import org.springframework.ai.rag.retrieval.join.DocumentJoiner;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RetrievarImpl implements Retrievar {

    private final VectorStore vectorStore;

    @Override
    public DocumentRetriever vectorStoreRetriever(final UUID documentId) {
        return VectorStoreDocumentRetriever.builder()
                .vectorStore(this.vectorStore)
                .topK(3)
                .similarityThreshold(0.65)
                .filterExpression(() ->
                        new FilterExpressionBuilder()
                                .eq("documentId", documentId.toString())
                                .build())
                .build();
    }

    @Override
    public DocumentJoiner joinStoreRetriever() {
        return new ConcatenationDocumentJoiner();
    }
}
