package com.pranit.docmind.ai.exception;

import com.pranit.docmind.exception.BaseException;
import org.springframework.http.HttpStatus;

public class PromptNotFoundException extends BaseException {
    public PromptNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
