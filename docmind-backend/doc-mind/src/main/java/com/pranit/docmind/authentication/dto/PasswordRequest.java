package com.pranit.docmind.authentication.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordRequest(
        @NotBlank(message = "Password is required")
        String password,

        @NotBlank(message = "Confirm password is required")
        String confirmPassword
) {
}
