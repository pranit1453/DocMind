package com.pranit.docmind.security.service;

import com.pranit.docmind.entities.model.UserDetail;

public interface GenerateToken {

    String generateAccessToken(UserDetail userDetail, String sessionId);

    String generateRefreshToken(UserDetail userDetail, String jti, String sessionId);
}
