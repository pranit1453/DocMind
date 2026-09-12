package com.pranit.docmind.ai.service.impl;

import com.pranit.docmind.ai.service.ChatModelExecutor;
import com.pranit.docmind.aop.annotation.LogChatResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Component
public class ChatModelExecutorImpl implements ChatModelExecutor {

    private final ChatClient chatClient;

    public ChatModelExecutorImpl(@Qualifier("chatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    @LogChatResponse
    public ChatResponse execute(final String context, final UUID conversationId) {
        return chatClient.prompt()
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .user(context)
                .call()
                .chatResponse();
    }

    @Override
    @LogChatResponse
    public Flux<String> stream(String context, UUID conversationId) {
        return this.chatClient.prompt()
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .user(context)
                .stream()
                .content();
    }
}
