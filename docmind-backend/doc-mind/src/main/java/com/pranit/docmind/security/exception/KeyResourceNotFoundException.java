package com.pranit.docmind.security.exception;

import com.pranit.docmind.exception.BaseException;
import org.springframework.http.HttpStatus;

public class KeyResourceNotFoundException extends BaseException {
    public KeyResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
