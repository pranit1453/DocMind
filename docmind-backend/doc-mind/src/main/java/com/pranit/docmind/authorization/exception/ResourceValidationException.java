package com.pranit.docmind.authorization.exception;

import com.pranit.docmind.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ResourceValidationException extends BaseException {
    public ResourceValidationException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
