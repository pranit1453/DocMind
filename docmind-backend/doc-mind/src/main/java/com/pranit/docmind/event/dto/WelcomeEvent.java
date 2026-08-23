package com.pranit.docmind.event.dto;

import lombok.Builder;

@Builder
public record WelcomeEvent(
        String eventId,
        String username,
        String email
) {
}
