package com.pranit.docmind.ai.provider;

import com.pranit.docmind.ai.dto.QueryRequest;
import com.pranit.docmind.ai.dto.QueryResponse;
import com.pranit.docmind.ai.stratergy.ChatModelStrategy;
import com.pranit.docmind.aop.annotation.LogExecution;
import com.pranit.docmind.aop.annotation.TrackExecution;
import com.pranit.docmind.entities.constant.Provider;
import com.pranit.docmind.entities.entity.DocumentMetadata;
import com.pranit.docmind.rag.workflow.WorkflowOrchestrator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Slf4j
@Service
public class NvidiaModelService implements ChatModelStrategy {

    private final ChatClient chatClient;
    private final Resource userPrompt;
    private final WorkflowOrchestrator workflow;

    public NvidiaModelService(
            @Qualifier("chatClient") final ChatClient chatClient,
            @Value("classpath:prompt/userPrompt.st") final Resource userPrompt,
            WorkflowOrchestrator workflow) {
        this.chatClient = chatClient;
        this.userPrompt = userPrompt;
        this.workflow = workflow;
    }

    @Override
    @LogExecution
    @TrackExecution
    public QueryResponse getResponse(final String query, final UUID conversationId, final DocumentMetadata metadata, final QueryRequest.Options options) {
        final var context = workflow.execute(metadata, query, options);
        final var content = chatClient.prompt()
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .user(user -> user.text(userPrompt)
                        .param("concept", query)
                        .param("context", context.context()))
                .call()
                .content();
        return QueryResponse.builder()
                .content(content)
                .citations(context.citations())
                .build();
    }

    @Override
    @LogExecution
    @TrackExecution
    public Flux<String> getStreamResponse(final String query, final UUID conversationId, final UUID documentId, final QueryRequest.Options options) {
        return this.chatClient.prompt()
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .user(user -> user.text(this.userPrompt).param("concept", query))
                .stream()
                .content();
    }

    @Override
    public Provider getProviderName() {
        return Provider.NVIDIA;
    }
}
