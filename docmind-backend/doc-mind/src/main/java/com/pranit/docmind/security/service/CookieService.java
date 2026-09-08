package com.pranit.docmind.security.service;

import jakarta.servlet.http.HttpServletResponse;

import java.time.Duration;

public interface CookieService {

    void attachAccessTokenCookie(HttpServletResponse response, String value, Duration maxAge);

    void attachRefreshTokenCookie(HttpServletResponse response, String value, Duration maxAge);

    void clearAccessTokenCookie(HttpServletResponse response);

    void clearRefreshTokenCookie(HttpServletResponse response);

    void addNoStoreHeaders(HttpServletResponse response);
}
