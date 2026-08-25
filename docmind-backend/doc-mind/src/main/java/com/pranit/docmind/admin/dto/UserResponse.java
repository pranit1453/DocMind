package com.pranit.docmind.admin.dto;

import com.pranit.docmind.entities.constant.RoleStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserResponse(
        UUID userId,
        String username,
        String roleName,
        String email,
        boolean enabled,
        boolean deleted,
        RoleStatus status
) {
}
