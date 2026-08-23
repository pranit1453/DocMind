package com.pranit.docmind.document.exception;

import com.pranit.docmind.exception.BaseException;
import org.springframework.http.HttpStatus;

public class DocumentNotFoundException extends BaseException {
    public DocumentNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
