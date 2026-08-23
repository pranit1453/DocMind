package com.pranit.docmind.ai.dto;

import com.pranit.docmind.entities.constant.Provider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QueryRequest(
        @NotBlank(message = "Query Required. Please ask something")
        String query,
        @NotNull(message = "Please select the available provider")
        Provider provider
) {
}