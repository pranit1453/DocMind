package com.pranit.docmind.rag.module.retrieval;

import com.pranit.docmind.ai.dto.RetrievalOptions;
import com.pranit.docmind.constant.DocMetadata;
import com.pranit.docmind.properties.RagProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.rag.retrieval.join.ConcatenationDocumentJoiner;
import org.springframework.ai.rag.retrieval.join.DocumentJoiner;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RetrievarImpl implements Retrievar {

    private final VectorStore vectorStore;
    private final RagProperties properties;

    @Override
    public DocumentRetriever vectorStoreRetriever(final UUID documentId, final RetrievalOptions options) {
        final var retrieval = properties.retrieval();
        final var topK = Optional.ofNullable(options.topK())
                .orElse(retrieval.topK());
        final var similarityThreshold = Optional.ofNullable(options.similarityThreshold())
                .orElse(retrieval.similarityThreshold());
        return VectorStoreDocumentRetriever.builder()
                .vectorStore(this.vectorStore)
                .topK(topK)
                .similarityThreshold(similarityThreshold)
                .filterExpression(() -> new FilterExpressionBuilder()
                        .eq(DocMetadata.DOCUMENT_ID, documentId.toString())
                        .build())
                .build();
    }

    @Override
    public DocumentJoiner joinStoreRetriever() {
        return new ConcatenationDocumentJoiner();
    }
}
