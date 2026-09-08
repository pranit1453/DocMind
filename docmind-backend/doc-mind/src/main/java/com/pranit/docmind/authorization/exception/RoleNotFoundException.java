package com.pranit.docmind.authorization.exception;

import com.pranit.docmind.exception.BaseException;
import org.springframework.http.HttpStatus;

public class RoleNotFoundException extends BaseException {
    public RoleNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
