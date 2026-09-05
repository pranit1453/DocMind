package com.pranit.docmind.security.service;

import jakarta.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface ResolveAccessToken {

    String getAccessTokenFromRequest(HttpServletRequest request);
}
