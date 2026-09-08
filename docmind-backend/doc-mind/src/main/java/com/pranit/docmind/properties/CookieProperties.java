package com.pranit.docmind.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.cookie")
public record CookieProperties(
        Name name,
        boolean cookieHttpOnly,
        boolean cookieSecure,
        String cookieDomain,
        String cookieSameSite
) {
    public record Name(String refreshTokenName, String accessTokenName) {
    }
}
