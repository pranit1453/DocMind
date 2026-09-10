package com.pranit.docmind.ai.stratergy;

import com.pranit.docmind.ai.dto.QueryRequest;
import com.pranit.docmind.ai.dto.QueryResponse;
import com.pranit.docmind.entities.constant.Provider;
import com.pranit.docmind.entities.entity.DocumentMetadata;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ChatModelStrategy {

    QueryResponse getResponse(String query, UUID conversationId, DocumentMetadata metadata, QueryRequest.Options options);

    Flux<String> getStreamResponse(String query, UUID conversationId, UUID documentId, QueryRequest.Options options);

    Provider getProviderName();
}
