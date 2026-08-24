package com.pranit.docmind.mail.email.handler;

import com.pranit.docmind.entities.constant.EmailPurpose;
import com.pranit.docmind.mail.dto.DeactivateAccountEvent;
import com.pranit.docmind.mail.email.EmailHandler;
import com.pranit.docmind.mail.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteAccountAlertEmailHandler implements EmailHandler<DeactivateAccountEvent> {

    private final EmailService emailService;

    @Override
    public EmailPurpose supports() {
        return EmailPurpose.SECURITY_ALERT;
    }

    @Override
    public void send(DeactivateAccountEvent event) {
        log.info("Sending account deactivation security alert. EventID: {}", event.eventId());
        final String body = """
                Security Alert
                
                Hi %s,
                
                Your account has been deactivated successfully.
                
                This action was requested on %s.
                
                Your account is scheduled for permanent deletion after 15 days.
                If you want to recover your account, please log in again within
                this 15-day recovery period.
                
                After 15 days, your account will be permanently deleted and
                cannot be recovered.
                
                If you did not request this action, please contact our support team immediately.
                
                Regards,
                Authentication Service
                """.formatted(event.username(), event.timestamp()
        );

        emailService.sendEmail(event.email(), "Security Alert: Account Deactivated", body);
    }
}
