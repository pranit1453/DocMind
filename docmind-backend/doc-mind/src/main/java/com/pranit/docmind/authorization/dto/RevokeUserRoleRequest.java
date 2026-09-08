package com.pranit.docmind.authorization.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.UUID;

@Builder
public record RevokeUserRoleRequest(

        @NotNull(message = "User id is mandatory")
        UUID userId,

        @NotNull(message = "Role id is mandatory")
        @Positive(message = "Role id must be greater than zero")
        Long roleId
) {
}
