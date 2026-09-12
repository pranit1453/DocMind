package com.pranit.docmind.ai.configuration;

import com.pranit.docmind.properties.AdvisorProperties;
import com.pranit.docmind.properties.RagProperties;
import com.pranit.docmind.properties.SpringAiProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.List;

@Slf4j
@Configuration
@EnableConfigurationProperties({AdvisorProperties.class, RagProperties.class})
public class AiConfiguration {

    @Value("classpath:prompt/systemPrompt.st")
    private Resource systemPrompt;

    @Bean("chatClient")
    public ChatClient chatClient(ChatClient.Builder builder, List<Advisor> advisors, SpringAiProperties properties) {
        return builder
                .defaultSystem(system -> system.text(this.systemPrompt))
                .defaultAdvisors(advisors)
                .defaultOptions(OpenAiChatOptions.builder()
                        .temperature(properties.chat().temperature())
                        .maxCompletionTokens(properties.chat().maxCompletionTokens()))
                .build();
    }

    @Bean("rewriteChatModel")
    public OpenAiChatModel rewriteChatModel(RagProperties properties) {
        final RagProperties.Rewrite rewrite = properties.rewrite();
        return OpenAiChatModel.builder().options(OpenAiChatOptions.builder()
                        .apiKey(rewrite.apiKey())
                        .baseUrl(rewrite.baseUrl())
                        .model(rewrite.chat().model())
                        .temperature(rewrite.chat().temperature())
                        .maxTokens(rewrite.chat().maxTokens())
                        .build())
                .build();
    }

    @Bean("rewriteChatClient")
    public ChatClient rewriteChatClient(@Qualifier("rewriteChatModel") OpenAiChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }

    @Bean
    public List<Advisor> advisors(MessageChatMemoryAdvisor memoryAdvisor) {
        return List.of(memoryAdvisor);
    }

    @Bean
    public ChatMemory chatMemory(JdbcChatMemoryRepository jdbcChatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .maxMessages(2)
                .build();
    }

    @Bean
    public MessageChatMemoryAdvisor memoryAdvisor(ChatMemory chatMemory) {
        return MessageChatMemoryAdvisor.builder(chatMemory).build();
    }
}
