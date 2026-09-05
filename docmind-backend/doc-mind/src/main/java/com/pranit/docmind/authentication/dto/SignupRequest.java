package com.pranit.docmind.authentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record SignupRequest(
        @NotBlank(message = "Name is required")
        String fullName,
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$",
                message = """
                        Password must contain at least:
                        one uppercase letter,
                        one lowercase letter,
                        one number,
                        and one special character
                        """
        )
        String password,
        @NotBlank(message = "Confirm password is required")
        String confirmPassword
) {
}
