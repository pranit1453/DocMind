package com.pranit.docmind.authentication.service;

import com.pranit.docmind.entities.model.UserDetail;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.UUID;

public interface UserDetailService extends UserDetailsService {
    
    UserDetail loadUserByUserId(UUID userId);
}
