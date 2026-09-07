package com.pranit.docmind.rag.pipeline.document.enricher.impl;

import com.pranit.docmind.constant.DocMetadata;
import com.pranit.docmind.rag.pipeline.document.enricher.DocumentMetadataEnricher;
import com.pranit.docmind.rag.pipeline.document.enricher.dto.SemanticMetadata;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.LinkedHashMap;
import java.util.List;

public class AiDocumentMetadataEnricher implements DocumentMetadataEnricher {

    private static final String SYSTEM_PROMPT = """
            You are a document metadata extraction system.
            
            Analyze the provided document chunk.
            
            Extract metadata ONLY from information explicitly
            present in the document chunk.
            
            Do not infer, guess, or add external knowledge.
            
            Generate:
            - 5 to 8 important keywords
            - important topics
            - a concise factual summary
            - the section title if identifiable
            
            Output rules:
            - Never return null.
            - keywords must be an array.
            - topics must be an array.
            - summary must be a string.
            - sectionTitle must be a string.
            - Use an empty array/string when information is unavailable.
            
            The metadata must represent the content faithfully
            because it will be used for document retrieval.
            """;

    private final ChatClient enrichChatClient;

    public AiDocumentMetadataEnricher(@Qualifier("enrichChatClient") final ChatClient enrichChatClient) {
        this.enrichChatClient = enrichChatClient;
    }

    @Override
    public List<Document> enrich(final List<Document> documents) {
        return documents.stream()
                .map(this::enrichDocument)
                .toList();
    }

    private Document enrichDocument(final Document document) {
        final var text = document.getText();
        if (text == null || text.isBlank()) return document;
        final var semanticMetadata = enrichChatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(text)
                .call()
                .entity(SemanticMetadata.class);
        if (semanticMetadata == null) return document;
        final var metadata = new LinkedHashMap<>(document.getMetadata());
        metadata.put(DocMetadata.KEYWORDS, semanticMetadata.safeKeywords());
        metadata.put(DocMetadata.TOPICS, semanticMetadata.safeTopics());
        metadata.put(DocMetadata.SECTION_SUMMARY, semanticMetadata.safeSummary());
        metadata.put(DocMetadata.SECTION_TITLE, semanticMetadata.safeSectionTitle());
        return new Document(document.getText(), metadata);
    }
}
