package com.pranit.docmind.mail.dto;

import com.pranit.docmind.mail.event.EmailEvent;
import lombok.Builder;

import java.time.Duration;

@Builder
public record OtpEvent(
        String eventId,
        String email,
        String otp,
        Duration expiresAt
) implements EmailEvent {
    public OtpEvent {
        if (expiresAt == null) expiresAt = Duration.ofMinutes(5);
    }
}
