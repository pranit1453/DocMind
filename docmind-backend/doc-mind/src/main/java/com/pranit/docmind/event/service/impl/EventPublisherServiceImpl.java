package com.pranit.docmind.event.service.impl;

import com.pranit.docmind.event.dto.DeactivateAccountEvent;
import com.pranit.docmind.event.dto.OtpEvent;
import com.pranit.docmind.event.dto.SecurityEvent;
import com.pranit.docmind.event.dto.WelcomeEvent;
import com.pranit.docmind.event.service.EventPublisherService;
import org.springframework.stereotype.Service;

@Service
public class EventPublisherServiceImpl implements EventPublisherService {

    @Override
    public void sendVerificationOtp(OtpEvent data) {
        IO.println("Otp: " + data.otp() + " Verification OTP");
    }

    @Override
    public void sendWelcomeMessage(WelcomeEvent data) {
        IO.println("Welcome: " + data.email());
    }

    @Override
    public void sendPasswordChangeOtp(OtpEvent data) {
        IO.println("Otp: " + data.otp() + " Password Change OTP");
    }

    @Override
    public void sendEmailChangeOtp(OtpEvent data) {
        IO.println("Otp: " + data.otp() + " Email Change OTP");
    }

    @Override
    public void sendSecurityAlertToOldEmail(SecurityEvent data) {
        IO.println("Security alert to old email");
    }

    @Override
    public void sendEmailVerifyData(OtpEvent data) {
        IO.println("Otp: " + data.otp() + " Email Verify Data");
    }

    @Override
    public void sendSecurityAlertToUser(DeactivateAccountEvent event) {
        IO.println("Account deactivated........");
    }
}
