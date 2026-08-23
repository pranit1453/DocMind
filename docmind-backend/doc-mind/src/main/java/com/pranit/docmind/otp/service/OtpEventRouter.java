package com.pranit.docmind.otp.service;


import com.pranit.docmind.entities.constant.OtpPurpose;
import com.pranit.docmind.event.dto.OtpEvent;

@FunctionalInterface
public interface OtpEventRouter {

    void publish(OtpPurpose purpose, OtpEvent event);
}
