package com.pranit.docmind.event.service;

import com.pranit.docmind.event.dto.DeactivateAccountEvent;
import com.pranit.docmind.event.dto.OtpEvent;
import com.pranit.docmind.event.dto.SecurityEvent;
import com.pranit.docmind.event.dto.WelcomeEvent;

public interface EventPublisherService {

    void sendVerificationOtp(OtpEvent data);

    void sendWelcomeMessage(WelcomeEvent data);

    void sendPasswordChangeOtp(OtpEvent data);

    void sendEmailChangeOtp(OtpEvent data);

    void sendSecurityAlertToOldEmail(SecurityEvent data);

    void sendEmailVerifyData(OtpEvent data);

    void sendSecurityAlertToUser(DeactivateAccountEvent event);
}
