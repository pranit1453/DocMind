package com.pranit.docmind.redis.service;

import java.util.List;
import java.util.UUID;

public interface RedisTokenStore {

    void addTokenIdentifier(UUID userId, String identifiers);

    void invalidateUserSession(UUID userId);

    void invalidateUserSession(List<UUID> userIds);

    boolean verifyIdentifier(UUID userId, String identifier);
}
