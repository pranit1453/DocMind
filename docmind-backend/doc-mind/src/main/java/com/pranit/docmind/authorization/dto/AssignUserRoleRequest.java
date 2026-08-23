package com.pranit.docmind.authorization.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record AssignUserRoleRequest(

        @NotNull(message = "User id is mandatory")
        UUID userId,
        @NotNull(message = "Role id is mandatory")
        Long roleId
) {
}
