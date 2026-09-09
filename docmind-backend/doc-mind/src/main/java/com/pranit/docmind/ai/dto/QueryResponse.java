package com.pranit.docmind.ai.dto;

import com.pranit.docmind.rag.dto.Context;
import lombok.Builder;

import java.util.List;

@Builder
public record QueryResponse(
        String content,
        String responseTime,
        List<Context.Citation> citations
) {
}
