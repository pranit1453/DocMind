package com.pranit.docmind.rag.module.preretrieval;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class QueryRewriteTransformer {

    private static final PromptTemplate REWRITE_PROMPT = PromptTemplate.builder().template("""
            Rewrite the following user query into an optimized
            semantic search query.
            
            Rules:
            - Preserve the original intent.
            - Remove unnecessary conversational words.
            - Make the query specific and information-rich.
            - Do not answer the question.
            - Return only the optimized query.
            
            User query:
            {query}
            """).build();

    private final ChatClient ragChatClient;

    public QueryRewriteTransformer(@Qualifier("rewriteChatClient") final ChatClient ragChatClient) {
        this.ragChatClient = ragChatClient;
    }

    public String rewriteQuery(final String query) {
        final Prompt prompt = REWRITE_PROMPT.create(Map.of("query", query));
        final var rewrittenQuery = ragChatClient
                .prompt(prompt)
                .call()
                .chatResponse();
        final String response = Optional.ofNullable(rewrittenQuery)
                .map(ChatResponse::getResult)
                .map(Generation::getOutput)
                .map(AssistantMessage::getText)
                .orElseThrow(() -> new IllegalStateException("LLM returned an empty response"));
        return response.isBlank() ? query : response.trim();
    }
}
