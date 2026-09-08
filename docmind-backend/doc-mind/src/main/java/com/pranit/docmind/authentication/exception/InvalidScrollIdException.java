package com.pranit.docmind.authentication.exception;

import com.pranit.docmind.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidScrollIdException extends BaseException {
    public InvalidScrollIdException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
