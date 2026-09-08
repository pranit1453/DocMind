package com.pranit.docmind.ai.exception;

import com.pranit.docmind.exception.BaseException;
import org.springframework.http.HttpStatus;

public class AiServiceUnavailableException extends BaseException {
    public AiServiceUnavailableException(String message) {
        super(message, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
