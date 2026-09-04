package com.pranit.docmind.authentication.service.impl;

import com.pranit.docmind.aop.annotation.LogExecution;
import com.pranit.docmind.authentication.dto.LoginRequest;
import com.pranit.docmind.authentication.dto.LoginRespone;
import com.pranit.docmind.authentication.dto.UserResponse;
import com.pranit.docmind.authentication.exception.UnauthorizedException;
import com.pranit.docmind.authentication.repository.RefreshTokenRepository;
import com.pranit.docmind.authentication.repository.UserRepository;
import com.pranit.docmind.authentication.service.LoginService;
import com.pranit.docmind.authentication.service.RestoreUserAccount;
import com.pranit.docmind.constant.UserMetadata;
import com.pranit.docmind.entities.entity.RefreshToken;
import com.pranit.docmind.entities.entity.User;
import com.pranit.docmind.entities.model.UserDetail;
import com.pranit.docmind.helper.Generate;
import com.pranit.docmind.helper.SecurityContext;
import com.pranit.docmind.interceptor.RateLimitInterceptor;
import com.pranit.docmind.properties.TokenProperties;
import com.pranit.docmind.security.service.CookieService;
import com.pranit.docmind.security.service.GenerateToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final AuthenticationManager authenticationManager;
    private final TokenProperties tokenProperties;
    private final RefreshTokenRepository refreshTokenRepository;
    private final GenerateToken generateToken;
    private final CookieService cookieService;
    private final UserRepository userRepository;
    private final RateLimitInterceptor rateLimitInterceptor;
    private final RestoreUserAccount restoreUserAccount;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    @LogExecution
    public LoginRespone authenticateUser(final LoginRequest request, final HttpServletRequest httpRequest, final HttpServletResponse response) {
        log.debug("Authentication attempt initiated for username: {}", request.username());
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate
                    (UsernamePasswordAuthenticationToken.unauthenticated
                            (request.username(), request.password()));
        } catch (BadCredentialsException exception) {
            throw new BadCredentialsException("Invalid username or password");
        }
        final Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetail userDetail)) {
            log.warn("Invalid authentication principal returned for username: {}", request.username());
            throw new UnauthorizedException("Invalid authentication principal");
        }
        log.debug("Authentication successful username: {} userId: {}", userDetail.username(), userDetail.userId());
        UserDetail authenticateUserDetail = userDetail;
        if (userDetail.deleted()) {
            restoreUserAccount.restoreUserAccount(userDetail.userId());
            authenticateUserDetail = UserDetail.builder()
                    .userId(userDetail.userId())
                    .username(userDetail.username())
                    .password(userDetail.password())
                    .authorities(userDetail.authorities())
                    .deleted(false)
                    .enabled(userDetail.enabled())
                    .build();
        }
        final Map<Boolean, Set<String>> grouped = authenticateUserDetail.authorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(Objects::nonNull)
                .collect(Collectors.partitioningBy(
                        a -> a.startsWith(UserMetadata.ROLE),
                        Collectors.toUnmodifiableSet()));
        final Set<String> roles = grouped.get(true);
        final String jti = Generate.generateJti();
        final Instant now = Instant.now();
        final User user = userRepository.getReferenceById(authenticateUserDetail.userId());
        final String sessionId = Generate.generateSessionId();
        final RefreshToken rt = RefreshToken.builder()
                .jti(jti)
                .sessionId(sessionId)
                .user(user)
                .expiresAt(now.plus(Duration.ofSeconds(tokenProperties.refreshToken().expiration())))
                .revoked(false)
                .build();
        refreshTokenRepository.save(rt);
        final String accessToken = generateToken.generateAccessToken(authenticateUserDetail, sessionId);
        final String refreshToken = generateToken.generateRefreshToken(authenticateUserDetail, jti, sessionId);
        final Duration accessTokenTtl = Duration.ofSeconds(tokenProperties.accessToken().expiration());
        final Duration refreshTokenTtl = Duration.ofSeconds(tokenProperties.refreshToken().expiration());
        cookieService.attachAccessTokenCookie(response, accessToken, accessTokenTtl);
        cookieService.attachRefreshTokenCookie(response, refreshToken, refreshTokenTtl);
        cookieService.addNoStoreHeaders(response);
        rateLimitInterceptor.reset(httpRequest.getRemoteAddr());
        log.info("User logged in successfully userId: {}", authenticateUserDetail.userId());
        return LoginRespone.builder()
                .username(authenticateUserDetail.username())
                .roles(roles)
                .build();
    }

    @Override
    @LogExecution
    public UserResponse getCurrentUser() {
        final UserDetail principal = SecurityContext.getUserDetail();
        return UserResponse.builder()
                .userId(principal.userId())
                .username(principal.username())
                .fullName(principal.fullName())
                .email(principal.email())
                .enabled(principal.enabled())
                .deleted(principal.deleted())
                .build();
    }
}
