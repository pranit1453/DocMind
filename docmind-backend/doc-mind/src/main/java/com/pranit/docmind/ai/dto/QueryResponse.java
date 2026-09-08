package com.pranit.docmind.ai.dto;

import lombok.Builder;

@Builder
public record QueryResponse(
        String content,
        String responseTime
) {
}
