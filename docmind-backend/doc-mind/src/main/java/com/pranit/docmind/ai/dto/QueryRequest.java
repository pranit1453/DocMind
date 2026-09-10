package com.pranit.docmind.ai.dto;

import com.pranit.docmind.entities.constant.Provider;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QueryRequest(
        @NotBlank(message = "Query Required. Please ask something")
        String query,
        @NotNull(message = "Please select the available provider")
        Provider provider,
        @Valid
        Options options
) {
    public enum QueryType {
        NORMAL_QA,
        DOCUMENT_SUMMARY,
        TECHNICAL_INSIGHTS

    }

    public record Options(
            @NotNull(message = "Query type is required")
            QueryType queryType,
            @Valid
            RetrievalOptions retrieval
    ) {
    }

    public record RetrievalOptions(
            @Min(value = 1, message = "topK must be between 1 and 10")
            @Max(value = 10, message = "topK must be between 1 and 10")
            Integer topK,
            @DecimalMin(value = "0.0", message = "similarityThreshold must be between 0.0 and 1.0")
            @DecimalMax(value = "1.0", message = "similarityThreshold must be between 0.0 and 1.0")
            Double similarityThreshold
    ) {
    }
}