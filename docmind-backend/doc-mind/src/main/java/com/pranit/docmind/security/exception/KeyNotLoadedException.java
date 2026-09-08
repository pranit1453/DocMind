package com.pranit.docmind.security.exception;

import com.pranit.docmind.exception.BaseException;
import org.springframework.http.HttpStatus;

public class KeyNotLoadedException extends BaseException {
    public KeyNotLoadedException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
