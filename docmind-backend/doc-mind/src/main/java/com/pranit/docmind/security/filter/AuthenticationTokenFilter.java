package com.pranit.docmind.security.filter;

import com.pranit.docmind.authentication.service.UserDetailService;
import com.pranit.docmind.redis.service.RedisTokenStore;
import com.pranit.docmind.security.endpoints.PublicEndpointProvider;
import com.pranit.docmind.security.helper.SecurityResponse;
import com.pranit.docmind.security.service.ExtractClaim;
import com.pranit.docmind.security.service.ResolveAccessToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@NullMarked
public class AuthenticationTokenFilter extends OncePerRequestFilter {

    private final ExtractClaim extractClaim;
    private final PublicEndpointProvider publicEndpointProvider;
    private final RedisTokenStore redisTokenStore;
    private final UserDetailService userDetailService;
    private final ResolveAccessToken resolveAccessToken;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        final String path = request.getRequestURI();
        return Arrays.stream(publicEndpointProvider.publicEndpoints())
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    /**
     * If Token is Valid then authentication Object is set in Security Context holder where it holds status where client is authenticated or unauthenticated
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        log.debug("Authenticating request: {}", request.getRequestURI());
        try {
            final String accessToken = resolveAccessToken.getAccessTokenFromRequest(request);
            if (accessToken == null) {
                log.debug("No access token found for request: {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }
            Claims claims = extractClaim.validateAndParseToken(accessToken);
            if (!extractClaim.isAccessToken(claims)) {
                log.debug("Token is invalid for request: {}", request.getRequestURI());
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }

            final UUID userId = extractClaim.getUserIdFromAccessToken(claims);
            final String sessionId = extractClaim.getSessionIdFromAccessToken(claims);

            if (!redisTokenStore.verifyIdentifier(userId, sessionId)) {
                SecurityResponse.unauthorized(request, response,
                        "Your session is no longer valid. Please log in again.");
                return;
            }

            final Authentication existingAuthentication = SecurityContextHolder.getContext().getAuthentication();
            if (existingAuthentication != null) {
                log.debug("Authentication already exists for request: {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }
            final String username = extractClaim.getUsernameFromAccessToken(claims);
            if (username == null) {
                log.debug("Username is null for request: {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }
            final UserDetails details = userDetailService.loadUserByUsername(username);
            if (!isUserEligibleForAuthentication(details)) {
                log.debug("User is not eligible for authentication: {}", username);
                filterChain.doFilter(request, response);
                return;
            }
            authenticate(request, details);
            log.debug("Successfully authenticated user: {}", username);
        } catch (UsernameNotFoundException e) {
            SecurityContextHolder.clearContext();
            log.warn("User not found");
            SecurityResponse.unauthorized(request, response, "Invalid authentication token.");
            return;
        } catch (JwtException e) {
            SecurityContextHolder.clearContext();
            log.warn("Invalid token");
            SecurityResponse.unauthorized(request, response, "Invalid or expired access token.");
            return;
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            log.error("Authentication Error: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Authentication failed.");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean isUserEligibleForAuthentication(final UserDetails userDetails) {
        return userDetails.isEnabled()
                && userDetails.isAccountNonExpired()
                && userDetails.isAccountNonLocked()
                && userDetails.isCredentialsNonExpired();
    }

    private void authenticate(final HttpServletRequest request, final UserDetails details) {
        UsernamePasswordAuthenticationToken authentication =
                UsernamePasswordAuthenticationToken.authenticated(
                        details, null, details.getAuthorities()
                );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
