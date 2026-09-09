package com.pranit.docmind.rag.workflow;

import com.pranit.docmind.ai.dto.RetrievalOptions;
import com.pranit.docmind.rag.dto.Context;
import com.pranit.docmind.rag.module.generation.QueryAugmenter;
import com.pranit.docmind.rag.module.preretrieval.QueryRewriteTransformer;
import com.pranit.docmind.rag.module.retrieval.DocumentRetriever;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@NullMarked
@Component
@RequiredArgsConstructor
public class DocumentWorkflowOrchestrator extends WorkflowOrchestrator {

    private final QueryRewriteTransformer rewriteTransformer;
    private final DocumentRetriever documentRetriever;
    private final QueryAugmenter queryAugmenter;

    @Override
    protected String rewrite(final String query) {
        return rewriteTransformer.rewriteQuery(query);
    }

    @Override
    protected List<Document> similaritySearch(final UUID documentId, final String query, final RetrievalOptions options) {
        return documentRetriever.similaritySearch(documentId, query, options);
    }

    @Override
    protected List<Context.Citation> createCitations(final List<Document> documents) {
        return documentRetriever.toCitations(documents);
    }

    @Override
    protected String augment(final String query, final List<Document> documents) {
        return queryAugmenter.augment(query, documents);
    }
}
