package com.pranit.docmind.controller;

import com.pranit.docmind.authentication.service.AccountDeletionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@Validated
@Tag(name = "Account", description = "User account management APIs")
public class UserAccountController {

    private final AccountDeletionService accountDeletionService;

    @Operation(
            summary = "Deactivate user account",
            description = "Deactivates the authenticated user's account and schedules permanent deletion after 15 days."
    )
    @PostMapping(value = "/deactivate", version = "v1")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deactivateUserAccount() {
        accountDeletionService.deleteUserAccount();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
