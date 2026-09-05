package com.pranit.docmind.authorization.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pranit.docmind.entities.constant.RoleStatus;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record RoleResponse(
        Long roleId,
        String roleName,
        String roleDescription,
        RoleStatus status
) {
}
