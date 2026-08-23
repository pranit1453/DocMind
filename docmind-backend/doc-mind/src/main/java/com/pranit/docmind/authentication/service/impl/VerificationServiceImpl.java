package com.pranit.docmind.authentication.service.impl;

import com.pranit.docmind.authentication.dto.VerificationResponse;
import com.pranit.docmind.authentication.dto.VerifyOtp;
import com.pranit.docmind.authentication.exception.EmailNotFoundException;
import com.pranit.docmind.authentication.repository.UserRepository;
import com.pranit.docmind.authentication.service.VerificationService;
import com.pranit.docmind.entities.constant.OtpPurpose;
import com.pranit.docmind.entities.entity.User;
import com.pranit.docmind.helper.Generate;
import com.pranit.docmind.mail.dto.WelcomeEvent;
import com.pranit.docmind.otp.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {

    private final OtpService otpService;
    private final UserRepository userRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public VerificationResponse verifyRegistration(final VerifyOtp request) {
        final OtpPurpose purpose = OtpPurpose.REGISTRATION;
        final String email = verifyOtp(request.challengeId(), request.otp(), purpose);
        final User user = findUserByEmail(email);
        activateAccount(user);
        final WelcomeEvent data = WelcomeEvent.builder()
                .eventId(Generate.generateEventId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
        log.info("Sending Welcome email to username: {}", user.getUsername());
        return VerificationResponse.builder()
                .message("Account verified successfully.")
                .build();
    }

    private String verifyOtp(String challengeId, String otp, OtpPurpose purpose) {
        return otpService.verifyOtp(challengeId, otp, purpose);
    }

    private User findUserByEmail(final String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User email not found: {}", email);
                    return new EmailNotFoundException("User's email not found");
                });
    }

    private void activateAccount(final User user) {
        if (!user.isEnabled()) {
            user.setEnabled(true);
            log.info("User account activated: {}", user.getEmail());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public VerificationResponse verifyPasswordResetOtp(final VerifyOtp request) {
        final OtpPurpose type = OtpPurpose.PASSWORD_CHANGE;
        final String email = verifyOtp(request.challengeId(), request.otp(), type);
        log.warn("Otp successfully verified for email: {}", email);
        return VerificationResponse.builder()
                .message("OTP verified successfully")
                .build();
    }
}
