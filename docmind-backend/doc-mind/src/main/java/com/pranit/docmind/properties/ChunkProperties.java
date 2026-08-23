package com.pranit.docmind.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rag")
public record ChunkProperties(
        int chunkSize,
        int chunkOverlap,
        int topK,
        double similarityThreshold
) {
}