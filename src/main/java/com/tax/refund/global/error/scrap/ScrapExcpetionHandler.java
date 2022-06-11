package com.tax.refund.global.error.scrap;

import com.tax.refund.domain.scrap.exception.NotFoundIncomeException;
import com.tax.refund.domain.scrap.exception.NotFoundScrapInfoException;
import com.tax.refund.domain.scrap.exception.NotFoundTaxException;
import com.tax.refund.domain.scrap.exception.ScrapUserDataNullException;
import com.tax.refund.global.config.common.Response;
import com.tax.refund.global.config.common.service.ResponseService;
import com.tax.refund.global.error.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ScrapExcpetionHandler {

    private final ResponseService responseService;

    public ScrapExcpetionHandler(ResponseService responseService) {
        this.responseService = responseService;
    }

    @ExceptionHandler(NotFoundIncomeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response notFoundIncomeException() {
        final StatusCode errorCode = StatusCode.NOT_FOUND_INCOME;
        return responseService.getFailureResult(errorCode);
    }

    @ExceptionHandler(NotFoundScrapInfoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response notFoundScrapInfoException() {
        final StatusCode errorCode = StatusCode.NOT_FOUND_SCRAP_INFO;
        return responseService.getFailureResult(errorCode);
    }

    @ExceptionHandler(NotFoundTaxException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response notFoundTaxException() {
        final StatusCode errorCode = StatusCode.NOT_FOUND_TAX;
        return responseService.getFailureResult(errorCode);
    }

    @ExceptionHandler(ScrapUserDataNullException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response scrapUserDataNullException() {
        final StatusCode errorCode = StatusCode.SCRAP_USER_DATA_NULL;
        return responseService.getFailureResult(errorCode);
    }

}
