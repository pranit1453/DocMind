package com.pranit.docmind.mail.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record DeactivateAccountEvent(
        String eventId,
        String username,
        String email,
        Instant timestamp
) {
}
