package com.pranit.docmind.authentication.dto;

import lombok.Builder;

@Builder
public record SignupResponse(
        String challengeId,
        String message
) {
}
