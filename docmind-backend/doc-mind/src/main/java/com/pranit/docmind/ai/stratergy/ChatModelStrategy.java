package com.pranit.docmind.ai.stratergy;

import com.pranit.docmind.ai.dto.QueryResponse;
import com.pranit.docmind.entities.constant.Provider;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ChatModelStrategy {

    QueryResponse getResponse(String query, UUID conversationId, UUID documentId);

    Flux<String> getStreamResponse(String query, UUID conversationId, UUID documentId);

    Provider getProviderName();
}
