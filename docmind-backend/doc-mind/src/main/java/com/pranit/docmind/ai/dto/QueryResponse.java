package com.pranit.docmind.ai.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record QueryResponse(
        String content,
        String responseTime,
        List<Citation> citations
) {

    @Builder
    public record Citation(
            String documentId,
            String fileName,
            Integer pageNumber,
            Integer chunkIndex
    ) {
    }
}
