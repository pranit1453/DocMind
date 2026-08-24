package com.pranit.docmind.mail.email.handler;

import com.pranit.docmind.entities.constant.OtpPurpose;
import com.pranit.docmind.mail.dto.OtpEvent;
import com.pranit.docmind.mail.email.OtpEmailHandler;
import com.pranit.docmind.mail.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public final class PasswordChangeOtpEmailHandler implements OtpEmailHandler {

    private final EmailService emailService;

    @Override
    public OtpPurpose supports() {
        return OtpPurpose.PASSWORD_CHANGE;
    }

    @Override
    public void send(final OtpEvent event) {
        log.info("Sending password change OTP. EventID: {}", event.eventId());
        final String body = """
                Password Change Request
                
                Use the following OTP to change your password:
                
                %s
                
                This OTP is valid for %s minutes.
                
                If you did not request this, please ignore.
                
                Regards,
                Authentication Service
                """.formatted(
                event.otp(),
                event.expiresAt().toMinutes()
        );
        emailService.sendEmail(event.email(), "Password Change OTP", body);
    }
}