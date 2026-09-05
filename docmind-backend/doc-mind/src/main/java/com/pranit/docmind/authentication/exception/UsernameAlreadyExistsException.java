package com.pranit.docmind.authentication.exception;

import com.pranit.docmind.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UsernameAlreadyExistsException extends BaseException {
    public UsernameAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
