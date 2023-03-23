package com.tax.refund.global.config.common.service;

import com.tax.refund.global.config.common.Response;
import com.tax.refund.global.config.common.ResponseResult;
import com.tax.refund.global.error.StatusCode;

public class ResponseService {

    public static <T> ResponseResult<T> getResponseResult(T data){
        ResponseResult<T> response = new ResponseResult<>();
        setSuccessResponse(response);
        response.setData(data);

        return response;
    }

    public static ResponseResult getResponseResult(StatusCode statusCode){
        ResponseResult response = new ResponseResult();
        setSuccessResponse(response, statusCode);

        return response;
    }

    private static Response setSuccessResponse(Response response) {
        response.setSuccess(true);
        response.setCode(StatusCode.SUCCESS.getCode());
        response.setMsg(StatusCode.SUCCESS.getMessage());
        response.setStatus(StatusCode.SUCCESS.getStatus());

        return response;
    }

    private static Response setSuccessResponse(Response response, StatusCode statusCode) {
        response.setSuccess(true);
        response.setCode(statusCode.getCode());
        response.setMsg(statusCode.getMessage());
        response.setStatus(statusCode.getStatus());

        return response;
    }

    public static Response getFailureResult(StatusCode errorCode) {
        Response result = new Response();
        result.setSuccess(false);
        result.setCode(errorCode.getCode());
        result.setMsg(errorCode.getMessage());
        result.setStatus(errorCode.getStatus());

        return result;
    }


    public static Response getFailureResult(Response result) {
        return  result;
    }
}

