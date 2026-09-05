package com.pranit.docmind.otp.service.impl;

import com.pranit.docmind.otp.service.OtpGeneration;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public final class AlphaNumericOtpGeneration implements OtpGeneration {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int OTP_LENGTH = 6;
    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    @Override
    public String generateOtp() {
        final StringBuilder otp = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(CHAR_POOL.charAt(SECURE_RANDOM.nextInt(CHAR_POOL.length())));
        }
        return otp.toString();
    }
}
