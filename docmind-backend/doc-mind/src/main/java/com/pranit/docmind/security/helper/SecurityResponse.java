package com.pranit.docmind.security.helper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public final class SecurityResponse {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private SecurityResponse() {
    }

    public static void unauthorized(final HttpServletRequest request, final HttpServletResponse response, final String message) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 401);
        body.put("error", "Unauthorized");
        body.put("message", message);
        body.put("path", request.getRequestURI());

        MAPPER.writeValue(response.getOutputStream(), body);
    }
}
