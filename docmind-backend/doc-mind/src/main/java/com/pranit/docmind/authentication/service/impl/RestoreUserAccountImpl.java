package com.pranit.docmind.authentication.service.impl;

import com.pranit.docmind.authentication.exception.AccountDeletedException;
import com.pranit.docmind.authentication.exception.UserNotExistsException;
import com.pranit.docmind.authentication.repository.UserRepository;
import com.pranit.docmind.authentication.service.RestoreUserAccount;
import com.pranit.docmind.entities.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestoreUserAccountImpl implements RestoreUserAccount {

    private final UserRepository userRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void restoreUserAccount(final UUID userId) {
        final User user = validateAndFindUserByUserId(userId);
        if (!user.isDeleted()) return;
        final Instant scheduledDeletionAt = user.getScheduledDeletionAt();
        if (scheduledDeletionAt == null) {
            log.warn("Deleted user has no scheduled deletion date. userId: {}", userId);
            throw new AccountDeletedException("Account cannot be recovered");
        }
        if (!Instant.now().isBefore(scheduledDeletionAt)) {
            log.warn("Account recovery period expired. userId: {}, scheduledDeletionAt: {}", userId, scheduledDeletionAt);
            throw new AccountDeletedException("Account recovery period has expired");
        }
        user.setDeleted(false);
        user.setScheduledDeletionAt(null);
        log.info("Soft-deleted account restored after successful login. userId: {}", userId);
    }

    private User validateAndFindUserByUserId(UUID userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("UserId {} not found", userId);
                    return new UserNotExistsException("User not found");
                });
    }
}
