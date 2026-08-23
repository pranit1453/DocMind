package com.pranit.docmind.ai.service;

import com.pranit.docmind.ai.dto.QueryResponse;
import com.pranit.docmind.entities.constant.Provider;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ChatService {

    QueryResponse getResponseFromAssistant(Provider provider, String query, UUID conversationId, UUID documentId);

    Flux<String> getStreamResponseFromAssistant(Provider provider, String query, UUID conversationId, UUID documentId);
}
