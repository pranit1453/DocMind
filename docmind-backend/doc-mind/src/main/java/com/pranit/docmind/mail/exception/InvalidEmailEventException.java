package com.pranit.docmind.mail.exception;

import com.pranit.docmind.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidEmailEventException extends BaseException {
    public InvalidEmailEventException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
