package com.pranit.docmind.authorization.service.impl;

import com.pranit.docmind.aop.annotation.LogExecution;
import com.pranit.docmind.authentication.exception.UserNotExistsException;
import com.pranit.docmind.authentication.repository.UserRepository;
import com.pranit.docmind.authorization.exception.RoleAlreadyAssignedException;
import com.pranit.docmind.authorization.repository.UserRoleRepository;
import com.pranit.docmind.authorization.service.UserRoleService;
import com.pranit.docmind.entities.constant.RoleStatus;
import com.pranit.docmind.entities.entity.Role;
import com.pranit.docmind.entities.entity.User;
import com.pranit.docmind.entities.entity.UserRole;
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
    @LogExecution
    public void addRoleToUser(final UUID userId, final Role role) {
        final User user = validateAndFindUserById(userId);
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

    private User validateAndFindUserById(final UUID userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("No user found for userId {}", userId);
                    return new UserNotExistsException("User not found");
                });
    }
}
