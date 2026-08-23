package com.pranit.docmind.event.dto;

import lombok.Builder;

import java.time.Duration;

@Builder
public record OtpEvent(
        String eventId,
        String email,
        String otp,
        Duration expiresAt
) {
    public OtpEvent {
        if (expiresAt == null) expiresAt = Duration.ofMinutes(5);
    }
}
