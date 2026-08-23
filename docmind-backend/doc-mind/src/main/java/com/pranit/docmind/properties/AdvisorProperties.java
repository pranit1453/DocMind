package com.pranit.docmind.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "ai.advisor")
public record AdvisorProperties(
        SafeGuardProperties safeguard
) {
    public record SafeGuardProperties(
            List<String> blockedWords
    ) {
    }
}