package com.pranit.docmind.authorization.dto;

import com.pranit.docmind.entities.constant.RoleStatus;
import lombok.Builder;

@Builder
public record RoleResponses(
        Long roleId,
        String roleName,
        String roleDescription,
        RoleStatus status
) {
}
