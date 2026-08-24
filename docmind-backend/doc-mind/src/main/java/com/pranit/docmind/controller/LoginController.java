package com.pranit.docmind.controller;

import com.pranit.docmind.authentication.dto.LoginRequest;
import com.pranit.docmind.authentication.dto.LoginRespone;
import com.pranit.docmind.authentication.dto.TokenResponse;
import com.pranit.docmind.authentication.exception.UnauthorizedException;
import com.pranit.docmind.authentication.service.LoginService;
import com.pranit.docmind.authentication.service.LogoutService;
import com.pranit.docmind.authentication.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Authentication",
        description = "Endpoints for user authentication, token refresh, and logout."
)
public class LoginController {

    private final LoginService loginService;
    private final TokenService tokenService;
    private final LogoutService logoutService;

    /**
     * api authenticateUser(username,password) --> call authentication manager --> call authentication provider --> call userDetailService --> call database to fetch data
     * once data is validated then security context holder confirms that request is authenticated
     * after that Access token is generated for client and send back to client
     * in HttpOnlyCookie
     */
    @Operation(
            summary = "Authenticate user",
            description = "Authenticates the user and sets the access and refresh tokens in secure HTTP-only cookies."
    )
    @PostMapping(value = "/login", version = "v1")
    public ResponseEntity<LoginRespone> authenticateUser(@RequestBody @Valid LoginRequest request, @Valid HttpServletRequest httpRequest, @Valid HttpServletResponse response) {
        final LoginRespone loginRespone = loginService.authenticateUser(request, httpRequest, response);
        return ResponseEntity.status(HttpStatus.OK).body(loginRespone);
    }

    @Operation(
            summary = "Refresh access token",
            description = "Validates the refresh token and generates a new access and refresh token."
    )
    @PostMapping(value = "/refresh", version = "v1")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<TokenResponse> refreshToken(final HttpServletRequest request, final HttpServletResponse response) {
        final String refreshToken = tokenService.readRefreshTokenFromRequest(request)
                .orElseThrow(() -> {
                    log.warn("No refresh token found");
                    return new UnauthorizedException("No refresh token found");
                });
        final TokenResponse tokenResponse = tokenService.generateNewRefreshToken(refreshToken, response);
        return ResponseEntity.status(HttpStatus.OK)
                .body(tokenResponse);
    }

    @Operation(
            summary = "Logout user",
            description = "Revokes the user's access and refresh tokens and clears the authentication cookies."
    )
    @PostMapping(value = "/logout", version = "v1")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> logout(@Valid HttpServletRequest request, @Valid HttpServletResponse response) {
        logoutService.readRefreshTokenFromRequest(request)
                .ifPresent(logoutService::revokedRefreshToken);
        logoutService.readAccessTokenFromRequest(request)
                .ifPresent(logoutService::revokedAccessToken);
        logoutService.clearResponse(response);
        log.info("User logged out successfully");
        return ResponseEntity.ok("Logout successful");
    }

}
