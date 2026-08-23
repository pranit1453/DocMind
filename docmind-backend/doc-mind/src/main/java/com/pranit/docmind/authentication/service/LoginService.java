package com.pranit.docmind.authentication.service;

import com.pranit.docmind.authentication.dto.LoginRequest;
import com.pranit.docmind.authentication.dto.LoginRespone;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@FunctionalInterface
public interface LoginService {

    LoginRespone authenticateUser(@Valid LoginRequest request, @Valid HttpServletRequest httpRequest, @Valid HttpServletResponse response);
}
