package com.pranit.docmind.authentication.repository;

import com.pranit.docmind.entities.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);

    Optional<User> findByUserId(UUID userId);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUserIdAndEmail(UUID userId, String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
                select u
                from User u
                where u.username = :username
            """)
    Optional<User> findByUsernameForUpdate(String username);

    @Query("""
                SELECT u.userId
                FROM User u
                WHERE u.deleted = true
                  AND u.scheduledDeletionAt IS NOT NULL
                  AND u.scheduledDeletionAt <= :now
            """)
    List<UUID> findAllIdsScheduledForPermanentDeletion(@Param("now") Instant now);

    @Modifying
    @Query("""
                DELETE FROM User u
                WHERE u.userId IN :userIds
            """)
    int deleteAllByUserIds(@Param("userIds") List<UUID> userIds);

    Optional<User> findByEmail(String email);
}
