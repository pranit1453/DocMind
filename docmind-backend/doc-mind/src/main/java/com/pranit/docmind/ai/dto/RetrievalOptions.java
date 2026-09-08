package com.pranit.docmind.ai.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record RetrievalOptions(
        @Min(value = 1, message = "topK must be between 1 and 10")
        @Max(value = 10, message = "topK must be between 1 and 10")
        Integer topK,

        @DecimalMin(value = "0.0", message = "similarityThreshold must be between 0.0 and 1.0")
        @DecimalMax(value = "1.0", message = "similarityThreshold must be between 0.0 and 1.0")
        Double similarityThreshold
) {
}

