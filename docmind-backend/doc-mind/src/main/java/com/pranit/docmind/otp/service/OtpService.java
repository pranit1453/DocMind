package com.pranit.docmind.otp.service;

import com.pranit.docmind.entities.constant.EmailPurpose;

public interface OtpService {

    String sendOtp(String email, EmailPurpose purpose);

    String verifyOtp(String challengeId, String otp, EmailPurpose purpose);
}
