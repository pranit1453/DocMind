package com.pranit.docmind.mail.router;

import com.pranit.docmind.entities.constant.OtpPurpose;
import com.pranit.docmind.mail.dto.OtpEvent;
import com.pranit.docmind.mail.email.OtpEmailHandler;
import com.pranit.docmind.mail.exception.InvalidOtpEventException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public final class OtpEmailRouter {

    private final Map<OtpPurpose, OtpEmailHandler> handlers;

    public OtpEmailRouter(List<OtpEmailHandler> handlers) {
        this.handlers = handlers.stream()
                .collect(Collectors.toUnmodifiableMap(OtpEmailHandler::supports, Function.identity()));
    }

    @Async("emailExecutor")
    public void send(final OtpEvent event, final OtpPurpose purpose) {
        OtpEmailHandler handler = handlers.get(purpose);
        if (handler == null) {
            throw new InvalidOtpEventException("Unsupported OTP purpose: " + purpose);
        }
        handler.send(event);
    }
}