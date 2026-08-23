package com.pranit.docmind.authentication.service.impl;

import com.pranit.docmind.authentication.repository.RefreshTokenRepository;
import com.pranit.docmind.authentication.service.LogoutService;
import com.pranit.docmind.entities.entity.RefreshToken;
import com.pranit.docmind.properties.CookieProperties;
import com.pranit.docmind.redis.service.RedisTokenStore;
import com.pranit.docmind.security.service.CookieService;
import com.pranit.docmind.security.service.ExtractClaim;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutServiceImpl implements LogoutService {

    private final CookieProperties cookieProperties;
    private final ExtractClaim extractClaim;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RedisTokenStore redisTokenStore;
    private final CookieService cookieService;

    @Override
    public Optional<String> readRefreshTokenFromRequest(final HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) return Optional.empty();
        return Arrays.stream(cookies)
                .filter(cookie -> cookieProperties.name().refreshTokenName().equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(value -> value != null && !value.isBlank())
                .findFirst();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void revokedRefreshToken(final String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) return;
        final Claims claims;
        try {
            claims = extractClaim.validateAndParseToken(refreshToken);
        } catch (JwtException ex) {
            log.debug("Invalid refresh token during logout");
            return;
        }
        if (!extractClaim.isRefreshToken(claims)) {
            log.debug("Non-refresh token supplied during logout");
            return;
        }
        final String jti = extractClaim.getJtiFromRefreshToken(claims);
        final UUID userId = extractClaim.getUserIdFromRefreshToken(claims);
        final String sessionId = extractClaim.getSessionIdFromRefeshToken(claims);

        if (jti == null || jti.isBlank()) return;
        if (userId == null) return;
        if (sessionId == null || sessionId.isBlank()) return;

        final RefreshToken token = refreshTokenRepository.findByJtiForUpdate(jti).orElse(null);

        if (token == null) {
            log.debug("Refresh token not found during logout jti: {}", jti);
            return;
        }

        if (token.getUser() == null || !token.getUser().getUserId().equals(userId)) {
            log.warn("Refresh token owner mismatch during logout jti: {} | userId: {}", jti, userId);
            return;
        }

        if (!token.getSessionId().equals(sessionId)) {
            log.warn("Refresh token session mismatch during logout jti: {} | userId: {}", jti, userId);
            return;
        }

        refreshTokenRepository.revokeAllByUserIdAndSessionId(userId, sessionId);
        redisTokenStore.invalidateUserSession(userId);

        log.info("Logout successful userId: {} | sessionId: {} ", userId, sessionId);
    }

    @Override
    public Optional<String> readAccessTokenFromRequest(final HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) return Optional.empty();
        return Arrays.stream(cookies)
                .filter(cookie -> cookieProperties.name().accessTokenName().equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(value -> value != null && !value.isBlank())
                .findFirst();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void revokedAccessToken(final String accessToken) {
        if (accessToken == null || accessToken.isBlank()) return;
        final Claims claims;
        try {
            claims = extractClaim.validateAndParseToken(accessToken);
        } catch (JwtException ex) {
            log.debug("Invalid access token during logout");
            return;
        }
        if (!extractClaim.isAccessToken(claims)) {
            log.debug("Non-access token supplied during logout");
            return;
        }
        final UUID userId = extractClaim.getUserIdFromAccessToken(claims);
        final String sessionId = extractClaim.getSessionIdFromAccessToken(claims);

        if (userId == null || sessionId == null || sessionId.isBlank()) return;
        refreshTokenRepository.revokeAllByUserIdAndSessionId(userId, sessionId);
        redisTokenStore.invalidateUserSession(userId);

        log.info("Access session revoked userId: {} | sessionId: {}", userId, sessionId);
    }

    @Override
    public void clearResponse(final HttpServletResponse response) {
        cookieService.clearAccessTokenCookie(response);
        cookieService.clearRefreshTokenCookie(response);
        cookieService.addNoStoreHeaders(response);
        SecurityContextHolder.clearContext();
    }
}
