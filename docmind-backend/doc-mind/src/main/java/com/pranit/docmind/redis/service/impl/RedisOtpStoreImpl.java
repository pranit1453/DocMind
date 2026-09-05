package com.pranit.docmind.redis.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pranit.docmind.entities.constant.EmailPurpose;
import com.pranit.docmind.otp.exception.OTPValidationException;
import com.pranit.docmind.redis.model.OtpVault;
import com.pranit.docmind.redis.service.RedisOtpStore;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisOtpStoreImpl implements RedisOtpStore {

    private static final String CHALLENGE_PREFIX = "otp:challenge:";
    private static final String CURRENT_PREFIX = "otp:current:";
    private static final String FAILURE_PREFIX = "otp:failures:";
    private static final String LOCK_PREFIX = "otp:lock:";

    private static final String LOCK_VALUE = "LOCKED";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void saveChallenge(final String challengeId, final OtpVault vault, final Duration ttl) {
        final String key = challengeKey(vault.purpose(), challengeId);
        try {
            final String value = objectMapper.writeValueAsString(vault);
            redisTemplate.opsForValue().set(key, value, ttl);
        } catch (JsonProcessingException e) {
            throw new OTPValidationException("Failed to serialize OTP challenge");
        }
    }

    private String challengeKey(final EmailPurpose purpose, final String challengeId) {
        return CHALLENGE_PREFIX + purpose.name() + ":" + challengeId;
    }

    @Override
    public Optional<OtpVault> getChallenge(final String challengeId, final EmailPurpose purpose) {
        final String key = challengeKey(purpose, challengeId);
        final String value = redisTemplate.opsForValue().get(key);
        if (value == null) return Optional.empty();
        try {
            final OtpVault vault = objectMapper.readValue(value, OtpVault.class);
            if (vault.purpose() != purpose) return Optional.empty();
            return Optional.of(vault);
        } catch (JsonProcessingException e) {
            throw new OTPValidationException("Failed to deserialize OTP challenge");
        }
    }

    @Override
    public void deleteChallenge(final String challengeId, final EmailPurpose purpose) {
        redisTemplate.delete(challengeKey(purpose, challengeId));
    }

    @Override
    public void setCurrentChallenge(final String email, final EmailPurpose purpose, final String challengeId, final Duration ttl) {
        redisTemplate.opsForValue().set(currentKey(email, purpose), challengeId, ttl);
    }

    private String currentKey(final String email, final EmailPurpose purpose) {
        return CURRENT_PREFIX + purpose.name() + ":" + normalizeEmail(email);
    }

    private String normalizeEmail(final String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean isCurrentChallenge(final String email, final EmailPurpose purpose, final String challengeId) {
        final String currentChallenge = redisTemplate.opsForValue().get(currentKey(email, purpose));
        return challengeId.equals(currentChallenge);
    }

    @Override
    public int incrementFailures(final String email, final EmailPurpose purpose, final Duration ttl) {
        final String key = failureKey(email, purpose);
        final Long failures = redisTemplate.opsForValue().increment(key);
        if (failures == null) throw new OTPValidationException("Failed to increment OTP failures");
        if (failures == 1) redisTemplate.expire(key, ttl);
        return failures.intValue();
    }

    private String failureKey(final String email, final EmailPurpose purpose) {
        return FAILURE_PREFIX + purpose.name() + ":" + normalizeEmail(email);
    }

    @Override
    public void clearFailures(final String email, final EmailPurpose purpose) {
        redisTemplate.delete(failureKey(email, purpose));
    }

    @Override
    public void lockOtpRequests(final String email, final Duration duration) {
        redisTemplate.opsForValue().set(lockKey(email), LOCK_VALUE, duration);
    }

    private String lockKey(final String email) {
        return LOCK_PREFIX + normalizeEmail(email);
    }

    @Override
    public boolean isOtpRequestLocked(final String email) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(lockKey(email)));
    }

    @Override
    public void clearOtpState(final String email, final String challengeId, final EmailPurpose purpose) {
        redisTemplate.delete(Set.of(
                challengeKey(purpose, challengeId),
                currentKey(email, purpose),
                failureKey(email, purpose)));
    }
}