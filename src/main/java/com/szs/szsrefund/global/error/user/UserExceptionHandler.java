package com.szs.szsrefund.global.error.user;

import com.szs.szsrefund.domain.user.exception.*;
import com.szs.szsrefund.global.config.common.Response;
import com.szs.szsrefund.global.config.common.service.ResponseService;
import com.szs.szsrefund.global.error.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionHandler {

    private final ResponseService responseService;

    public UserExceptionHandler(ResponseService responseService) {
        this.responseService = responseService;
    }

    @ExceptionHandler(AlreadyExistsUserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response alreadyExistsUserException() {
        final StatusCode errorCode = StatusCode.ALREADY_EXISTS_USER;
        return responseService.getFailureResult(errorCode);
    }

    @ExceptionHandler(AlreadyExistsUserIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response alreadyExistsUserIdException() {
        final StatusCode errorCode = StatusCode.ALREADY_EXISTS_USERID;
        return responseService.getFailureResult(errorCode);
    }

    @ExceptionHandler(NotMatchedRegNoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response notMatchedRegNoException() {
        final StatusCode errorCode = StatusCode.NOT_MATCHED_REG_NO;
        return responseService.getFailureResult(errorCode);
    }

    @ExceptionHandler(NotFoundUserException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response notFoundUserException() {
        final StatusCode errorCode = StatusCode.NOT_FOUND_USER;
        return responseService.getFailureResult(errorCode);
    }

    @ExceptionHandler(NotMatchedPasswordException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response notMatchedPasswordException() {
        final StatusCode errorCode = StatusCode.NOT_MATCHED_PASSWORD;
        return responseService.getFailureResult(errorCode);
    }
}
