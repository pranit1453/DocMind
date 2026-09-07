package com.pranit.docmind.rag.pipeline.document.enricher.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record SemanticMetadata(
        List<String> keywords,
        List<String> topics,
        String summary,
        String sectionTitle
) {
    public List<String> safeKeywords() {
        return keywords == null ? List.of() : keywords;
    }

    public List<String> safeTopics() {
        return topics == null ? List.of() : topics;
    }

    public String safeSummary() {
        return summary == null ? "" : summary;
    }

    public String safeSectionTitle() {
        return sectionTitle == null ? "" : sectionTitle;
    }
}
