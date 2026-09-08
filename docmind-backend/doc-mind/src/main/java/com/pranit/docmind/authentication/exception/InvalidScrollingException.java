package com.pranit.docmind.authentication.exception;

import com.pranit.docmind.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidScrollingException extends BaseException {
    public InvalidScrollingException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
