package com.pranit.docmind.helper;

import com.pranit.docmind.authentication.exception.UnauthorizedException;
import com.pranit.docmind.entities.model.UserDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

@Slf4j
public final class SecurityContext {

    private SecurityContext() {
    }

    public static UUID getCurrentUserId() {
        final UUID userId = getUserDetail().userId();
        if (userId == null) throw new UnauthorizedException("Authenticated user ID not found");
        return userId;
    }

    public static UserDetail getUserDetail() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            log.warn("User is not authenticated: {}", authentication);
            throw new UnauthorizedException("User not authenticated");
        }
        final Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetail userDetail)) {
            log.warn("Invalid authentication principal: {}", principal);
            throw new UnauthorizedException("Invalid authentication principal");
        }
        return userDetail;
    }
}
