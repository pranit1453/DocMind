package com.pranit.docmind.authentication.service;

import com.pranit.docmind.authentication.dto.SignupRequest;
import com.pranit.docmind.authentication.dto.SignupResponse;
import jakarta.validation.Valid;

public interface RegistrationService {

    SignupResponse createNewUserAccount(@Valid SignupRequest request);

    SignupResponse createNewAdminAccount(@Valid SignupRequest request);
}
