package com.pranit.docmind.ai.provider;

import com.pranit.docmind.ai.dto.QueryRequest;
import com.pranit.docmind.ai.dto.QueryResponse;
import com.pranit.docmind.ai.service.ChatModelExecutor;
import com.pranit.docmind.ai.stratergy.ChatModelStrategy;
import com.pranit.docmind.aop.annotation.LogChatResponse;
import com.pranit.docmind.aop.annotation.LogExecution;
import com.pranit.docmind.aop.annotation.TrackExecution;
import com.pranit.docmind.entities.constant.Provider;
import com.pranit.docmind.entities.entity.DocumentMetadata;
import com.pranit.docmind.rag.workflow.WorkflowOrchestrator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class NvidiaModelService implements ChatModelStrategy {

    private final WorkflowOrchestrator workflow;
    private final ChatModelExecutor chatModelExecutor;

    public NvidiaModelService(WorkflowOrchestrator workflow, ChatModelExecutor chatModelExecutor) {
        this.chatModelExecutor = chatModelExecutor;
        this.workflow = workflow;
    }

    @Override
    @LogExecution
    @LogChatResponse
    @TrackExecution
    public QueryResponse getResponse(final String query, final UUID conversationId, final DocumentMetadata metadata, final QueryRequest.Options options) {
        final var context = workflow.execute(metadata, query, options);
        final var chatResponse = chatModelExecutor.execute(context.context(), conversationId);
        final String response = Optional.ofNullable(chatResponse)
                .map(ChatResponse::getResult)
                .map(Generation::getOutput)
                .map(AssistantMessage::getText)
                .orElseThrow(() -> new IllegalStateException("LLM returned an empty response"));
        return QueryResponse.builder()
                .content(response)
                .citations(context.citations())
                .build();
    }

    @Override
    @LogExecution
    public Flux<String> getStreamResponse(final String query, final UUID conversationId, final DocumentMetadata metadata, final QueryRequest.Options options) {
        final var context = workflow.execute(metadata, query, options);
        return chatModelExecutor.stream(context.context(), conversationId);
    }

    @Override
    public Provider getProviderName() {
        return Provider.NVIDIA;
    }
}
