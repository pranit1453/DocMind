package com.pranit.docmind.mail.router;

import com.pranit.docmind.entities.constant.EmailPurpose;
import com.pranit.docmind.mail.email.EmailHandler;
import com.pranit.docmind.mail.event.EmailEvent;
import com.pranit.docmind.mail.exception.InvalidEmailEventException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class EmailRouter {

    private final Map<EmailPurpose, EmailHandler<?>> handlers;

    public EmailRouter(List<EmailHandler<?>> handlers) {
        this.handlers = handlers.stream()
                .collect(Collectors.toUnmodifiableMap(EmailHandler::supports, Function.identity()));
    }

    @Async("emailExecutor")
    public void send(final EmailEvent event, final EmailPurpose purpose) {
        EmailHandler<?> handler = handlers.get(purpose);
        if (handler == null) throw new InvalidEmailEventException("Unsupported email purpose: " + purpose);
        sendEvent(handler, event);
    }

    @SuppressWarnings("unchecked")
    private <T> void sendEvent(EmailHandler<T> handler, EmailEvent event) {
        handler.send((T) event);
    }
}