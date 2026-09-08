package com.pranit.docmind.authorization.dto;

import lombok.Builder;

@Builder
public record RevokeResponse(
        boolean status,
        String message
) {
}
