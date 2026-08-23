package com.pranit.docmind.document.exception;

import com.pranit.docmind.exception.BaseException;
import org.springframework.http.HttpStatus;

public class DocumentProcessingException extends BaseException {
    public DocumentProcessingException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_CONTENT);
    }
}
