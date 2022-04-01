package com.szs.szsrefund.global.error;

import com.szs.szsrefund.global.config.common.Response;
import com.szs.szsrefund.global.config.common.service.ResponseService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ResponseService responseService;

    public GlobalExceptionHandler(ResponseService responseService) {
        this.responseService = responseService;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response alreadyExistsUserException(MethodArgumentNotValidException exception) {
        final ErrorCode errorCode = ErrorCode.NO_PARAMETER;
        return responseService.getInValidateResult(errorCode, exception);
    }
}
