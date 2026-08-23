package com.pranit.docmind.helper;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

public final class Generate {
    private static final SecureRandom RANDOM = new SecureRandom();

    private Generate() {
    }

    public static String generateSessionId() {
        return generateToken(64);
    }

    private static String generateToken(int length) {
        byte[] randomBytes = new byte[length];
        RANDOM.nextBytes(randomBytes);
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

    public static String generateEventId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String generateJti() {
        return generateToken(64);
    }

    public static String generateChallengeId() {
        return generateToken(6);
    }
}
