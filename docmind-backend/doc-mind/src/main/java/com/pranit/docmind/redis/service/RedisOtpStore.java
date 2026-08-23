package com.pranit.docmind.redis.service;

import com.pranit.docmind.entities.constant.OtpPurpose;
import com.pranit.docmind.redis.model.OtpVault;

import java.time.Duration;
import java.util.Optional;

public interface RedisOtpStore {

    void saveChallenge(String challengeId, OtpVault vault, Duration ttl);

    Optional<OtpVault> getChallenge(String challengeId, OtpPurpose purpose);

    void deleteChallenge(String challengeId, OtpPurpose purpose);

    void setCurrentChallenge(String email, OtpPurpose purpose, String challengeId, Duration ttl);

    boolean isCurrentChallenge(String email, OtpPurpose purpose, String challengeId);

    int incrementFailures(String email, OtpPurpose purpose, Duration ttl);

    void clearFailures(String email, OtpPurpose purpose);

    void lockOtpRequests(String email, Duration duration);

    boolean isOtpRequestLocked(String email);

    void clearOtpState(String email, String challengeId, OtpPurpose purpose);
}