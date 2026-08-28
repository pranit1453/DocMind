package com.pranit.docmind.authorization.dto;

import com.pranit.docmind.entities.constant.RoleStatus;
import lombok.Builder;

@Builder
public record RoleResponse(
        Long roleId,
        String roleName,
        String roleDescription,
        RoleStatus status
) {
}
