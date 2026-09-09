package com.pranit.docmind.rag.module.retrieval;

import com.pranit.docmind.ai.dto.RetrievalOptions;
import com.pranit.docmind.constant.DocMetadata;
import com.pranit.docmind.properties.RagProperties;
import com.pranit.docmind.rag.dto.Context;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class DocumentRetriever {

    private final VectorStore vectorStore;
    private final RagProperties properties;

    public DocumentRetriever(VectorStore vectorStore, RagProperties properties) {
        this.vectorStore = vectorStore;
        this.properties = properties;
    }

    public List<Document> similaritySearch(final UUID documentId, final String query, final RetrievalOptions options) {
        final var retrieval = properties.retrieval();
        final var topK = Optional.ofNullable(options.topK())
                .orElse(retrieval.topK());
        final var similarityThreshold = Optional.ofNullable(options.similarityThreshold())
                .orElse(retrieval.similarityThreshold());
        final var filterExpression = new FilterExpressionBuilder()
                .eq("documentId", documentId.toString())
                .build();
        final var builder = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .filterExpression(filterExpression)
                .similarityThreshold(similarityThreshold);
        return vectorStore.similaritySearch(builder.build());
    }

    public List<Context.Citation> toCitations(final List<Document> documents) {
        return documents.stream()
                .map(this::citationMapper)
                .toList();
    }

    private Context.Citation citationMapper(final Document document) {
        final var metadata = document.getMetadata();
        return Context.Citation.builder()
                .documentId(UUID.fromString((String) metadata.get(DocMetadata.DOCUMENT_ID)))
                .fileName((String) metadata.get(DocMetadata.FILE_NAME))
                .pageNumber((Integer) metadata.get(DocMetadata.PAGE_NUMBER))
                .chunkIndex((Integer) metadata.get(DocMetadata.CHUNK_INDEX))
                .previousChunkIndex((Integer) metadata.get(DocMetadata.PREVIOUS_CHUNK_INDEX))
                .nextChunkIndex((Integer) metadata.get(DocMetadata.NEXT_CHUNK_INDEX))
                .similarityScore(document.getScore())
                .build();
    }
}
