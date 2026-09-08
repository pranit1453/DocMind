package com.pranit.docmind.otp.exception;

import com.pranit.docmind.exception.BaseException;
import org.springframework.http.HttpStatus;

public class OTPResendException extends BaseException {
    public OTPResendException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
