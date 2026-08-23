package com.pranit.docmind.redis.model;

import com.pranit.docmind.entities.constant.OtpPurpose;
import com.pranit.docmind.entities.constant.OtpStatus;
import lombok.Builder;

@Builder
public record OtpVault(
        String email,
        String otp,
        OtpPurpose purpose,
        OtpStatus status
) {
}
