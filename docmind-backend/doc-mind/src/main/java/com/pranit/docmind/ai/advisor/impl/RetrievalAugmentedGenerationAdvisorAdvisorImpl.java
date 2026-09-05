package com.pranit.docmind.ai.advisor.impl;

import com.pranit.docmind.ai.advisor.RetrievalAugmentedGenerationAdvisor;
import com.pranit.docmind.ai.dto.RetrievalOptions;
import com.pranit.docmind.rag.module.generation.Generation;
import com.pranit.docmind.rag.module.preretrieval.Transformer;
import com.pranit.docmind.rag.module.retrieval.Retrievar;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RetrievalAugmentedGenerationAdvisorAdvisorImpl implements RetrievalAugmentedGenerationAdvisor {

    private final Transformer transformer;
    private final Retrievar retriever;
    private final Generation generation;

    @Override
    public Advisor retrievalAugmentedGenerationWorkflow(final UUID documentId, final RetrievalOptions options) {
        return RetrievalAugmentationAdvisor.builder()
                .queryTransformers(transformer.rewriteTransformer(), transformer.translationTransformer())
                .queryExpander(transformer.queryExpander())
                .documentRetriever(retriever.vectorStoreRetriever(documentId, options))
                .documentJoiner(retriever.joinStoreRetriever())
                .queryAugmenter(generation.augmentQuery())
                .build();
    }
}
