package com.pranit.docmind.controller;

import com.pranit.docmind.authorization.dto.RoleResponse;
import com.pranit.docmind.authorization.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Validated
@Tag(name = "Role", description = "Account Roles")
public class RoleController {

    private final RoleService roleService;

    @Operation(
            summary = "Get all roles",
            description = "Returns a roles list."
    )
    @GetMapping(version = "v1")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RoleResponse>> fetchAllRoles() {
        return ResponseEntity.status(HttpStatus.OK).body(roleService.findAllRoles());
    }
}
