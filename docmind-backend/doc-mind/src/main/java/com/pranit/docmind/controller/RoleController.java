package com.pranit.docmind.controller;

import com.pranit.docmind.authorization.dto.RoleResponse;
import com.pranit.docmind.authorization.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Validated
public class RoleController {

    private final RoleService roleService;

    @GetMapping(version = "v1")
    public ResponseEntity<List<RoleResponse>> fetchAllRoles() {
        return ResponseEntity.status(HttpStatus.OK).body(roleService.findAllRoles());
    }
}
