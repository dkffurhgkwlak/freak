package com.my.example.common;

import com.my.example.web.dto.BaseExceptionDto;
import com.my.example.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import org.springframework.security.access.AccessDeniedException;

@RestControllerAdvice
@Slf4j
public class CommonExceptionAdvice {
    @ExceptionHandler({ApiException.class})
    public ResponseEntity<?> exceptionHandler(HttpServletRequest request, final ApiException e) {
        log.error("ApiExceptionAdvice : "+e.getMessage());
        return e.getResponseEntity();
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<BaseExceptionDto> exceptionHandler(HttpServletRequest request, final RuntimeException e) {
        e.printStackTrace();
        log.error("RuntimeException : "+e.getMessage());
        return ResponseEntity
                .status(CommonExceptionResponse.RUNTIME_EXCEPTION.getStatus())
                .body(BaseExceptionDto.builder()
                        .errorCode(CommonExceptionResponse.RUNTIME_EXCEPTION.getCode())
                        .errorMessage(CommonExceptionResponse.RUNTIME_EXCEPTION.getMessage())
                        .build());
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<BaseExceptionDto> exceptionHandler(HttpServletRequest request, final Exception e) {
        e.printStackTrace();
        log.error("Exception : "+e.getMessage());
        return ResponseEntity
                .status(CommonExceptionResponse.INTERNAL_SERVER_ERROR.getStatus())
                .body(BaseExceptionDto.builder()
                        .errorCode(CommonExceptionResponse.INTERNAL_SERVER_ERROR.getCode())
                        .errorMessage(e.getMessage())
                        .build());
    }

    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    public ResponseEntity<BaseExceptionDto> exceptionHandler(HttpServletRequest httpServletRequest,
                                                             final HttpMediaTypeNotSupportedException e) {
        log.error("HttpMediaTypeNotSupportedException : "+e.getMessage());
        return ResponseEntity
                .status(CommonExceptionResponse.BAD_REQUEST_EXCEPTION.getStatus())
                .body(BaseExceptionDto.builder()
                        .errorCode(CommonExceptionResponse.BAD_REQUEST_EXCEPTION.getCode())
                        .errorMessage(CommonExceptionResponse.BAD_REQUEST_EXCEPTION.getMessage())
                        .build());
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<BaseExceptionDto> exceptionHandler(HttpServletRequest httpServletRequest,
                                                             final HttpRequestMethodNotSupportedException e) {
        log.error("HttpRequestMethodNotSupportedException : "+e.getMessage());
        return ResponseEntity
                .status(CommonExceptionResponse.BAD_REQUEST_EXCEPTION.getStatus())
                .body(BaseExceptionDto.builder()
                        .errorCode(CommonExceptionResponse.BAD_REQUEST_EXCEPTION.getCode())
                        .errorMessage(CommonExceptionResponse.BAD_REQUEST_EXCEPTION.getMessage())
                        .build());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<BaseExceptionDto> exceptionHandler(HttpServletRequest httpServletRequest,
                                                             final HttpMessageNotReadableException e) {
        log.error("HttpMessageNotReadableException : "+e.getMessage());
        return ResponseEntity
                .status(CommonExceptionResponse.BAD_REQUEST_EXCEPTION.getStatus())
                .body(BaseExceptionDto.builder()
                        .errorCode(CommonExceptionResponse.BAD_REQUEST_EXCEPTION.getCode())
                        .errorMessage(CommonExceptionResponse.BAD_REQUEST_EXCEPTION.getMessage())
                        .build());
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<BaseExceptionDto> exceptionHandler(HttpServletRequest request, final AccessDeniedException e) {

        log.error("AccessDeniedException : "+e.getMessage());
        return ResponseEntity
                .status(CommonExceptionResponse.ACCESS_DENIED_EXCEPTION.getStatus())
                .body(BaseExceptionDto.builder()
                        .errorCode(CommonExceptionResponse.ACCESS_DENIED_EXCEPTION.getCode())
                        .errorMessage(e.getMessage())
                        .build());
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<BaseExceptionDto> exceptionHandler(HttpServletRequest request, final AuthenticationException e) {
        e.printStackTrace();
        log.error("Exception : "+e.getMessage());
        return ResponseEntity
                .status(CommonExceptionResponse.SECURITY_AUTHENTICATION.getStatus())
                .body(BaseExceptionDto.builder()
                        .errorCode(CommonExceptionResponse.SECURITY_AUTHENTICATION.getCode())
                        .errorMessage(e.getMessage())
                        .build());
    }

    @ExceptionHandler({LockedException.class})
    public ResponseEntity<BaseExceptionDto> exceptionHandler(HttpServletRequest httpServletRequest,
                                                             final LockedException e) {
        log.error("LockedException : "+e.getMessage());
        return ResponseEntity
                .status(CommonExceptionResponse.SECURITY_ACCOUNT_LOCK.getStatus())
                .body(BaseExceptionDto.builder()
                        .errorCode(CommonExceptionResponse.SECURITY_ACCOUNT_LOCK.getCode())
                        .errorMessage(CommonExceptionResponse.SECURITY_ACCOUNT_LOCK.getMessage())
                        .build());
    }

    @ExceptionHandler({DisabledException.class})
    public ResponseEntity<BaseExceptionDto> exceptionHandler(HttpServletRequest httpServletRequest,
                                                             final DisabledException e) {
        log.error("DisabledException : "+e.getMessage());
        return ResponseEntity
                .status(CommonExceptionResponse.SECURITY_ACCOUNT_DORMANT.getStatus())
                .body(BaseExceptionDto.builder()
                        .errorCode(CommonExceptionResponse.SECURITY_ACCOUNT_DORMANT.getCode())
                        .errorMessage(CommonExceptionResponse.SECURITY_ACCOUNT_DORMANT.getMessage())
                        .build());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<BaseExceptionDto> exceptionHandler(HttpServletRequest httpServletRequest,
                                                             final MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException : "+e.getMessage());
        return ResponseEntity
                .status(CommonExceptionResponse.BAD_REQUEST_EXCEPTION.getStatus())
                .body(BaseExceptionDto.builder()
                        .errorCode(CommonExceptionResponse.BAD_REQUEST_EXCEPTION.getCode())
                        .errorMessage(CommonExceptionResponse.BAD_REQUEST_EXCEPTION.getMessage())
                        .build());
    }


}

