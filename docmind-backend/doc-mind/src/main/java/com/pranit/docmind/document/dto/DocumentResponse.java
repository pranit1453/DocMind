package com.pranit.docmind.document.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pranit.docmind.entities.constant.FileStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DocumentResponse(
        UUID documentId,
        String fileName,
        long fileSize,
        FileStatus status,
        long chunksCreated,
        Instant createdAt
) {
}
