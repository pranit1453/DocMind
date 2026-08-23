package com.pranit.docmind.authentication.service;

import com.pranit.docmind.authentication.dto.ChangeForgotPasswordRequest;
import com.pranit.docmind.authentication.dto.ChangePassword;
import com.pranit.docmind.authentication.dto.ChangePasswordResponse;
import com.pranit.docmind.authentication.dto.ForgotPasswordEmail;
import com.pranit.docmind.authentication.dto.PasswordResponse;
import jakarta.validation.Valid;

public interface PasswordService {

    PasswordResponse requestPasswordReset(@Valid ForgotPasswordEmail request);

    PasswordResponse resetPassword(@Valid ChangeForgotPasswordRequest request);

    ChangePasswordResponse changeAccountPassword(@Valid ChangePassword request);
}
