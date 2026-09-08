package com.pranit.docmind.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security")
public record TokenProperties(
        AccessToken accessToken,
        RefreshToken refreshToken,
        Token token
) {
    public record AccessToken(long expiration) {
    }

    public record RefreshToken(long expiration) {
    }

    public record Token(String issuer) {
    }
}
