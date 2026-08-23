package com.pranit.docmind.redis.service.impl;

import com.pranit.docmind.properties.TokenProperties;
import com.pranit.docmind.redis.service.RedisTokenStore;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public final class RedisTokenStoreImpl implements RedisTokenStore {

    private final StringRedisTemplate redisTemplate;
    private final TokenProperties properties;

    @Override
    public void addTokenIdentifier(final UUID userId, final String identifiers) {
        redisTemplate.opsForValue().set(key(userId), identifiers, Duration.ofSeconds(properties.accessToken().expiration()));
    }

    private String key(final UUID userId) {
        return "token:" + userId;
    }

    @Override
    public void invalidateUserSession(final UUID userId) {
        redisTemplate.delete(key(userId));
    }

    @Override
    public void invalidateUserSession(final List<UUID> userIds) {
        userIds.forEach(this::invalidateUserSession);
    }

    @Override
    public boolean verifyIdentifier(final UUID userId, final String identifier) {
        final String value = redisTemplate.opsForValue().get(key(userId));
        return value != null && value.equals(identifier);
    }
}
