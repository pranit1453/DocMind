package com.pranit.docmind.rag.module.preretrieval;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.expansion.QueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransformerImpl implements Transformer {

    private final ChatClient ragChatClient;

    @Override
    public QueryTransformer rewriteTransformer() {
        return RewriteQueryTransformer.builder()
                .chatClientBuilder(this.ragChatClient.mutate().clone())
                .build();
    }

    @Override
    public QueryTransformer translationTransformer() {
        return TranslationQueryTransformer.builder()
                .chatClientBuilder(this.ragChatClient.mutate().clone())
                .targetLanguage("english")
                .build();
    }

    @Override
    public QueryExpander queryExpander() {
        return MultiQueryExpander.builder()
                .chatClientBuilder(this.ragChatClient.mutate().clone())
                .build();
    }
}
