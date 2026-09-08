package com.pranit.docmind.wrapper;

import lombok.Builder;

import java.util.List;

@Builder
public record ScrollResponse<T>(
        boolean hasPrevious,
        String prevScrollId,
        List<T> contents,
        boolean hasNext,
        String nextScrollId,
        int pageSize
) {
}
