package com.pranit.docmind.controller;

import com.pranit.docmind.authentication.dto.SignupRequest;
import com.pranit.docmind.authentication.dto.SignupResponse;
import com.pranit.docmind.authentication.service.RegistrationService;
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
@RequestMapping("/api/register")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Registration Management",
        description = "Endpoints for registering new users and administrators."
)
public class RegistrationController {

    private final RegistrationService registrationService;

    @Operation(
            summary = "Register new user",
            description = "Creates a new user account and initiates the account verification process."
    )
    @PostMapping(version = "v1")
    public ResponseEntity<SignupResponse> registerNewUser(@Valid @RequestBody SignupRequest request) {
        final SignupResponse response = registrationService.createNewUserAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Register new administrator",
            description = "Creates a new administrator account with the required administrative privileges."
    )
    @PostMapping(value = "/admin", version = "v1")
    public ResponseEntity<SignupResponse> registerNewAdmin(@Valid @RequestBody SignupRequest request) {
        final SignupResponse response = registrationService.createNewAdminAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
