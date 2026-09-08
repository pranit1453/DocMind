package com.pranit.docmind.wrapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.Instant;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean status,
        String message,
        T data,
        Instant timestamp
) {
}
