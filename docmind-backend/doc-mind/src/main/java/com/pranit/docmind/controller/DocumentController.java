package com.pranit.docmind.controller;

import com.pranit.docmind.document.dto.DocumentResponse;
import com.pranit.docmind.document.service.DocumentService;
import com.pranit.docmind.document.service.DocumentUploadService;
import com.pranit.docmind.wrapper.ApiResponse;
import com.pranit.docmind.wrapper.ScrollResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Document Management",
        description = "Endpoints for uploading, retrieving, listing, and deleting documents and their vector embeddings."
)
public class DocumentController {

    private final DocumentUploadService documentUploadService;
    private final DocumentService documentService;

    @Operation(summary = "Upload a document", description = "Uploads a document and processes it for vector embedding.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, version = "v1")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<DocumentResponse>> uploadDocument(@Valid @RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(documentUploadService.uploadDocument(file));
    }

    @Operation(summary = "Get document by ID", description = "Retrieves a document and its metadata using its unique ID.")
    @GetMapping(value = "/{documentId}", version = "v1")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<DocumentResponse>> fetchDocumentById(@NotNull @PathVariable("documentId") UUID documentId) {
        return ResponseEntity.status(HttpStatus.OK).body(documentService.fetchDocumentById(documentId));
    }

    @Operation(summary = "List documents", description = "Retrieves a paginated list of documents with optional keyword search and sorting.")
    @GetMapping(version = "v1")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ScrollResponse<DocumentResponse>> fetchAllDocuments(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String scrollId,
            @RequestParam(defaultValue = "5") @Min(1) @Max(5) int pageSize,
            @RequestParam(defaultValue = "fileName") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(defaultValue = "FORWARD") String scrollDirection
    ) {
        final ScrollResponse<DocumentResponse> responses = documentService.fetchDocuments(keyword, scrollId, pageSize, sortBy, sortDirection, scrollDirection);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @Operation(summary = "Delete a document", description = "Deletes a document and its associated vector embeddings using its unique ID.")
    @DeleteMapping(value = "/{documentId}/delete", version = "v1")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteDocumentById(@NotNull @PathVariable("documentId") UUID documentId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(documentService.deleteDocumentById(documentId));
    }
}
