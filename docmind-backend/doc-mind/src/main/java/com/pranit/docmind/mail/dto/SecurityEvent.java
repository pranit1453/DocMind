package com.pranit.docmind.mail.dto;

import com.pranit.docmind.mail.event.EmailEvent;
import lombok.Builder;

import java.time.Instant;

@Builder
public record SecurityEvent(
        String eventId,
        String newMail,
        String oldMail,
        Instant timestamp
) implements EmailEvent {
}
