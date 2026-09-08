package com.pranit.docmind.otp.service.impl;

import com.pranit.docmind.otp.service.OtpGeneration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@Primary
public final class NumericOtpGeneration implements OtpGeneration {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int OTP_LENGTH = 6;

    @Override
    public String generateOtp() {
        final StringBuilder otp = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(SECURE_RANDOM.nextInt(10));
        }
        return otp.toString();
    }
}
