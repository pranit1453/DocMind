package com.pranit.docmind.ai.advisor;

import com.pranit.docmind.ai.dto.RetrievalOptions;
import org.springframework.ai.chat.client.advisor.api.Advisor;

import java.util.UUID;

@FunctionalInterface
public interface RetrievalAugmentedGenerationAdvisor {

    Advisor retrievalAugmentedGenerationWorkflow(UUID documentId, RetrievalOptions options);
}
