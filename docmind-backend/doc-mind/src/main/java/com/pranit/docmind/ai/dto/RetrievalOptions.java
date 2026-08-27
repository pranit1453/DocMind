package com.pranit.docmind.ai.dto;

public record RetrievalOptions(
        Integer topK,
        Double similarityThreshold
) {
}

