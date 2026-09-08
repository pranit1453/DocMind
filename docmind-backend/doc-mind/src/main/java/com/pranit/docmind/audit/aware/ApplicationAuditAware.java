package com.pranit.docmind.audit.aware;

import com.pranit.docmind.entities.model.UserDetail;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@NullMarked
public final class ApplicationAuditAware implements AuditorAware<UUID> {

    @Override
    public Optional<UUID> getCurrentAuditor() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        final Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetail userDetail)) {
            log.warn("Invalid authentication principal: {}", principal);
            return Optional.empty();
        }
        return Optional.ofNullable(userDetail.userId());
    }
}
