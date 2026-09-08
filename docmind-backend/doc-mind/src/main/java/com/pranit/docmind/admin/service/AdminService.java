package com.pranit.docmind.admin.service;

import com.pranit.docmind.admin.dto.UserAccountControlRequest;
import com.pranit.docmind.admin.dto.UserResponse;
import com.pranit.docmind.authorization.dto.AssignResponse;
import com.pranit.docmind.authorization.dto.AssignUserRoleRequest;
import com.pranit.docmind.authorization.dto.RevokeResponse;
import com.pranit.docmind.authorization.dto.RevokeUserRoleRequest;
import com.pranit.docmind.authorization.dto.UserRoleResponse;
import com.pranit.docmind.wrapper.ApiResponse;
import com.pranit.docmind.wrapper.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public interface AdminService {

    PageResponse<UserResponse> fetchUsers(int page, int size, String keyword, String sortBy, String sortDirection);

    ApiResponse<Void> controlUserAccount(@NotNull UUID userId, @Valid UserAccountControlRequest request);

    AssignResponse assignRoleToUser(@Valid AssignUserRoleRequest request);

    RevokeResponse revokeRoleAssignedToUser(@NotNull Long userRoleId, @Valid RevokeUserRoleRequest request);

    AssignResponse reAssignRoleToUser(@NotNull Long userRoleId, @Valid AssignUserRoleRequest request);

    UserRoleResponse fetchUserRoleById(@NotNull Long userRoleId);
}
