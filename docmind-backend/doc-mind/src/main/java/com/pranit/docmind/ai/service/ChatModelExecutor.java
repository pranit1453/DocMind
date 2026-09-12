package com.pranit.docmind.ai.service;

import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ChatModelExecutor {

    ChatResponse execute(String context, UUID conversationId);

    Flux<String> stream(final String context, final UUID conversationId);
}
