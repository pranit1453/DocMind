package com.pranit.docmind.authorization.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateRoleRequest(

        @NotBlank(message = "Role name is mandatory!!!")
        String roleName,

        @Size(max = 255, message = "Role description cannot exceed 255 characters.")
        String roleDescription
) {
}
