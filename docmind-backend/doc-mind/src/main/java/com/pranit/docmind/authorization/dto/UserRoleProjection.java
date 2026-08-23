package com.pranit.docmind.authorization.dto;

import com.pranit.docmind.entities.constant.RoleStatus;

import java.util.UUID;

public record UserRoleProjection(
        UUID userId,
        String username,
        Long roleId,
        String roleName,
        String roleDescription,
        RoleStatus status
) {
}
