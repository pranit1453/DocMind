package com.pranit.docmind.document.service;

import com.pranit.docmind.document.dto.DocumentResponse;
import com.pranit.docmind.wrapper.ApiResponse;
import com.pranit.docmind.wrapper.PageResponse;

import java.util.UUID;

public interface DocumentService {
    ApiResponse<DocumentResponse> fetchDocumentById(UUID documentId);

    PageResponse<DocumentResponse> fetchDocuments(int page, int size, String keyword, String sortBy, String sortDirection);

    ApiResponse<Void> deleteDocumentById(UUID documentId);
}
