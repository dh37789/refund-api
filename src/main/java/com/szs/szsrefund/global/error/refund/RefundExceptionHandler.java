package com.szs.szsrefund.global.error.refund;

import com.szs.szsrefund.domain.refund.exception.NotCompleteScrapException;
import com.szs.szsrefund.global.config.common.Response;
import com.szs.szsrefund.global.config.common.service.ResponseService;
import com.szs.szsrefund.global.error.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RefundExceptionHandler {

    private final ResponseService responseService;

    public RefundExceptionHandler(ResponseService responseService) {
        this.responseService = responseService;
    }

    @ExceptionHandler(NotCompleteScrapException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response notCompleteScrapExcpeption() {
        final StatusCode errorCode = StatusCode.NOT_COMPLETE_SCRAP;
        return responseService.getFailureResult(errorCode);
    }
}
