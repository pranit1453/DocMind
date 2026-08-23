package com.pranit.docmind.controller;

import com.pranit.docmind.document.dto.DocumentResponse;
import com.pranit.docmind.document.service.DocumentService;
import com.pranit.docmind.document.service.DocumentUploadService;
import com.pranit.docmind.wrapper.ApiResponse;
import com.pranit.docmind.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
public class DocumentController {

    private final DocumentUploadService documentUploadService;
    private final DocumentService documentService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, version = "v1")
    public ResponseEntity<ApiResponse<DocumentResponse>> uploadDocument(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(documentUploadService.uploadDocument(file));
    }

    @GetMapping(value = "/{documentId}", version = "v1")
    public ResponseEntity<ApiResponse<DocumentResponse>> fetchDocumentById(@PathVariable("documentId") UUID documentId) {
        return ResponseEntity.status(HttpStatus.OK).body(documentService.fetchDocumentById(documentId));
    }

    @GetMapping(version = "v1")
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

    @DeleteMapping(value = "/{documentId}/delete", version = "v1")
    public ResponseEntity<ApiResponse<Void>> deleteDocumentById(@PathVariable("documentId") UUID documentId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(documentService.deleteDocumentById(documentId));
    }
}
