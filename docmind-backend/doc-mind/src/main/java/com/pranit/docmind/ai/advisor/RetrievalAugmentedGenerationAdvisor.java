package com.pranit.docmind.ai.advisor;

import org.springframework.ai.chat.client.advisor.api.Advisor;

import java.util.UUID;

@FunctionalInterface
public interface RetrievalAugmentedGenerationAdvisor {

    Advisor retrievalAugmentedGenerationWorkflow(UUID documentId);
}
