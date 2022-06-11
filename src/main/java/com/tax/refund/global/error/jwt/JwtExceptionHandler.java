package com.tax.refund.global.error.jwt;

import com.tax.refund.global.config.common.Response;
import com.tax.refund.global.config.common.service.ResponseService;
import com.tax.refund.global.error.StatusCode;
import com.tax.refund.global.security.jwt.exception.InvalidJwtTokenException;
import com.tax.refund.global.security.jwt.exception.JwtTokenException;
import com.tax.refund.global.security.jwt.exception.JwtTokenExpiredException;
import com.tax.refund.global.security.jwt.exception.NullJwtTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class JwtExceptionHandler {

    private final ResponseService responseService;

    public JwtExceptionHandler(ResponseService responseService) {
        this.responseService = responseService;
    }

    @ExceptionHandler(JwtTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Response jwtTokenException() {
        final StatusCode errorCode = StatusCode.JWT_TOKEN_EXCEPTION;
        return responseService.getFailureResult(errorCode);
    }

    @ExceptionHandler(JwtTokenExpiredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Response jwtTokenExpiredException() {
        final StatusCode errorCode = StatusCode.JWT_TOKEN_EXPIRED;
        return responseService.getFailureResult(errorCode);
    }

    @ExceptionHandler(NullJwtTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Response nullJwtTokenException() {
        final StatusCode errorCode = StatusCode.JWT_TOKEN_NULL;
        return responseService.getFailureResult(errorCode);
    }

    @ExceptionHandler(InvalidJwtTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Response invalidJwtTokenException() {
        final StatusCode errorCode = StatusCode.INVALID_JWT_TOKEN;
        return responseService.getFailureResult(errorCode);
    }
}
