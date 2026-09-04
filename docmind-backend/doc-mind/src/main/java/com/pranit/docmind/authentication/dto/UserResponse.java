package com.pranit.docmind.authentication.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserResponse(
        UUID userId,
        String username,
        String fullName,
        String email,
        boolean enabled,
        boolean deleted
) {
}
