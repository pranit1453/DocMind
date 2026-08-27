package com.pranit.docmind.document.service.impl;

import com.pranit.docmind.authentication.exception.InvalidScrollingException;
import com.pranit.docmind.document.dto.DocumentResponse;
import com.pranit.docmind.document.exception.DocumentNotFoundException;
import com.pranit.docmind.document.helper.ScrollPositionCodec;
import com.pranit.docmind.document.repository.DocumentRepository;
import com.pranit.docmind.document.service.DocumentService;
import com.pranit.docmind.document.specification.DocumentSpecification;
import com.pranit.docmind.entities.entity.DocumentMetadata;
import com.pranit.docmind.helper.SecurityContext;
import com.pranit.docmind.wrapper.ApiResponse;
import com.pranit.docmind.wrapper.ScrollResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
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
        final DocumentMetadata metadata = validateAndFetchDocumentById(documentId);
        return ApiResponse.<DocumentResponse>builder()
                .status(true)
                .message("Document fetched successfully")
                .data(DocumentResponse.builder()
                        .documentId(metadata.getDocumentId())
                        .fileName(metadata.getFileName())
                        .fileSize(metadata.getFileSize())
                        .status(metadata.getFileStatus())
                        .chunksCreated(metadata.getChunksCreated())
                        .createdAt(metadata.getCreatedAt())
                        .build())
                .timestamp(Instant.now())
                .build();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public ScrollResponse<DocumentResponse> fetchDocuments(
            final String keyword, final String scrollId,
            final int pageSize, final String sortBy,
            final String sortDirection, final String scrollDirection) {
        final UUID userId = SecurityContext.getCurrentUserId();
        final ScrollPositionCodec.ScrollDirection requestedDirection = parseScrollDirection(scrollDirection);
        final boolean initialRequest = scrollId == null || scrollId.isBlank();
        if (initialRequest && requestedDirection == ScrollPositionCodec.ScrollDirection.BACKWARD) {
            throw new InvalidScrollingException("Backward scrolling requires a scrollId");
        }
        final ScrollPositionCodec.DecodedScrollPosition decoded = ScrollPositionCodec.decode(scrollId);
        final ScrollPosition position = decoded.position();

        if (!initialRequest && decoded.direction() != requestedDirection) {
            throw new InvalidScrollingException("scrollId direction does not match scrollDirection");
        }

        final Specification<DocumentMetadata> specification = DocumentSpecification.searchKeyword(keyword, userId);
        final String validatedSortBy = validateAndMapSortField(sortBy);
        final Sort.Direction requestedSortDirection = parseSortDirection(sortDirection);
        final Sort sort = buildSort(validatedSortBy, requestedSortDirection);
        final Window<DocumentMetadata> window = documentRepository.findBy(
                specification, query -> query
                        .limit(pageSize)
                        .sortBy(sort)
                        .scroll(position));
        // Maintain DB order
        final List<DocumentMetadata> databaseDocuments = new ArrayList<>(window.getContent());
        ScrollPosition firstDatabasePosition = null;
        ScrollPosition lastDatabasePosition = null;
        if (!databaseDocuments.isEmpty()) {
            firstDatabasePosition = window.positionAt(0);
            lastDatabasePosition = window.positionAt(window.size() - 1);
        }
        final List<DocumentMetadata> documents = new ArrayList<>(databaseDocuments);
        if (requestedDirection == ScrollPositionCodec.ScrollDirection.BACKWARD) {
            Collections.reverse(documents);
        }
        final List<DocumentResponse> responses = documents.stream()
                .map(this::toResponse)
                .toList();
        String nextScrollId = null;
        String previousScrollId = null;
        boolean hasNext = false;
        boolean hasPrevious = false;
        if (!documents.isEmpty()) {
            // FORWARD REQUEST
            if (requestedDirection == ScrollPositionCodec.ScrollDirection.FORWARD) {
                // NEXT
                if (window.hasNext()) {
                    nextScrollId = ScrollPositionCodec.encode(lastDatabasePosition, ScrollPositionCodec.ScrollDirection.FORWARD);
                    hasNext = true;
                }
                // PREVIOUS
                if (!initialRequest) {
                    previousScrollId = ScrollPositionCodec.encode(firstDatabasePosition, ScrollPositionCodec.ScrollDirection.BACKWARD);
                    hasPrevious = true;
                }
            }
            // BACKWARD REQUEST
            else {
                // NEXT
                nextScrollId = ScrollPositionCodec.encode(firstDatabasePosition, ScrollPositionCodec.ScrollDirection.FORWARD);
                hasNext = true;
                // PREVIOUS
                if (window.hasNext()) {
                    previousScrollId = ScrollPositionCodec.encode(lastDatabasePosition, ScrollPositionCodec.ScrollDirection.BACKWARD);
                    hasPrevious = true;
                }
            }
        }
        return ScrollResponse.<DocumentResponse>builder()
                .contents(responses)
                .nextScrollId(nextScrollId)
                .prevScrollId(previousScrollId)
                .hasNext(hasNext)
                .hasPrevious(hasPrevious)
                .pageSize(pageSize)
                .build();
    }

    private Sort buildSort(final String sortField, final Sort.Direction direction) {
        if ("documentId".equals(sortField)) return Sort.by(direction, "documentId");
        return Sort.by(direction, sortField).and(Sort.by(direction, "documentId"));
    }

    private String validateAndMapSortField(final String sortBy) {
        if (sortBy == null || sortBy.isBlank() || "name".equalsIgnoreCase(sortBy)) return "fileName";
        throw new IllegalArgumentException("Invalid sortBy: " + sortBy);
    }

    private Sort.Direction parseSortDirection(final String sortDirection) {
        if (sortDirection == null || sortDirection.isBlank()) return Sort.Direction.ASC;
        if ("ASC".equalsIgnoreCase(sortDirection)) return Sort.Direction.ASC;
        if ("DESC".equalsIgnoreCase(sortDirection)) return Sort.Direction.DESC;
        throw new IllegalArgumentException("Invalid sortDirection. " + "Allowed values: ASC or DESC");
    }

    private ScrollPositionCodec.ScrollDirection parseScrollDirection(final String scrollDirection) {
        if (scrollDirection == null || scrollDirection.isBlank()) return ScrollPositionCodec.ScrollDirection.FORWARD;
        try {
            return ScrollPositionCodec.ScrollDirection.valueOf(scrollDirection.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid scrollDirection. " + "Allowed values: FORWARD or BACKWARD");
        }
    }

    private DocumentResponse toResponse(final DocumentMetadata metadata) {
        return DocumentResponse.builder()
                .documentId(metadata.getDocumentId())
                .fileName(metadata.getFileName())
                .fileSize(metadata.getFileSize())
                .status(metadata.getFileStatus())
                .chunksCreated(metadata.getChunksCreated())
                .build();
    }

    private DocumentMetadata validateAndFetchDocumentById(final UUID documentId) {
        final UUID userId = SecurityContext.getCurrentUserId();
        return documentRepository.findByDocumentIdAndUser_UserId(documentId, userId)
                .orElseThrow(() -> {
                    log.warn("Document with id {} not found for user {}", documentId, userId);
                    return new DocumentNotFoundException("Document not found");
                });
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ApiResponse<Void> deleteDocumentById(final UUID documentId) {
        final DocumentMetadata metadata = validateAndFetchDocumentById(documentId);
        documentRepository.delete(metadata);
        return ApiResponse.<Void>builder()
                .status(true)
                .message("Document deleted successfully.")
                .timestamp(Instant.now())
                .build();
    }
}
