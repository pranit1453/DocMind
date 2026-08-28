package com.pranit.docmind.authorization.service;

import com.pranit.docmind.entities.entity.Role;

import java.util.UUID;

@FunctionalInterface
public interface UserRoleService {

    void addRoleToUser(UUID userId, Role role);
}
