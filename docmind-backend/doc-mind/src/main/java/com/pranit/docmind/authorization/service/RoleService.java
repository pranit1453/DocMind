package com.pranit.docmind.authorization.service;

import com.pranit.docmind.authorization.dto.RoleResponse;

import java.util.List;

public interface RoleService {
    
    List<RoleResponse> findAllRoles();
}
