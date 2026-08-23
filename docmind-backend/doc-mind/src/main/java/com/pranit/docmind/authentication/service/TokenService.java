package com.pranit.docmind.authentication.service;

import com.pranit.docmind.authentication.dto.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

public interface TokenService {

    Optional<String> readRefreshTokenFromRequest(HttpServletRequest request);

    TokenResponse generateNewRefreshToken(String refreshToken, HttpServletResponse response);
}
