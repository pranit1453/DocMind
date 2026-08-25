package com.pranit.docmind.document.service.impl;

import com.pranit.docmind.document.dto.DocumentResponse;
import com.pranit.docmind.document.exception.DocumentNotFoundException;
import com.pranit.docmind.document.repository.DocumentRepository;
import com.pranit.docmind.document.service.DocumentService;
import com.pranit.docmind.document.specification.DocumentSpecification;
import com.pranit.docmind.entities.entity.Document;
import com.pranit.docmind.helper.SecurityContext;
import com.pranit.docmind.wrapper.ApiResponse;
import com.pranit.docmind.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public ApiResponse<DocumentResponse> fetchDocumentById(final UUID documentId) {
        final Document document = validateAndFetchDocumentById(documentId);
        return ApiResponse.<DocumentResponse>builder()
                .status(true)
                .message("Document fetched successfully")
                .data(DocumentResponse.builder()
                        .documentId(document.getDocumentId())
                        .fileName(document.getFileName())
                        .fileSize(document.getFileSize())
                        .status(document.getFileStatus())
                        .chunksCreated(document.getChunksCreated())
                        .createdAt(document.getCreatedAt())
                        .build())
                .timestamp(Instant.now())
                .build();
    }

    private Document validateAndFetchDocumentById(final UUID documentId) {
        final UUID userId = SecurityContext.getCurrentUserId();
        return documentRepository.findByDocumentIdAndUser_UserId(documentId, userId)
                .orElseThrow(() -> {
                    log.warn("Document with id {} not found for user {}", documentId, userId);
                    return new DocumentNotFoundException("Document not found");
                });
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public PageResponse<DocumentResponse> fetchDocuments(final int page, final int size, final String keyword, final String sortBy, final String sortDirection) {
        final Sort sort = sortDirection.equalsIgnoreCase("ASC")
                ? Sort.by(Sort.Direction.ASC, sortBy)
                : Sort.by(Sort.Direction.DESC, sortBy);
        final Pageable pageable = PageRequest.of(page, size, sort);
        final UUID userId = SecurityContext.getCurrentUserId();
        final Specification<Document> specification = DocumentSpecification.searchKeyword(keyword, userId);
        final Page<Document> pages = documentRepository.findAll(specification, pageable);
        final List<DocumentResponse> contents = pages.getContent()
                .stream()
                .map(document -> DocumentResponse.builder()
                        .documentId(document.getDocumentId())
                        .fileName(document.getFileName())
                        .fileSize(document.getFileSize())
                        .status(document.getFileStatus())
                        .chunksCreated(document.getChunksCreated())
                        .createdAt(document.getCreatedAt())
                        .build())
                .toList();
        return PageResponse.<DocumentResponse>builder()
                .contents(contents)
                .currentPage(pages.getNumber())
                .totalPages(pages.getSize())
                .totalElements(pages.getTotalElements())
                .totalPages(pages.getTotalPages())
                .isLastPage(pages.isLast())
                .isFirstPage(pages.isFirst())
                .build();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ApiResponse<Void> deleteDocumentById(final UUID documentId) {
        final Document document = validateAndFetchDocumentById(documentId);
        documentRepository.delete(document);
        return ApiResponse.<Void>builder()
                .status(true)
                .message("Document deleted successfully.")
                .timestamp(Instant.now())
                .build();
    }
}
