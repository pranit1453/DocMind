package com.pranit.docmind.controller;

import com.pranit.docmind.document.dto.DocumentResponse;
import com.pranit.docmind.document.service.DocumentService;
import com.pranit.docmind.document.service.DocumentUploadService;
import com.pranit.docmind.wrapper.ApiResponse;
import com.pranit.docmind.wrapper.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<DocumentResponse>> uploadDocument(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(documentUploadService.uploadDocument(file));
    }

    @Operation(summary = "Get document by ID", description = "Retrieves a document and its metadata using its unique ID.")
    @GetMapping(value = "/{documentId}", version = "v1")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<DocumentResponse>> fetchDocumentById(@PathVariable("documentId") UUID documentId) {
        return ResponseEntity.status(HttpStatus.OK).body(documentService.fetchDocumentById(documentId));
    }

    @Operation(summary = "List documents", description = "Retrieves a paginated list of documents with optional keyword search and sorting.")
    @GetMapping(version = "v1")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PageResponse<DocumentResponse>> fetchAllDocuments(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "5") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "fileName") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortDirection
    ) {
        final PageResponse<DocumentResponse> responses = documentService.fetchDocuments(page, size, keyword, sortBy, sortDirection);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @Operation(summary = "Delete a document", description = "Deletes a document and its associated vector embeddings using its unique ID.")
    @DeleteMapping(value = "/{documentId}/delete", version = "v1")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> deleteDocumentById(@PathVariable("documentId") UUID documentId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(documentService.deleteDocumentById(documentId));
    }
}
