package com.pranit.docmind.ai.provider;

import com.pranit.docmind.ai.advisor.RetrievalAugmentedGenerationAdvisor;
import com.pranit.docmind.ai.dto.QueryResponse;
import com.pranit.docmind.ai.dto.RetrievalOptions;
import com.pranit.docmind.ai.stratergy.ChatModelStrategy;
import com.pranit.docmind.entities.constant.Provider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public final class NvidiaModelService implements ChatModelStrategy {

    private final ChatClient chatClient;
    private final RetrievalAugmentedGenerationAdvisor advisor;

    @Value("classpath:prompt/userPrompt.st")
    private Resource userPrompt;

    @Override
    public QueryResponse getResponse(final String query, final UUID conversationId, final UUID documentId, final RetrievalOptions options) {
        var response = this.chatClient.prompt()
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .advisors(advisor.retrievalAugmentedGenerationWorkflow(documentId, options))
                .user(user -> user.text(this.userPrompt).param("concept", query))
                .call()
                .entity(QueryResponse.class);
        log.info("Non Streaming Response: {}", response);
        return response;
    }

    @Override
    public Flux<String> getStreamResponse(final String query, final UUID conversationId, final UUID documentId, final RetrievalOptions options) {
        var response = this.chatClient.prompt()
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .advisors(advisor.retrievalAugmentedGenerationWorkflow(documentId, options))
                .user(user -> user.text(this.userPrompt).param("concept", query))
                .stream()
                .content();
        log.info("Streaming Response: {}", response);
        return response;
    }

    @Override
    public Provider getProviderName() {
        return Provider.NVIDIA;
    }
}
