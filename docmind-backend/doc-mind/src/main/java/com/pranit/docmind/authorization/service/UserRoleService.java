package com.pranit.docmind.authorization.service;

import com.pranit.docmind.authorization.dto.AssignResponse;
import com.pranit.docmind.authorization.dto.AssignUserRoleRequest;
import com.pranit.docmind.authorization.dto.RevokeResponse;
import com.pranit.docmind.authorization.dto.RevokeUserRoleRequest;
import com.pranit.docmind.authorization.dto.UserRoleResponse;
import com.pranit.docmind.entities.entity.Role;
import com.pranit.docmind.wrapper.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public interface UserRoleService {

    void addRoleToUser(UUID userId, Role role);

    AssignResponse assignRoleToUser(@Valid AssignUserRoleRequest request);

    RevokeResponse revokeRoleAssignedToUser(@NotNull Long userRoleId, @Valid RevokeUserRoleRequest request);

    AssignResponse reAssignRoleToUser(@NotNull Long userRoleId, @Valid AssignUserRoleRequest request);

    UserRoleResponse fetchUserRoleById(@NotNull Long userRoleId);

    PageResponse<UserRoleResponse> findAllRolesForParticularUser(int page, int size, String keyword, String sortBy, String sortDirection);
}
