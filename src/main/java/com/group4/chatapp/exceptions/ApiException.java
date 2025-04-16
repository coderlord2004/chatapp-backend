package com.group4.chatapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

public class ApiException extends ErrorResponseException {
    public ApiException(HttpStatus status, String title) {
        super(HttpStatus.NOT_FOUND);
        this.setTitle(title);
    }
}
