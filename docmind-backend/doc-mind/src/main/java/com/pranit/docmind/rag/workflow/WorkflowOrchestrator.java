package com.pranit.docmind.rag.workflow;

import com.pranit.docmind.ai.dto.QueryRequest;
import com.pranit.docmind.entities.entity.DocumentMetadata;
import com.pranit.docmind.rag.dto.Context;
import org.jspecify.annotations.NullMarked;
import org.springframework.ai.document.Document;

import java.util.List;

@NullMarked
public abstract class WorkflowOrchestrator {

    public final Context execute(final DocumentMetadata metadata, final String query, final QueryRequest.Options options) {
        final var rewrittenQuery = rewrite(query);
        final var documents = retrieve(metadata, rewrittenQuery, options);
        final var citations = createCitations(documents);
        final var context = augment(rewrittenQuery, documents);
        return Context.builder()
                .originalQuery(query)
                .rewrittenQuery(rewrittenQuery)
                .context(context)
                .documents(documents)
                .citations(citations)
                .build();
    }

    private List<Document> retrieve(DocumentMetadata metadata, String rewrittenQuery, QueryRequest.Options options) {
        return switch (options.queryType()) {
            case DOCUMENT_SUMMARY, TECHNICAL_INSIGHTS -> searchDocumentChunks(metadata, rewrittenQuery);
            case NORMAL_QA -> similaritySearch(metadata, rewrittenQuery, options.retrieval());
        };
    }

    protected abstract String rewrite(final String query);

    protected abstract List<Document> similaritySearch(final DocumentMetadata metadata, final String query, final QueryRequest.RetrievalOptions options);

    protected abstract List<Document> searchDocumentChunks(final DocumentMetadata metadata, final String query);

    protected abstract List<Context.Citation> createCitations(final List<Document> documents);

    protected abstract String augment(final String query, final List<Document> documents);
}