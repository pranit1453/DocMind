package com.pranit.docmind.rag.module.preretrieval;

import org.springframework.ai.rag.preretrieval.query.expansion.QueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;

public interface Transformer {
    
    QueryTransformer rewriteTransformer();

    QueryTransformer translationTransformer();

    QueryExpander queryExpander();
}
