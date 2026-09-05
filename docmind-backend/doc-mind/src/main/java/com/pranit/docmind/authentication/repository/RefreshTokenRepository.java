package com.pranit.docmind.authentication.repository;

import com.pranit.docmind.entities.entity.RefreshToken;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByJti(String jti);

    @Modifying
    @Query("""
            delete from RefreshToken rt
            where rt.expiresAt < :before
            """)
    int deleteExpiredTokens(@Param("before") Instant before);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select rt
            from RefreshToken rt
            join fetch rt.user u
            where rt.jti = :jti
            """)
    Optional<RefreshToken> findByJtiForUpdate(@Param("jti") String jti);

    @Modifying
    @Query("""
            update RefreshToken rt
            set rt.revoked = true
            where rt.user.userId = :userId
              and rt.sessionId = :sessionId
              and rt.revoked = false
            """)
    void revokeAllByUserIdAndSessionId(@Param("userId") UUID userId, @Param("sessionId") String sessionId);

    @Modifying
    @Query("""
            update RefreshToken rt
            set rt.revoked = true
            where rt.user.userId = :userId
              and rt.revoked = false
            """)
    void revokeAllByUserId(UUID userId);

    @Modifying
    @Transactional
    @Query("""
            DELETE FROM RefreshToken rt
            WHERE rt.user.userId IN :userIds
            """)
    void deleteAllByUserIds(@Param("userIds") List<UUID> userIds);
}
