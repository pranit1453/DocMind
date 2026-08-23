package com.pranit.docmind.entities.entity;

import com.pranit.docmind.audit.entity.AuditEntity;
import com.pranit.docmind.entities.constant.OtpPurpose;
import com.pranit.docmind.entities.constant.OtpStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Entity
@Table(
        name = "otp",
        schema = "auth",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_otp_challenge_id", columnNames = "challenge_id")
        },
        indexes = {
                @Index(name = "idx_otp_email", columnList = "email"),
                @Index(name = "idx_otp_email_purpose", columnList = "email, purpose"),
                @Index(name = "idx_otp_status", columnList = "status"),
                @Index(name = "idx_otp_expires_at", columnList = "expires_at")
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public class OtpEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long otpId;

    @Column(nullable = false, unique = true)
    private String challengeId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String otpHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OtpPurpose purpose;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OtpStatus status;

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant verifiedAt;
}