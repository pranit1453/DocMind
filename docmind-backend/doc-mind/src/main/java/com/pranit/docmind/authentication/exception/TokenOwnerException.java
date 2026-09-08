package com.pranit.docmind.authentication.exception;

import com.pranit.docmind.exception.BaseException;
import org.springframework.http.HttpStatus;

public class TokenOwnerException extends BaseException {
    public TokenOwnerException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
