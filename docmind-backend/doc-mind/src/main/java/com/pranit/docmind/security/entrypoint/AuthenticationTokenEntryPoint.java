package com.pranit.docmind.security.entrypoint;

import com.pranit.docmind.security.helper.SecurityResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@NullMarked
public class AuthenticationTokenEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException authException) throws IOException {
        log.debug("Request in Authentication Token Entry Point: {}", request.getRequestURI());
        SecurityResponse.unauthorized(request, response, "Authentication failed: " + authException.getMessage());
    }
}
