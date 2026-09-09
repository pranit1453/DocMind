package com.pranit.docmind.rag.dto;

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
    public record Citation(
            UUID documentId,
            String fileName,
            Integer pageNumber,
            Integer chunkIndex,
            Integer previousChunkIndex,
            Integer nextChunkIndex,
            Double similarityScore
    ) {
    }
}