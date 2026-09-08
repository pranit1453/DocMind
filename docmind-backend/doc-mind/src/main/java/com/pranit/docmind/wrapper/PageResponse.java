package com.pranit.docmind.wrapper;

import lombok.Builder;

import java.util.List;

@Builder
public record PageResponse<T>(
        List<T> contents,
        int currentPage,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean isLastPage,
        boolean isFirstPage
) {
}
