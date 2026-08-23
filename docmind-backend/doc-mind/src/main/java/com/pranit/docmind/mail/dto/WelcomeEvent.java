package com.pranit.docmind.mail.dto;

import lombok.Builder;

@Builder
public record WelcomeEvent(
        String eventId,
        String username,
        String email
) {
}
