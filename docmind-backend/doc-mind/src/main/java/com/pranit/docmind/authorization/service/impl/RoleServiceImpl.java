package com.pranit.docmind.authorization.service.impl;

import com.pranit.docmind.authorization.dto.RoleResponse;
import com.pranit.docmind.authorization.repository.RoleRepository;
import com.pranit.docmind.authorization.service.RoleService;
import com.pranit.docmind.entities.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<RoleResponse> findAllRoles() {
        final List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .map(this::mapToRoleResponse)
                .toList();
    }

    private RoleResponse mapToRoleResponse(final Role role) {
        return RoleResponse.builder()
                .roleId(role.getRoleId())
                .roleName(role.getRoleName())
                .roleDescription(role.getRoleDescription())
                .build();
    }
}
