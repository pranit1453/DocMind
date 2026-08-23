package com.pranit.docmind.controller;

import com.pranit.docmind.authentication.dto.SignupRequest;
import com.pranit.docmind.authentication.dto.SignupResponse;
import com.pranit.docmind.authentication.service.RegistrationService;
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
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping(version = "v1")
    public ResponseEntity<SignupResponse> registerNewUser(@RequestBody @Valid SignupRequest request) {
        final SignupResponse response = registrationService.createNewUserAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "/admin", version = "v1")
    public ResponseEntity<SignupResponse> registerNewAdmin(@RequestBody @Valid SignupRequest request) {
        final SignupResponse response = registrationService.createNewAdminAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
