package com.pranit.docmind.rag.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.UUID;

@Builder
public record Context(
        String originalQuery,
        String rewrittenQuery,
        String context,
        List<Document> documents,
        List<Citation> citations
) {

    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Citation(
            UUID documentId,
            String fileName,
            Long pageNumber,
            Long chunkIndex,
            Long previousChunkIndex,
            Long nextChunkIndex,
            Double similarityScore
    ) {
    }
}