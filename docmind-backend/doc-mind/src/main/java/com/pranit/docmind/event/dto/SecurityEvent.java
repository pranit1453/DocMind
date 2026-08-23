package com.pranit.docmind.event.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record SecurityEvent(
        String eventId,
        String newMail,
        String oldMail,
        Instant timestamp
) {
}
