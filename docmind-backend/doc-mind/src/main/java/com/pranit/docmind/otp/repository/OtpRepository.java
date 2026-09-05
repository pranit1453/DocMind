package com.pranit.docmind.otp.repository;

import com.pranit.docmind.entities.entity.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpEntity, Long> {

    Optional<OtpEntity> findByChallengeId(String challengeId);
}
