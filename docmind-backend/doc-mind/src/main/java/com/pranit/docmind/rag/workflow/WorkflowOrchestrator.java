package com.pranit.docmind.rag.workflow;

import com.pranit.docmind.ai.dto.RetrievalOptions;
import com.pranit.docmind.rag.dto.Context;
import org.jspecify.annotations.NullMarked;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.UUID;

@NullMarked
public abstract class WorkflowOrchestrator {

    public final Context execute(final UUID documentId, final String query, final RetrievalOptions options) {
        final var rewrittenQuery = rewrite(query);
        final var documents = similaritySearch(documentId, rewrittenQuery, options);
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

    protected abstract String rewrite(final String query);

    protected abstract List<Document> similaritySearch(final UUID documentId, final String query, final RetrievalOptions options);

    protected abstract List<Context.Citation> createCitations(final List<Document> documents);

    protected abstract String augment(final String query, final List<Document> documents);
}