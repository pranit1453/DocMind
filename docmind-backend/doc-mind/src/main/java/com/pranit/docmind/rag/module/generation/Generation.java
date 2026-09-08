package com.pranit.docmind.rag.module.generation;

import org.springframework.ai.rag.generation.augmentation.QueryAugmenter;

public interface Generation {

    QueryAugmenter augmentQuery();
}
