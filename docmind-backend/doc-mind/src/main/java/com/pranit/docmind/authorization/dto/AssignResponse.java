package com.pranit.docmind.authorization.dto;

import lombok.Builder;

@Builder
public record AssignResponse(
        boolean status,
        String message
) {
}
