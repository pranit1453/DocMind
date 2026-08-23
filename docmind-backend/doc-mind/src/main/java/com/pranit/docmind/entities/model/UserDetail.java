package com.pranit.docmind.entities.model;

import lombok.Builder;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

@Builder
public record UserDetail(
        UUID userId,
        String username,
        String password,
        Collection<? extends GrantedAuthority> authorities,
        boolean deleted,
        boolean enabled
) implements UserDetails {
    @Override
    public @NullMarked Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return this.password;
    }

    @Override
    public @NullMarked String getUsername() {
        return this.username;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
