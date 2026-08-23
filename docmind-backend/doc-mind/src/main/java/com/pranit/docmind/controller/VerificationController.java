package com.pranit.docmind.controller;

import com.pranit.docmind.authentication.dto.VerificationResponse;
import com.pranit.docmind.authentication.dto.VerifyOtp;
import com.pranit.docmind.authentication.service.VerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/verify")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Verification Management",
        description = "Endpoints for verifying OTPs for account registration and password reset."
)
public class VerificationController {

    private final VerificationService verificationService;

    @Operation(
            summary = "Verify registration OTP",
            description = "Verifies the OTP provided during user registration and activates the user account."
    )
    @PostMapping(version = "v1")
    public ResponseEntity<VerificationResponse> verifyRegistration(@RequestBody @Valid VerifyOtp request) {
        return ResponseEntity.ok(verificationService.verifyRegistration(request));
    }

    @Operation(
            summary = "Verify password reset OTP",
            description = "Verifies the OTP provided for password reset and authorizes the password reset process."
    )
    @PostMapping(value = "/reset", version = "v1")
    public ResponseEntity<VerificationResponse> verifyPasswordResetOtp(@RequestBody @Valid VerifyOtp request) {
        final VerificationResponse response = verificationService.verifyPasswordResetOtp(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
