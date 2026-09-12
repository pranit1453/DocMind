package com.pranit.docmind.rag.workflow;

import com.pranit.docmind.ai.dto.QueryRequest;
import com.pranit.docmind.entities.entity.DocumentMetadata;
import com.pranit.docmind.rag.dto.Context;
import com.pranit.docmind.rag.module.generation.QueryAugmenter;
import com.pranit.docmind.rag.module.preretrieval.QueryRewriteTransformer;
import com.pranit.docmind.rag.module.retrieval.DocumentRetriever;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.List;

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
    protected List<Document> similaritySearch(final DocumentMetadata metadata, final String query, final QueryRequest.RetrievalOptions options) {
        return documentRetriever.similaritySearch(metadata, query, options);
    }

    @Override
    protected List<Document> searchDocumentChunks(final DocumentMetadata metadata, final String query) {
        return documentRetriever.searchDocumentChunks(metadata, query);
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
