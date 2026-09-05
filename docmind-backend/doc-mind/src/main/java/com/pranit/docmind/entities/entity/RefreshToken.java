package com.pranit.docmind.entities.entity;

import com.pranit.docmind.audit.entity.AuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens",
        schema = "auth",
        indexes = {
                @Index(name = "idx_refresh_token_jti", columnList = "jti", unique = true),
                @Index(name = "idx_refresh_token_user", columnList = "user_id"),
                @Index(name = "idx_refresh_token_session", columnList = "session_id"),
                @Index(name = "idx_refresh_token_expires_at", columnList = "expires_at"),
                @Index(name = "idx_refresh_token_revoked_expires", columnList = "revoked, expires_at")
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public class RefreshToken extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refreshTokenId;

    @Column(nullable = false, unique = true, updatable = false, length = 128)
    private String jti;

    @Column(name = "session_id", nullable = false, updatable = false, length = 128)
    private String sessionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_refresh_token_user")
    )
    private User user;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    @Builder.Default
    private boolean revoked = false;

    @Column(name = "replaced_by_token", length = 128)
    private String replacedByToken;

    @Version
    @Column(nullable = false)
    @Builder.Default
    private Long version = 0L;
}
