package com.pranit.docmind.otp.service;

import com.pranit.docmind.entities.constant.OtpPurpose;

public interface OtpService {

    String sendOtp(String email, OtpPurpose purpose);

    String verifyOtp(String challengeId, String otp, OtpPurpose purpose);
}
