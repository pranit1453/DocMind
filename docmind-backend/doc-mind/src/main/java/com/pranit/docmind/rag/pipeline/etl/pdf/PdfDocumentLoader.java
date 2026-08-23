package com.pranit.docmind.rag.pipeline.etl.pdf;

import com.pranit.docmind.rag.pipeline.etl.Loader;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public final class PdfDocumentLoader implements Loader {

    private final VectorStore vectorStore;

    @Override
    public void load(final UUID documentId, final List<Document> documents) {
        documents.forEach(document -> document.getMetadata().put(
                "documentId", documentId.toString()));
        this.vectorStore.add(documents);
    }
}
