package com.pranit.docmind.authentication.service.impl;

import com.pranit.docmind.aop.annotation.LogExecution;
import com.pranit.docmind.authentication.dto.TokenResponse;
import com.pranit.docmind.authentication.exception.AccountDeletedException;
import com.pranit.docmind.authentication.exception.TokenExpiredException;
import com.pranit.docmind.authentication.exception.TokenOwnerException;
import com.pranit.docmind.authentication.exception.UnauthorizedException;
import com.pranit.docmind.authentication.repository.RefreshTokenRepository;
import com.pranit.docmind.authentication.service.TokenService;
import com.pranit.docmind.authentication.service.UserDetailService;
import com.pranit.docmind.entities.entity.RefreshToken;
import com.pranit.docmind.entities.model.UserDetail;
import com.pranit.docmind.helper.Generate;
import com.pranit.docmind.properties.CookieProperties;
import com.pranit.docmind.properties.TokenProperties;
import com.pranit.docmind.redis.service.RedisTokenStore;
import com.pranit.docmind.security.service.CookieService;
import com.pranit.docmind.security.service.ExtractClaim;
import com.pranit.docmind.security.service.GenerateToken;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final GenerateToken generateToken;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ExtractClaim extractClaim;
    private final CookieService cookieService;
    private final CookieProperties cookieProperties;
    private final TokenProperties tokenProperties;
    private final UserDetailService userDetailService;
    private final RedisTokenStore redisTokenStore;

    @Override
    @LogExecution
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
    @LogExecution
    public TokenResponse generateNewRefreshToken(final String refreshToken, final HttpServletResponse response) {
        final Claims claims = extractClaim.validateAndParseToken(refreshToken);
        if (!extractClaim.isRefreshToken(claims)) {
            log.warn("Invalid refresh token: {}", refreshToken);
            throw new UnauthorizedException("Invalid token");
        }
        final String jti = extractClaim.getJtiFromRefreshToken(claims);
        final UUID userId = extractClaim.getUserIdFromRefreshToken(claims);
        final String sessionId = extractClaim.getSessionIdFromRefeshToken(claims);
        if (jti == null || jti.isBlank()) {
            log.warn("Refresh token has no JTI");
            throw new UnauthorizedException("Invalid refresh token");
        }
        if (userId == null) {
            log.warn("Refresh token has no userId jti: {}", jti);
            throw new UnauthorizedException("Invalid refresh token");
        }
        if (sessionId == null || sessionId.isBlank()) {
            log.warn("Refresh token has no sessionId jti: {}", jti);
            throw new UnauthorizedException("Invalid refresh token");
        }

        log.debug("Refresh request userId: {} | jti: {} | sessionId: {}", userId, jti, sessionId);

        final RefreshToken existingToken = refreshTokenRepository.findByJtiForUpdate(jti)
                .orElseThrow(() -> {
                    log.warn("Refresh token not found jti: {}", jti);
                    return new UnauthorizedException("Invalid refresh token");
                });

        if (existingToken.isRevoked()) {
            log.warn("Refresh token reuse detected userId: {} | jti: {} | sessionId: {}", userId, jti, sessionId);
            refreshTokenRepository.revokeAllByUserIdAndSessionId(userId, sessionId);
            // Remove session id from Redis
            redisTokenStore.invalidateUserSession(userId);
            throw new UnauthorizedException("Token reuse detected");
        }

        final Instant now = Instant.now();

        if (existingToken.getExpiresAt().isAfter(now)) {
            existingToken.setRevoked(true);
            log.debug("Refresh token expired userId: {} | jti: {}", userId, jti);
            throw new TokenExpiredException("Refresh token expired");
        }

        if (existingToken.getUser() == null) {
            log.error("Refresh token has no user relation jti: {}", jti);
            throw new UnauthorizedException("Invalid refresh token");
        }

        if (!existingToken.getUser().getUserId().equals(userId)) {
            log.warn("Refresh token owner mismatch");
            throw new TokenOwnerException("Invalid refresh token");
        }

        if (!existingToken.getSessionId().equals(sessionId)) {
            log.warn("Refresh token session mismatch");
            throw new UnauthorizedException("Invalid session");
        }

        final UserDetail userDetail = userDetailService.loadUserByUserId(userId);

        if (userDetail == null) {
            log.warn("User not found userId: {} | jti: {}", userId, jti);
            throw new UnauthorizedException("Invalid refresh token");
        }

        if (userDetail.deleted()) {
            log.warn("Deleted account attempted refresh userId: {}", userId);
            refreshTokenRepository.revokeAllByUserIdAndSessionId(userId, sessionId);
            redisTokenStore.invalidateUserSession(userId);
            throw new AccountDeletedException("Account has been deleted");
        }

        final String newJti = Generate.generateJti();

        final Duration accessTokenTtl = Duration.ofSeconds(tokenProperties.accessToken().expiration());
        final Duration refreshTokenTtl = Duration.ofSeconds(tokenProperties.refreshToken().expiration());

        final String newAccessToken = generateToken.generateAccessToken(userDetail, sessionId);
        final String newRefreshToken = generateToken.generateRefreshToken(userDetail, newJti, sessionId);

        existingToken.setRevoked(true);
        existingToken.setReplacedByToken(newJti);

        final RefreshToken rotatedToken = RefreshToken.builder()
                .jti(newJti)
                .sessionId(existingToken.getSessionId())
                .user(existingToken.getUser())
                .expiresAt(now.plus(refreshTokenTtl))
                .revoked(false)
                .build();
        refreshTokenRepository.save(rotatedToken);

        cookieService.attachAccessTokenCookie(response, newAccessToken, accessTokenTtl);
        cookieService.attachRefreshTokenCookie(response, newRefreshToken, refreshTokenTtl);
        cookieService.addNoStoreHeaders(response);

        log.info("Refresh token rotated successfully userId: {} | sessionId: {} | oldJti: {} | newJti: {}", userId, sessionId, jti, newJti);

        return TokenResponse.builder()
                .message("Token refreshed successfully")
                .build();
    }
}
