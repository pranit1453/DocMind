package com.pranit.docmind.mail.email.handler;

import com.pranit.docmind.entities.constant.EmailPurpose;
import com.pranit.docmind.mail.dto.WelcomeEvent;
import com.pranit.docmind.mail.email.EmailHandler;
import com.pranit.docmind.mail.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WelcomeUserEmailHandler implements EmailHandler<WelcomeEvent> {

    private final EmailService emailService;

    @Override
    public EmailPurpose supports() {
        return EmailPurpose.REGISTRATION;
    }

    @Override
    public void send(WelcomeEvent event) {
        log.info("Sending welcome email. EventID: {}", event.eventId());
        final String body = """
                Welcome to our service, %s!
                
                Your account has been successfully created.
                
                We're glad to have you with us.
                
                If you did not create this account, please contact our support team immediately.
                
                Regards,
                DocMind
                """.formatted(event.username());

        emailService.sendEmail(event.email(), "Welcome to our service", body);
    }
}
