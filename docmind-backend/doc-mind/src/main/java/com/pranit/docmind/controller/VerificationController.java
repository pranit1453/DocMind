package com.pranit.docmind.controller;

import com.pranit.docmind.authentication.dto.VerificationResponse;
import com.pranit.docmind.authentication.dto.VerifyOtp;
import com.pranit.docmind.authentication.service.VerificationService;
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
public class VerificationController {

    private final VerificationService verificationService;

    @PostMapping(version = "v1")
    public ResponseEntity<VerificationResponse> verifyRegistration(@RequestBody @Valid VerifyOtp request) {
        return ResponseEntity.ok(verificationService.verifyRegistration(request));
    }
    
    @PostMapping(value = "/reset", version = "v1")
    public ResponseEntity<VerificationResponse> verifyPasswordResetOtp(@RequestBody @Valid VerifyOtp request) {
        final VerificationResponse response = verificationService.verifyPasswordResetOtp(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
