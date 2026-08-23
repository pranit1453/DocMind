package com.pranit.docmind.ai.configuration;

import com.pranit.docmind.properties.AdvisorProperties;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.List;

@Configuration
@EnableConfigurationProperties({AdvisorProperties.class})
public class AiConfiguration {

    @Value("classpath:prompt/systemPrompt.st")
    private Resource systemPrompt;

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, List<Advisor> advisors) {
        return builder
                .defaultSystem(system -> system.text(this.systemPrompt))
                .defaultAdvisors(advisors)
                .build();
    }

    @Bean
    public ChatClient ragChatClient(ChatModel chatModel) {
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
                .maxMessages(10)
                .build();
    }

    @Bean
    public MessageChatMemoryAdvisor memoryAdvisor(ChatMemory chatMemory) {
        return MessageChatMemoryAdvisor.builder(chatMemory).build();
    }
}
