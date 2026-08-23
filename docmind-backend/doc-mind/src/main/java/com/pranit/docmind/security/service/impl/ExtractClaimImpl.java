package com.pranit.docmind.security.service.impl;

import com.pranit.docmind.properties.KeysPath;
import com.pranit.docmind.security.constant.JwtClaim;
import com.pranit.docmind.security.helper.LoadKey;
import com.pranit.docmind.security.service.ExtractClaim;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.UUID;

@Slf4j
@Service
public final class ExtractClaimImpl implements ExtractClaim {

    private final PublicKey publicKey;

    public ExtractClaimImpl(KeysPath keysPath) {
        this.publicKey = LoadKey.loadPublicKey(keysPath.publicKeyPath());
    }

    @Override
    public String getUsernameFromAccessToken(final Claims claims) {
        return claims.getSubject();
    }

    @Override
    public UUID getUserIdFromRefreshToken(final Claims claims) {
        final String userId = claims.getId();
        return UUID.fromString(userId);
    }

    @Override
    public UUID getUserIdFromAccessToken(final Claims claims) {
        final String userId = claims.getId();
        return UUID.fromString(userId);
    }

    @Override
    public String getJtiFromRefreshToken(final Claims claims) {
        return claims.getId();
    }

    @Override
    public Claims validateAndParseToken(final String token) {
        try {
            return Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            log.error("JWT validation failed: {}", ex.getMessage());
            throw new JwtException("Invalid or expired JWT", ex);
        }
    }

    @Override
    public boolean isRefreshToken(final Claims claims) {
        return JwtClaim.REFRESH.equals(claims.get(JwtClaim.TOKEN_TYPE, String.class));
    }

    @Override
    public boolean isAccessToken(final Claims claims) {
        return JwtClaim.ACCESS.equals(claims.get(JwtClaim.TOKEN_TYPE, String.class));
    }

    @Override
    public String getSessionIdFromAccessToken(Claims claims) {
        return claims.get(JwtClaim.SESSIONID, String.class);
    }

    @Override
    public String getSessionIdFromRefeshToken(Claims claims) {
        return claims.get(JwtClaim.SESSIONID, String.class);
    }
}
