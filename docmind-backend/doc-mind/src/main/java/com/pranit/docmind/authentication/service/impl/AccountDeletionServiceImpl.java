package com.pranit.docmind.authentication.service.impl;

import com.pranit.docmind.authentication.exception.UserNotExistsException;
import com.pranit.docmind.authentication.repository.RefreshTokenRepository;
import com.pranit.docmind.authentication.repository.UserRepository;
import com.pranit.docmind.authentication.service.AccountDeletionService;
import com.pranit.docmind.entities.constant.EmailPurpose;
import com.pranit.docmind.entities.entity.User;
import com.pranit.docmind.helper.Generate;
import com.pranit.docmind.helper.SecurityContext;
import com.pranit.docmind.mail.dto.DeactivateAccountEvent;
import com.pranit.docmind.mail.router.EmailRouter;
import com.pranit.docmind.redis.service.RedisTokenStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountDeletionServiceImpl implements AccountDeletionService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RedisTokenStore redisTokenStore;
    private final EmailRouter emailRouter;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUserAccount() {
        final UUID userId = SecurityContext.getCurrentUserId();
        final User user = findUserByUserId(userId);
        if (user.isDeleted()) {
            log.info("User account already scheduled for deletion: {}", userId);
            return;
        }
        final Instant deletionDate = Instant.now().plus(15, ChronoUnit.DAYS);
        user.setDeleted(true);
        user.setScheduledDeletionAt(deletionDate);
        refreshTokenRepository.revokeAllByUserId(userId);
        redisTokenStore.invalidateUserSession(userId);
        log.info("User account scheduled for deletion. userId: {}, deletionDate: {}", userId, deletionDate);
        final DeactivateAccountEvent event = DeactivateAccountEvent.builder()
                .eventId(Generate.generateEventId())
                .username(user.getUsername())
                .email(user.getEmail())
                .timestamp(Instant.now())
                .build();
        emailRouter.send(event, EmailPurpose.SECURITY_ALERT);
    }

    private User findUserByUserId(final UUID userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with userId: {}", userId);
                    return new UserNotExistsException("User not found");
                });
    }
}
