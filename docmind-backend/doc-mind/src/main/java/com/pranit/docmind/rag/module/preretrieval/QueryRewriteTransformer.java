package com.pranit.docmind.rag.module.preretrieval;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

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
        final String rewrittenQuery = ragChatClient
                .prompt(prompt)
                .call()
                .content();
        return rewrittenQuery == null || rewrittenQuery.isBlank()
                ? query : rewrittenQuery.trim();
    }
}
