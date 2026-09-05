package com.pranit.docmind.security.exception;

import com.pranit.docmind.exception.BaseException;
import org.springframework.http.HttpStatus;

public class KeyExtensionException extends BaseException {
    public KeyExtensionException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
