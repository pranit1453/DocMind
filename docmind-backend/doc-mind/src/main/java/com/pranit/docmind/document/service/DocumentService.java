package com.pranit.docmind.document.service;

import com.pranit.docmind.document.dto.DocumentResponse;
import com.pranit.docmind.wrapper.ApiResponse;
import com.pranit.docmind.wrapper.ScrollResponse;

import java.util.UUID;

public interface DocumentService {
    ApiResponse<DocumentResponse> fetchDocumentById(UUID documentId);

    ScrollResponse<DocumentResponse> fetchDocuments(String keyword, String scrollId, int pageSize, String sortBy, String sortDirection, String scrollDirection);

    ApiResponse<Void> deleteDocumentById(UUID documentId);
}
