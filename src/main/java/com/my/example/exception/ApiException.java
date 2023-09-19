package com.my.example.exception;

import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;

@Getter
public class ApiException extends RuntimeException {
    @Nullable
    private ResponseEntity responseEntity;

    public ApiException(String message, ResponseEntity t) {
        super(message);
        this.responseEntity = t;
    }
}
