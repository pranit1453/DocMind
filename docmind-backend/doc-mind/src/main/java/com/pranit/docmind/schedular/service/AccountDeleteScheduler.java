package com.pranit.docmind.schedular.service;

import com.pranit.docmind.authentication.repository.RefreshTokenRepository;
import com.pranit.docmind.authentication.repository.UserRepository;
import com.pranit.docmind.redis.service.RedisTokenStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountDeleteScheduler {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RedisTokenStore redisTokenStore;

    @Scheduled(cron = "0 0 2 * * *", zone = "Asia/Kolkata")
    @Transactional
    public void permanentUserAccountDeletion() {
        final Instant now = Instant.now();
        final List<UUID> userIds = userRepository.findAllIdsScheduledForPermanentDeletion(now);
        if (userIds.isEmpty()) {
            log.info("No accounts scheduled for permanent deletion");
            return;
        }
        log.info("Permanent deletion for {} users", userIds.size());
        redisTokenStore.invalidateUserSession(userIds);
        refreshTokenRepository.deleteAllByUserIds(userIds);
        final int deletedUsers = userRepository.deleteAllByUserIds(userIds);
        log.info("Permanent account deletion completed for {} users", deletedUsers);
    }

}
