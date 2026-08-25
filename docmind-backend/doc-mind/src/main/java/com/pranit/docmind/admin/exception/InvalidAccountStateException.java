package com.pranit.docmind.admin.exception;

import com.pranit.docmind.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidAccountStateException extends BaseException {

    public InvalidAccountStateException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
