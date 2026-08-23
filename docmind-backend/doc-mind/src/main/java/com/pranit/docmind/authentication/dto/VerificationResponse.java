package com.pranit.docmind.authentication.dto;

import lombok.Builder;

@Builder
public record VerificationResponse(
        String message
) {
}
