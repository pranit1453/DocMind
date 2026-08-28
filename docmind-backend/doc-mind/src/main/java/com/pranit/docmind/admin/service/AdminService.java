package com.pranit.docmind.admin.service;

import com.pranit.docmind.admin.dto.UserAccountControlRequest;
import com.pranit.docmind.admin.dto.UserResponse;
import com.pranit.docmind.wrapper.ApiResponse;
import com.pranit.docmind.wrapper.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public interface AdminService {
    
    PageResponse<UserResponse> fetchUsers(int page, int size, String keyword, String sortBy, String sortDirection);

    ApiResponse<Void> controlUserAccount(@NotNull UUID userId, @Valid UserAccountControlRequest request);
}
