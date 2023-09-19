package com.my.example.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public enum CommonExceptionResponse {

    BAD_REQUEST_EXCEPTION(HttpStatus.BAD_REQUEST, "E001", "This should be application specific"),
    ACCESS_DENIED_EXCEPTION(HttpStatus.UNAUTHORIZED, "E002","Access denied"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E003"),
    RUNTIME_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "E001"),
    SECURITY_AUTHENTICATION(HttpStatus.UNAUTHORIZED, "S001", "Authentication failed"),

    SECURITY_ACCOUNT_NOTFOUND(HttpStatus.UNAUTHORIZED, "S002", "User account is not found"),
    SECURITY_ACCOUNT_LOCK(HttpStatus.UNAUTHORIZED, "S003", "User account is locked"),
    SECURITY_ACCOUNT_DORMANT(HttpStatus.UNAUTHORIZED, "S004", "User account is dormant");

    private final HttpStatus status;
    private final String code;
    private String message;

    CommonExceptionResponse(HttpStatus status, String code) {
        this.status = status;
        this.code = code;
    }

    CommonExceptionResponse(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
