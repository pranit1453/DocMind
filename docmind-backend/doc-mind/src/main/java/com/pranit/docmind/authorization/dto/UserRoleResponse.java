package com.pranit.docmind.authorization.dto;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record UserRoleResponse(
        Long userRoleId,
        UUID userId,
        String username,
        List<RoleResponses> roles
) {
}
