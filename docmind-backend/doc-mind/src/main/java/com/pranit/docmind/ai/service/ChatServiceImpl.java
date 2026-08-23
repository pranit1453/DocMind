package com.pranit.docmind.ai.service;

import com.pranit.docmind.ai.dto.QueryResponse;
import com.pranit.docmind.ai.factory.ChatModelProviderFactory;
import com.pranit.docmind.document.exception.DocumentNotFoundException;
import com.pranit.docmind.document.repository.DocumentRepository;
import com.pranit.docmind.entities.constant.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public final class ChatServiceImpl implements ChatService {

    private final ChatModelProviderFactory factory;
    private final DocumentRepository documentRepository;

    @Override
    public QueryResponse getResponseFromAssistant(final Provider provider, final String query, final UUID conversationId, final UUID documentId) {
        checkForDocumentRefrence(documentId);
        return factory.getStrategy(provider).getResponse(query, conversationId, documentId);
    }

    private void checkForDocumentRefrence(final UUID documentId) {
        if (!documentRepository.existsByDocumentId(documentId)) {
            throw new DocumentNotFoundException("Document not found");
        }
    }

    @Override
    public Flux<String> getStreamResponseFromAssistant(final Provider provider, final String query, final UUID conversationId, final UUID documentId) {
        checkForDocumentRefrence(documentId);
        return factory.getStrategy(provider).getStreamResponse(query, conversationId, documentId);
    }
}
