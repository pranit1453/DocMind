package com.pranit.docmind.controller;

import com.pranit.docmind.authorization.dto.AssignResponse;
import com.pranit.docmind.authorization.dto.AssignUserRoleRequest;
import com.pranit.docmind.authorization.dto.RevokeResponse;
import com.pranit.docmind.authorization.dto.RevokeUserRoleRequest;
import com.pranit.docmind.authorization.dto.UserRoleResponse;
import com.pranit.docmind.authorization.service.UserRoleService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user-roles")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "User Role Management",
        description = "APIs for assigning, revoking, re-assigning and managing user roles"
)
public class UserRoleController {

    private final UserRoleService userRoleService;

    @Operation(
            summary = "Assign role to user",
            description = "Assigns a role to the specified user."
    )
    @PostMapping(value = "/assign", version = "v1")
    public ResponseEntity<AssignResponse> assignRoleToUser(@Valid @RequestBody AssignUserRoleRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(userRoleService.assignRoleToUser(request));
    }

    @Operation(
            summary = "Revoke role assigned to user",
            description = "Revokes an active role assignment from a user."
    )
    @PatchMapping(value = "/{userRoleId}/revoke", version = "v1")
    public ResponseEntity<RevokeResponse> revokeRoleAssignedToUser(@PathVariable @NotNull Long userRoleId, @Valid @RequestBody RevokeUserRoleRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(userRoleService.revokeRoleAssignedToUser(userRoleId, request));
    }

    @Operation(
            summary = "Re-assign role to user",
            description = "Re-activates a previously inactive role assignment."
    )
    @PatchMapping(value = "/{userRoleId}/reassign", version = "v1")
    public ResponseEntity<AssignResponse> reAssignRoleToUser(@PathVariable @NotNull Long userRoleId, @Valid @RequestBody AssignUserRoleRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(userRoleService.reAssignRoleToUser(userRoleId, request));
    }

    @Operation(
            summary = "Get user role assignment by ID",
            description = "Returns the user and role details for the specified user-role assignment."
    )
    @GetMapping(value = "/{userRoleId}", version = "v1")
    public ResponseEntity<UserRoleResponse> fetchUserRoleById(@PathVariable @NotNull Long userRoleId) {
        return ResponseEntity.status(HttpStatus.OK).body(userRoleService.fetchUserRoleById(userRoleId));
    }

    @Operation(
            summary = "Get all user role assignments",
            description = " Returns a paginated list of user-role assignments."
    )
    @GetMapping
    public ResponseEntity<PageResponse<UserRoleResponse>> findAllUserRoles(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(10) int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(userRoleService.findAllRolesForParticularUser(page, size, keyword, sortBy, sortDirection));
    }
}
