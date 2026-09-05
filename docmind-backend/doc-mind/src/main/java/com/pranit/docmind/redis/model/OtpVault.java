package com.pranit.docmind.redis.model;

import com.pranit.docmind.entities.constant.EmailPurpose;
import com.pranit.docmind.entities.constant.OtpStatus;
import lombok.Builder;

@Builder
public record OtpVault(
        String email,
        String otp,
        EmailPurpose purpose,
        OtpStatus status
) {
}
