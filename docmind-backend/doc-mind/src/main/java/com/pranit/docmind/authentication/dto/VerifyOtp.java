package com.pranit.docmind.authentication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record VerifyOtp(
        @NotBlank(message = "Challenge Id is mandatory!!!")
        String challengeId,
        @NotBlank(message = "Otp is required!!!")
        String otp
) {
}
