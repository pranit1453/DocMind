package com.pranit.docmind.otp.service.impl;

import com.pranit.docmind.entities.constant.OtpPurpose;
import com.pranit.docmind.entities.constant.OtpStatus;
import com.pranit.docmind.entities.entity.OtpEntity;
import com.pranit.docmind.helper.Generate;
import com.pranit.docmind.mail.dto.OtpEvent;
import com.pranit.docmind.mail.router.OtpEmailRouter;
import com.pranit.docmind.otp.exception.OTPValidationException;
import com.pranit.docmind.otp.repository.OtpRepository;
import com.pranit.docmind.otp.service.OtpGeneration;
import com.pranit.docmind.otp.service.OtpService;
import com.pranit.docmind.redis.model.OtpVault;
import com.pranit.docmind.redis.service.RedisOtpStore;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private static final int MAX_ATTEMPTS = 3;
    private static final Duration OTP_TTL = Duration.ofMinutes(5);
    private static final Duration LOCK_DURATION = Duration.ofMinutes(10);

    private final RedisOtpStore redisOtpStore;
    private final PasswordEncoder passwordEncoder;
    private final OtpGeneration otpGeneration;
    private final OtpRepository otpRepository;
    private final OtpEmailRouter otpEmailRouter;

    @Override
    @Transactional
    public String sendOtp(String email, OtpPurpose purpose) {

        /*
         * Global account-level OTP lock.
         */
        if (redisOtpStore.isOtpRequestLocked(email)) {
            throw new OTPValidationException("OTP requests are temporarily blocked. " + "Try again later.");
        }

        String challengeId = Generate.generateChallengeId();
        String otp = otpGeneration.generateOtp();
        String otpHash = passwordEncoder.encode(otp);
        OtpVault vault = OtpVault.builder()
                .email(email)
                .otp(otpHash)
                .purpose(purpose)
                .status(OtpStatus.PENDING)
                .build();

        OtpEntity otpEntity = OtpEntity.builder()
                .challengeId(challengeId)
                .email(email)
                .otpHash(otpHash)
                .purpose(purpose)
                .status(OtpStatus.PENDING)
                .expiresAt(Instant.now().plus(OTP_TTL))
                .build();

        otpRepository.save(otpEntity);

        redisOtpStore.saveChallenge(challengeId, vault, OTP_TTL);

        /*
         * Mark this as the ONLY valid challenge
         * for this email + purpose.
         */
        redisOtpStore.setCurrentChallenge(email, purpose, challengeId, OTP_TTL);

        final OtpEvent data = OtpEvent.builder()
                .eventId(Generate.generateEventId())
                .email(email)
                .otp(otp)
                .build();
        otpEmailRouter.send(data, purpose);
        return challengeId;
    }

    @Override
    public String verifyOtp(String challengeId, String otp, OtpPurpose purpose) {

        OtpVault vault = redisOtpStore.getChallenge(challengeId, purpose).orElseThrow(() -> new OTPValidationException("OTP expired or invalid"));
        String email = vault.email();
        /*
         * Make sure this is the latest OTP.
         */
        if (!redisOtpStore.isCurrentChallenge(email, purpose, challengeId)) {
            throw new OTPValidationException("OTP expired or invalid");
        }

        /*
         * Extra purpose validation.
         */
        if (vault.purpose() != purpose) {
            throw new OTPValidationException("Invalid OTP purpose");
        }

        if (vault.status() != OtpStatus.PENDING) {
            throw new OTPValidationException("OTP is no longer valid");
        }

        boolean valid = passwordEncoder.matches(otp, vault.otp());

        if (!valid) {
            handleFailedVerification(challengeId, email, purpose);
            // handleFailedVerification throws
            throw new IllegalStateException();
        }
        handleSuccessfulVerification(challengeId, email, purpose);
        return email;
    }

    private void handleFailedVerification(String challengeId, String email, OtpPurpose purpose) {

        /*
         * Atomic Redis INCR.
         *
         * Important:
         * failures belong to email + purpose,
         * NOT to challengeId.
         */
        int failures = redisOtpStore.incrementFailures(email, purpose, OTP_TTL);

        /*
         * Third failure.
         */
        if (failures >= MAX_ATTEMPTS) {

            /*
             * Global 10-minute lock.
             */
            redisOtpStore.lockOtpRequests(email, LOCK_DURATION);

            /*
             * Invalidate current OTP.
             */
            redisOtpStore.deleteChallenge(challengeId, purpose);

            /*
             * Mark DB record as exhausted.
             */
            OtpEntity otpEntity = getOtpEntity(challengeId);

            otpEntity.setStatus(OtpStatus.FAILED);

            otpRepository.save(otpEntity);

            throw new OTPValidationException("Maximum OTP attempts exceeded. " + "Try again in 10 minutes.");
        }

        /*
         * DO NOT mark the DB record FAILED here.
         *
         * The OTP is still usable.
         */
        int remaining = MAX_ATTEMPTS - failures;

        throw new OTPValidationException("Invalid OTP. " + remaining + " attempts remaining.");
    }

    private void handleSuccessfulVerification(String challengeId, String email, OtpPurpose purpose) {

        redisOtpStore.clearOtpState(email, challengeId, purpose);

        /*
         * Update DB audit record.
         */
        OtpEntity otpEntity = getOtpEntity(challengeId);

        otpEntity.setStatus(OtpStatus.VERIFIED);

        otpEntity.setVerifiedAt(Instant.now());

        otpRepository.save(otpEntity);
    }

    private OtpEntity getOtpEntity(String challengeId) {
        return otpRepository.findByChallengeId(challengeId).orElseThrow(() -> new IllegalStateException("OTP record not found: " + challengeId));
    }
}