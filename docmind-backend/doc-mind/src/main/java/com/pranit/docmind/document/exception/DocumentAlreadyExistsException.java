package com.pranit.docmind.document.exception;

import com.pranit.docmind.exception.BaseException;
import org.springframework.http.HttpStatus;

public class DocumentAlreadyExistsException extends BaseException {
    public DocumentAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
