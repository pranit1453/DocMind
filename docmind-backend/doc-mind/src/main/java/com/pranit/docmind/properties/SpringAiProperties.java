package com.pranit.docmind.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.ai.openai")
public record SpringAiProperties(
        ChatProperties chat
) {

    public record ChatProperties(
            Double temperature,
            Integer maxCompletionTokens
    ) {
    }
}
