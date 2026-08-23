package com.pranit.docmind.authentication.dto;

import lombok.Builder;

@Builder
public record ChangePasswordResponse(
        String message
) {
}
