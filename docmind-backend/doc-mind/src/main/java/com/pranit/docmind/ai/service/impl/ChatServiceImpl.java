package com.pranit.docmind.ai.service.impl;

import com.openai.errors.OpenAIIoException;
import com.openai.errors.SseException;
import com.pranit.docmind.ai.dto.QueryResponse;
import com.pranit.docmind.ai.dto.RetrievalOptions;
import com.pranit.docmind.ai.exception.AiServiceUnavailableException;
import com.pranit.docmind.ai.factory.ChatModelProviderFactory;
import com.pranit.docmind.ai.service.ChatService;
import com.pranit.docmind.aop.annotation.LogExecution;
import com.pranit.docmind.document.exception.DocumentNotFoundException;
import com.pranit.docmind.document.repository.DocumentRepository;
import com.pranit.docmind.entities.constant.Provider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.io.InterruptedIOException;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatModelProviderFactory factory;
    private final DocumentRepository documentRepository;

    @Override
    @LogExecution
    public QueryResponse getResponseFromAssistant(final Provider provider, final String query, final UUID conversationId, final UUID documentId, final RetrievalOptions options) {
        checkForDocumentRefrence(documentId);
        return factory.getStrategy(provider).getResponse(query, conversationId, documentId, options);
    }

    private void checkForDocumentRefrence(final UUID documentId) {
        if (!documentRepository.existsByDocumentId(documentId)) {
            throw new DocumentNotFoundException("Document not found");
        }
    }

    @Override
    @LogExecution
    public Flux<String> getStreamResponseFromAssistant(final Provider provider, final String query, final UUID conversationId, final UUID documentId, final RetrievalOptions options) {
        checkForDocumentRefrence(documentId);
        return Flux.defer(() -> factory
                .getStrategy(provider)
                .getStreamResponse(query, conversationId, documentId, options)
        ).retryWhen(Retry.backoff(2, Duration.ofSeconds(2))
                .maxBackoff(Duration.ofSeconds(10))
                .jitter(0.5)
                .filter(this::isRetryable)
                .doBeforeRetry(retrySignal ->
                        log.warn("AI stream failed. Retrying request. attempt: {}, cause: {}", retrySignal.totalRetries() + 1, retrySignal.failure().toString()))
                .onRetryExhaustedThrow((retrySpec, retrySignal) ->
                        new AiServiceUnavailableException("AI service is temporarily unavailable. Please try again."))
        );
    }

    private boolean isRetryable(final Throwable throwable) {
        Throwable cause = throwable;
        while (cause != null) {
            switch (cause) {
                case SseException ignored -> {
                    return true;
                }
                case OpenAIIoException ignored -> {
                    return true;
                }
                case InterruptedIOException ignored -> {
                    return true;
                }
                default -> cause = cause.getCause();
            }
        }
        return false;
    }
}
