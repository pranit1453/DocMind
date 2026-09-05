package com.pranit.docmind.mail.dto;

import com.pranit.docmind.mail.event.EmailEvent;
import lombok.Builder;

@Builder
public record WelcomeEvent(
        String eventId,
        String username,
        String email
) implements EmailEvent {
}
