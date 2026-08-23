package com.pranit.docmind.authorization.dto;

import lombok.Builder;

@Builder
public record RoleResponse(
        Long roleId,
        String roleName,
        String roleDescription
) {
}
