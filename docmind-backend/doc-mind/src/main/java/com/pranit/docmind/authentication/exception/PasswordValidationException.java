package com.pranit.docmind.authentication.exception;

import com.pranit.docmind.exception.BaseException;
import org.springframework.http.HttpStatus;

public class PasswordValidationException extends BaseException {
    public PasswordValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
