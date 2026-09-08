package com.pranit.docmind.authentication.service;

import com.pranit.docmind.authentication.dto.VerificationResponse;
import com.pranit.docmind.authentication.dto.VerifyOtp;
import jakarta.validation.Valid;

public interface VerificationService {

    VerificationResponse verifyRegistration(@Valid VerifyOtp request);

    VerificationResponse verifyPasswordResetOtp(@Valid VerifyOtp request);

}
