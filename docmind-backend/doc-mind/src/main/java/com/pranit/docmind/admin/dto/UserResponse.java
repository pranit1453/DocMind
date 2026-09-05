package com.pranit.docmind.admin.dto;

import com.pranit.docmind.authorization.dto.RoleResponse;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record UserResponse(
        UUID userId,
        String username,
        String email,
        boolean enabled,
        boolean deleted,
        List<RoleResponse> roles
) {
}
