package com.pranit.docmind.mail.email.handler;

import com.pranit.docmind.entities.constant.EmailPurpose;
import com.pranit.docmind.mail.dto.OtpEvent;
import com.pranit.docmind.mail.email.EmailHandler;
import com.pranit.docmind.mail.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public final class RegistrationOtpEmailHandler implements EmailHandler<OtpEvent> {

    private final EmailService emailService;

    @Override
    public EmailPurpose supports() {
        return EmailPurpose.REGISTRATION;
    }

    @Override
    public void send(final OtpEvent event) {
        log.info("Sending registration OTP. EventID: {}", event.eventId());
        final String body = """
                Email Verification
                
                Use the following OTP to verify your email:
                
                %s
                
                This OTP is valid for %s minutes.
                
                Do not share this OTP with anyone.
                
                If you did not request this, please ignore this email.
                
                Regards,
                DocMind
                """.formatted(
                event.otp(),
                event.expiresAt().toMinutes()
        );
        
        emailService.sendEmail(event.email(), "Account Activation OTP", body);
    }
}