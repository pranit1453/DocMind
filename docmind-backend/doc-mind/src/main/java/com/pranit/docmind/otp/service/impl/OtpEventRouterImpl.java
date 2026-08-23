package com.pranit.docmind.otp.service.impl;

import com.pranit.docmind.entities.constant.OtpPurpose;
import com.pranit.docmind.event.dto.OtpEvent;
import com.pranit.docmind.event.service.EventPublisherService;
import com.pranit.docmind.otp.service.OtpEventRouter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OtpEventRouterImpl implements OtpEventRouter {

    private final EventPublisherService eventPublisherService;

    @Override
    public void publish(OtpPurpose purpose, OtpEvent event) {
        switch (purpose) {
            case REGISTRATION -> eventPublisherService.sendVerificationOtp(event);
            case FORGOT_PASSWORD -> eventPublisherService.sendPasswordChangeOtp(event);
            default -> throw new IllegalArgumentException("Unsupported OTP purpose: " + purpose);
        }
    }
}
