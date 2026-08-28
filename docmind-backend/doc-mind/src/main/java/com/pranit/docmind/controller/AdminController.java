package com.pranit.docmind.controller;

import com.pranit.docmind.admin.dto.UserAccountControlRequest;
import com.pranit.docmind.admin.dto.UserResponse;
import com.pranit.docmind.admin.service.AdminService;
import com.pranit.docmind.authorization.dto.AssignResponse;
import com.pranit.docmind.authorization.dto.AssignUserRoleRequest;
import com.pranit.docmind.authorization.dto.RevokeResponse;
import com.pranit.docmind.authorization.dto.RevokeUserRoleRequest;
import com.pranit.docmind.authorization.dto.UserRoleResponse;
import com.pranit.docmind.wrapper.ApiResponse;
import com.pranit.docmind.wrapper.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/portal")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Admin Portal",
        description = "Endpoints for managing users and their role assignments."
)
public class AdminController {

    private final AdminService adminService;

    @Operation(
            summary = "Get all users",
            description = "Returns a paginated list of users with their assigned roles."
    )
    @GetMapping("/all/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<UserResponse>> fetchAllUsers(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(10) int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        final PageResponse<UserResponse> responses = adminService.fetchUsers(page, size, keyword, sortBy, sortDirection);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @Operation(
            summary = "Control user account",
            description = "Enables, disables, or updates the account status of a user."
    )
    @PostMapping("/user/{userId}/control")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> userAccountControl(@NotNull @PathVariable UUID userId, @Valid @RequestBody UserAccountControlRequest request) {
        final ApiResponse<Void> response = adminService.controlUserAccount(userId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Assign role to user",
            description = "Assigns a role to the specified user."
    )
    @PostMapping(value = "/assign", version = "v1")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AssignResponse> assignRoleToUser(@Valid @RequestBody AssignUserRoleRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.assignRoleToUser(request));
    }

    @Operation(
            summary = "Revoke role assigned to user",
            description = "Revokes an active role assignment from a user."
    )
    @PatchMapping(value = "/{userRoleId}/revoke", version = "v1")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RevokeResponse> revokeRoleAssignedToUser(@PathVariable @NotNull Long userRoleId, @Valid @RequestBody RevokeUserRoleRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.revokeRoleAssignedToUser(userRoleId, request));
    }

    @Operation(
            summary = "Re-assign role to user",
            description = "Re-activates a previously inactive role assignment."
    )
    @PatchMapping(value = "/{userRoleId}/reassign", version = "v1")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AssignResponse> reAssignRoleToUser(@PathVariable @NotNull Long userRoleId, @Valid @RequestBody AssignUserRoleRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.reAssignRoleToUser(userRoleId, request));
    }

    @Operation(
            summary = "Get user role assignment by ID",
            description = "Returns the user and role details for the specified user-role assignment."
    )
    @GetMapping(value = "/{userRoleId}", version = "v1")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserRoleResponse> fetchUserRoleById(@PathVariable @NotNull Long userRoleId) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.fetchUserRoleById(userRoleId));
    }

}
