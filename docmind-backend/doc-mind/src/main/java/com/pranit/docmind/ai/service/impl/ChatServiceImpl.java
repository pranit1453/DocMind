package com.pranit.docmind.ai.service.impl;

import com.pranit.docmind.ai.dto.QueryResponse;
import com.pranit.docmind.ai.dto.RetrievalOptions;
import com.pranit.docmind.ai.factory.ChatModelProviderFactory;
import com.pranit.docmind.ai.service.ChatService;
import com.pranit.docmind.aop.annotation.LogExecution;
import com.pranit.docmind.document.exception.DocumentNotFoundException;
import com.pranit.docmind.document.repository.DocumentRepository;
import com.pranit.docmind.entities.constant.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.UUID;

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
        return factory.getStrategy(provider).getStreamResponse(query, conversationId, documentId, options);
    }
}
