package com.pranit.docmind.authentication.exception;

import com.pranit.docmind.exception.BaseException;
import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends BaseException {
    public EmailAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
