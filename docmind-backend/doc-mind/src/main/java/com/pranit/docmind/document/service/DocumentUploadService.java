package com.pranit.docmind.document.service;

import com.pranit.docmind.document.dto.DocumentResponse;
import com.pranit.docmind.wrapper.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

@FunctionalInterface
public interface DocumentUploadService {

    ApiResponse<DocumentResponse> uploadDocument(MultipartFile file);

}
