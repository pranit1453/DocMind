package com.pranit.docmind.admin.service.impl;

import com.pranit.docmind.admin.dto.UserAccountControlRequest;
import com.pranit.docmind.admin.dto.UserResponse;
import com.pranit.docmind.admin.exception.InvalidAccountStateException;
import com.pranit.docmind.admin.service.AdminService;
import com.pranit.docmind.authentication.exception.UserNotExistsException;
import com.pranit.docmind.authentication.repository.RefreshTokenRepository;
import com.pranit.docmind.authentication.repository.UserRepository;
import com.pranit.docmind.authorization.dto.AssignResponse;
import com.pranit.docmind.authorization.dto.AssignUserRoleRequest;
import com.pranit.docmind.authorization.dto.RevokeResponse;
import com.pranit.docmind.authorization.dto.RevokeUserRoleRequest;
import com.pranit.docmind.authorization.dto.RoleResponse;
import com.pranit.docmind.authorization.dto.RoleResponses;
import com.pranit.docmind.authorization.dto.UserRoleResponse;
import com.pranit.docmind.authorization.exception.ResourceValidationException;
import com.pranit.docmind.authorization.exception.RoleNotFoundException;
import com.pranit.docmind.authorization.repository.RoleRepository;
import com.pranit.docmind.authorization.repository.UserRoleRepository;
import com.pranit.docmind.authorization.service.UserRoleService;
import com.pranit.docmind.entities.constant.RoleStatus;
import com.pranit.docmind.entities.entity.Role;
import com.pranit.docmind.entities.entity.User;
import com.pranit.docmind.entities.entity.UserRole;
import com.pranit.docmind.redis.service.RedisTokenStore;
import com.pranit.docmind.wrapper.ApiResponse;
import com.pranit.docmind.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final RedisTokenStore redisTokenStore;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRoleService userRoleService;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> fetchUsers(int page, int size, String keyword, String sortBy, String sortDirection) {
        final Sort sort = sortDirection.equalsIgnoreCase("ASC")
                ? Sort.by(Sort.Direction.ASC, sortBy)
                : Sort.by(Sort.Direction.DESC, sortBy);
        final Pageable pageable = PageRequest.of(page, size, sort);
        final String searchKeyword = keyword == null ? "" : keyword.trim();
        final Page<User> pages = userRepository.findAllUsers(searchKeyword, pageable);
        final List<UserResponse> content = pages.getContent()
                .stream()
                .map(this::mapToUserResponse)
                .toList();
        return PageResponse.<UserResponse>builder()
                .contents(content)
                .currentPage(pages.getNumber())
                .pageSize(pages.getSize())
                .totalElements(pages.getTotalElements())
                .totalPages(pages.getTotalPages())
                .isLastPage(pages.isLast())
                .isFirstPage(pages.isFirst())
                .build();
    }

    private UserResponse mapToUserResponse(final User user) {
        final List<RoleResponse> roles = user.getUserRoles()
                .stream()
                .map(this::mapToRoleResponse)
                .toList();

        return UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .enabled(user.isEnabled())
                .deleted(user.isDeleted())
                .roles(roles)
                .build();
    }

    private RoleResponse mapToRoleResponse(final UserRole userRole) {
        final Role role = userRole.getRole();
        return RoleResponse.builder()
                .roleId(role.getRoleId())
                .roleName(role.getRoleName())
                .roleDescription(role.getRoleDescription())
                .status(userRole.getStatus())
                .build();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ApiResponse<Void> controlUserAccount(final UUID userId, final UserAccountControlRequest request) {
        final User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotExistsException("User not found"));
        if (user.isEnabled() == request.enabled()) {
            throw new InvalidAccountStateException(request.enabled()
                    ? "User account is already enabled."
                    : "User account is already disabled."
            );
        }
        user.setEnabled(request.enabled());
        String sessionId = redisTokenStore.getTokenIdentifier(userId);
        if (sessionId != null) refreshTokenRepository.revokeAllByUserIdAndSessionId(userId, sessionId);
        redisTokenStore.invalidateUserSession(userId);
        return ApiResponse.<Void>builder()
                .status(true)
                .message(request.enabled()
                        ? "User account successfully enabled."
                        : "User account successfully disabled."
                )
                .data(null)
                .timestamp(Instant.now())
                .build();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public AssignResponse assignRoleToUser(final AssignUserRoleRequest request) {
        final Role role = validateAndFindRoleById(request.roleId());
        userRoleService.addRoleToUser(request.userId(), role);
        invalidateSession(request.userId());
        return AssignResponse.builder()
                .status(true)
                .message("Role Successfully assigned to user")
                .build();
    }

    private Role validateAndFindRoleById(final Long roleId) {
        return roleRepository.findByRoleId(roleId)
                .orElseThrow(() -> {
                    log.warn("No role found for roleId {}", roleId);
                    return new RoleNotFoundException("Role not found");
                });
    }

    private void invalidateSession(final UUID userId) {
        redisTokenStore.invalidateUserSession(userId);
        refreshTokenRepository.revokeAllByUserId(userId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public RevokeResponse revokeRoleAssignedToUser(final Long userRoleId, final RevokeUserRoleRequest request) {
        final UserRole userRole = validateAndFetchUserRoleDetails(userRoleId, request.userId(), request.roleId());
        if (userRole.getStatus() != RoleStatus.ACTIVE) {
            throw new ResourceValidationException("User role assignment cannot be revoked because it is already inactive");
        }
        userRole.revoke();
        invalidateSession(request.userId());
        return RevokeResponse.builder()
                .status(true)
                .message("Role revoked successfully")
                .build();
    }

    private UserRole validateAndFetchUserRoleDetails(final Long userRoleId, UUID userId, Long roleId) {
        return userRoleRepository.findByUserRoleIdAndUser_UserIdAndRole_RoleId(userRoleId, userId, roleId)
                .orElseThrow(() -> new ResourceValidationException("User role assignment not found"));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public AssignResponse reAssignRoleToUser(final Long userRoleId, final AssignUserRoleRequest request) {
        final UserRole userRole = validateAndFetchUserRoleDetails(userRoleId, request.userId(), request.roleId());
        if (userRole.getStatus() == RoleStatus.ACTIVE) {
            throw new ResourceValidationException("User role assignment is already active");
        }
        userRole.reAssign();
        invalidateSession(request.userId());
        return AssignResponse.builder()
                .status(true)
                .message("Role assigned successfully")
                .build();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public UserRoleResponse fetchUserRoleById(final Long userRoleId) {
        final UserRole userRole = userRoleRepository.findByUserRoleId(userRoleId)
                .orElseThrow(() -> {
                    log.warn("No user role found for roleId {}", userRoleId);
                    return new ResourceValidationException("User Role not found");
                });
        final Role role = userRole.getRole();
        final User user = userRole.getUser();

        return UserRoleResponse.builder()
                .userRoleId(userRole.getUserRoleId())
                .userId(user.getUserId())
                .username(user.getUsername())
                .roles(List.of(RoleResponses.builder()
                        .roleId(role.getRoleId())
                        .roleName(role.getRoleName())
                        .roleDescription(role.getRoleDescription())
                        .build()))
                .build();
    }
}
