package com.pranit.docmind.authentication.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.util.Optional;

public interface LogoutService {

    Optional<String> readRefreshTokenFromRequest(@Valid HttpServletRequest request);

    void revokedRefreshToken(String refreshToken);

    Optional<String> readAccessTokenFromRequest(@Valid HttpServletRequest request);

    void revokedAccessToken(String accessToken);

    void clearResponse(@Valid HttpServletResponse response);
}
