package com.pranit.docmind.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "rag")
@Validated
public record RagProperties(
        @Valid Chunking chunking,
        @Valid Retrieval retrieval,
        @Valid Rewrite rewrite
) {

    public record Chunking(
            @Min(1) int chunkSize,
            @Min(1) int minChunkSizeChars,
            @Min(1) int minChunkLengthToEmbed,
            @Min(1) int maxNumChunks,
            boolean keepSeparator
    ) {
    }

    public record Retrieval(
            @Min(1) int topK,
            @Min(1) int candidateK,
            @DecimalMin("0.0")
            @DecimalMax("1.0")
            double similarityThreshold
    ) {
    }

    public record Rewrite(
            String apiKey,
            String baseUrl,
            @Valid Chat chat
    ) {
    }

    public record Chat(
            String model,
            @DecimalMin("0.0")
            @DecimalMax("1.0")
            double temperature,
            @Min(1) int maxTokens
    ) {
    }
}

