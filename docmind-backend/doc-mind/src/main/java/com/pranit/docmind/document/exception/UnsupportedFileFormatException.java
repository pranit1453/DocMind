package com.pranit.docmind.document.exception;

import com.pranit.docmind.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UnsupportedFileFormatException extends BaseException {
    public UnsupportedFileFormatException(String message) {
        super(message, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }
}
