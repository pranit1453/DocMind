package com.pranit.docmind.authorization.service.impl;

import com.pranit.docmind.authentication.exception.UserNotExistsException;
import com.pranit.docmind.authentication.repository.UserRepository;
import com.pranit.docmind.authorization.dto.AssignResponse;
import com.pranit.docmind.authorization.dto.AssignUserRoleRequest;
import com.pranit.docmind.authorization.dto.RevokeResponse;
import com.pranit.docmind.authorization.dto.RevokeUserRoleRequest;
import com.pranit.docmind.authorization.dto.UserRoleResponse;
import com.pranit.docmind.authorization.exception.RoleAlreadyAssignedException;
import com.pranit.docmind.authorization.repository.UserRoleRepository;
import com.pranit.docmind.authorization.service.UserRoleService;
import com.pranit.docmind.entities.constant.RoleStatus;
import com.pranit.docmind.entities.entity.Role;
import com.pranit.docmind.entities.entity.User;
import com.pranit.docmind.entities.entity.UserRole;
import com.pranit.docmind.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void addRoleToUser(final UUID userId, final Role role) {
        final User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("No user found for userId {}", userId);
                    return new UserNotExistsException("User not found");
                });
        if (userRoleRepository.existsByUser_UserIdAndRole_RoleId(userId, role.getRoleId())) {
            log.debug("Role '{}' already assigned to userId: {}", role.getRoleName(), userId);
            throw new RoleAlreadyAssignedException("Role already assigned");
        }
        final UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .status(RoleStatus.ACTIVE)
                .build();
        userRoleRepository.save(userRole);
        log.info("Assigned role '{}' to userId: {}", role.getRoleName(), userId);
    }

    @Override
    public AssignResponse assignRoleToUser(AssignUserRoleRequest request) {
        return null;
    }

    @Override
    public RevokeResponse revokeRoleAssignedToUser(Long userRoleId, RevokeUserRoleRequest request) {
        return null;
    }

    @Override
    public AssignResponse reAssignRoleToUser(Long userRoleId, AssignUserRoleRequest request) {
        return null;
    }

    @Override
    public UserRoleResponse fetchUserRoleById(Long userRoleId) {
        return null;
    }

    @Override
    public PageResponse<UserRoleResponse> findAllRolesForParticularUser(int page, int size, String keyword, String sortBy, String sortDirection) {
        return null;
    }
}
