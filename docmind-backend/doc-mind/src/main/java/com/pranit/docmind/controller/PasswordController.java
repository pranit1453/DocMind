package com.pranit.docmind.controller;

import com.pranit.docmind.authentication.dto.ChangeForgotPasswordRequest;
import com.pranit.docmind.authentication.dto.ChangePassword;
import com.pranit.docmind.authentication.dto.ChangePasswordResponse;
import com.pranit.docmind.authentication.dto.ForgotPasswordEmail;
import com.pranit.docmind.authentication.dto.PasswordResponse;
import com.pranit.docmind.authentication.service.PasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Password Management",
        description = "Endpoints for requesting password resets, resetting forgotten passwords, and changing account passwords."
)
public class PasswordController {

    private final PasswordService passwordService;

    @Operation(
            summary = "Request password reset",
            description = "Initiates the password reset process by sending a password reset OTP to the user's registered email address."
    )
    @PostMapping(value = "/reset/request", version = "v1")
    public ResponseEntity<PasswordResponse> requestPasswordReset(@Valid @RequestBody ForgotPasswordEmail request) {
        final PasswordResponse response = passwordService.requestPasswordReset(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Reset forgotten password",
            description = "Resets the user's password after successful verification of the password reset request."
    )
    @PatchMapping(value = "/reset", version = "v1")
    public ResponseEntity<PasswordResponse> resetPassword(@Valid @RequestBody ChangeForgotPasswordRequest request) {
        final PasswordResponse response = passwordService.resetPassword(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Change account password",
            description = "Changes the authenticated user's current account password."
    )
    @PatchMapping(value = "/change", version = "v1")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ChangePasswordResponse> changeAccountPassword(@Valid @RequestBody ChangePassword request) {
        final ChangePasswordResponse response = passwordService.changeAccountPassword(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
