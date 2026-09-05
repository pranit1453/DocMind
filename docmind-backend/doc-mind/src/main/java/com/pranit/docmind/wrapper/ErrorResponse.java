package com.pranit.docmind.wrapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.Instant;
import java.util.Map;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int status,
        String message,
        Map<String, String> errors,
        String path,
        Instant timestamp
) {
}