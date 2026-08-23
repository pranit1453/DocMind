package com.pranit.docmind.authentication.dto;

import lombok.Builder;

@Builder
public record PasswordResponse(
        String challengeId,
        String message
) {
}
