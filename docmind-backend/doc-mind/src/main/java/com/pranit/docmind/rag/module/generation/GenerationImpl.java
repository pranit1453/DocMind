package com.pranit.docmind.rag.module.generation;

import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.generation.augmentation.QueryAugmenter;
import org.springframework.stereotype.Service;

@Service
public class GenerationImpl implements Generation {

    @Override
    public QueryAugmenter augmentQuery() {
        return ContextualQueryAugmenter.builder()
                .allowEmptyContext(false)
                .build();
    }
}
