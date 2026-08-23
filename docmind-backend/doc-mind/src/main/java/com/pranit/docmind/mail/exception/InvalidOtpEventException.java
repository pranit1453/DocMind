package com.pranit.docmind.mail.exception;

import com.pranit.docmind.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidOtpEventException extends BaseException {
    public InvalidOtpEventException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
