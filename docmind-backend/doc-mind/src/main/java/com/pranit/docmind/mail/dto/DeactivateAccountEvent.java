package com.pranit.docmind.mail.dto;

import com.pranit.docmind.mail.event.EmailEvent;
import lombok.Builder;

import java.time.Instant;

@Builder
public record DeactivateAccountEvent(
        String eventId,
        String username,
        String email,
        Instant timestamp
) implements EmailEvent {
}
