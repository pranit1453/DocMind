package com.pranit.docmind.interceptor;

import com.pranit.docmind.interceptor.model.RateLimit;
import com.pranit.docmind.wrapper.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final int MAX_REQUESTS = 5;
    private static final long WINDOW_MS = 60_000L;

    private final Map<String, RateLimit> clients = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        String clientIp = request.getRemoteAddr();
        RateLimit rateLimit = clients.computeIfAbsent(clientIp, key -> new RateLimit());
        synchronized (rateLimit) {
            long now = System.currentTimeMillis();
            // New 60-second window
            if (now - rateLimit.getWindowStart() >= WINDOW_MS) {
                rateLimit.setWindowStart(now);
                rateLimit.setCount(0);
            }
            // Limit exceeded
            if (rateLimit.getCount() >= MAX_REQUESTS) {
                writeTooManyRequests(response, request);
                return false;
            }
            // Count current request
            rateLimit.setCount(rateLimit.getCount() + 1);
            return true;
        }
    }

    private void writeTooManyRequests(@NonNull HttpServletResponse response, @NonNull HttpServletRequest request) {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try {
            final ErrorResponse errorResponse = ErrorResponse.builder()
                    .status(HttpStatus.TOO_MANY_REQUESTS.value())
                    .message("Too many requests. Please try again later.")
                    .path(request.getRequestURI())
                    .timestamp(Instant.now())
                    .build();
            response.getWriter().write(errorResponse.toString());
        } catch (Exception e) {
            log.error("Failed to write rate limit response", e);
        }
    }

    public void reset(String clientIp) {
        clients.remove(clientIp);
    }
}
