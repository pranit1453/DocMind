package com.pranit.docmind.admin.service.impl;

import com.pranit.docmind.admin.dto.UserAccountControlRequest;
import com.pranit.docmind.admin.dto.UserResponse;
import com.pranit.docmind.admin.exception.InvalidAccountStateException;
import com.pranit.docmind.admin.service.AdminService;
import com.pranit.docmind.admin.specification.UserRoleSpecification;
import com.pranit.docmind.authentication.exception.UserNotExistsException;
import com.pranit.docmind.authentication.repository.RefreshTokenRepository;
import com.pranit.docmind.authentication.repository.UserRepository;
import com.pranit.docmind.authorization.repository.UserRoleRepository;
import com.pranit.docmind.entities.entity.User;
import com.pranit.docmind.entities.entity.UserRole;
import com.pranit.docmind.redis.service.RedisTokenStore;
import com.pranit.docmind.wrapper.ApiResponse;
import com.pranit.docmind.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final RedisTokenStore redisTokenStore;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> fetchUsers(int page, int size, String keyword, String sortBy, String sortDirection) {
        final Sort sort = sortDirection.equalsIgnoreCase("ASC")
                ? Sort.by(Sort.Direction.ASC, sortBy)
                : Sort.by(Sort.Direction.DESC, sortBy);
        final Pageable pageable = PageRequest.of(page, size, sort);
        final Specification<UserRole> specification = UserRoleSpecification.searchKeyword(keyword);
        final Page<UserRole> pages = userRoleRepository.findAll(specification, pageable);
        final List<UserResponse> contents = pages.getContent()
                .stream()
                .map(ur -> UserResponse.builder()
                        .userId(ur.getUser().getUserId())
                        .username(ur.getUser().getUsername())
                        .roleName(ur.getRole().getRoleName())
                        .email(ur.getUser().getEmail())
                        .enabled(ur.getUser().isEnabled())
                        .deleted(ur.getUser().isDeleted())
                        .status(ur.getStatus())
                        .build())
                .toList();
        return PageResponse.<UserResponse>builder()
                .contents(contents)
                .currentPage(pages.getNumber())
                .pageSize(pages.getSize())
                .totalElements(pages.getTotalElements())
                .totalPages(pages.getTotalPages())
                .isLastPage(pages.isLast())
                .isFirstPage(pages.isFirst())
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
}
