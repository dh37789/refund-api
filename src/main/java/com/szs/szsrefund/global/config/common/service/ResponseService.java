package com.szs.szsrefund.global.config.common.service;

import com.szs.szsrefund.global.config.common.Response;
import com.szs.szsrefund.global.config.common.ResponseResult;
import com.szs.szsrefund.global.error.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.stream.Collectors;

@Service
public class ResponseService {

    public <T> ResponseResult<T> getResponseResult(T data){
        ResponseResult<T> response = new ResponseResult<>();
        setSuccessResponse(response);
        response.setData(data);

        return response;
    }

    private Response setSuccessResponse() {
        Response result = new Response();
        setSuccessResponse(result);

        return result;
    }

    private Response setSuccessResponse(Response response) {
        response.setSuccess(true);
        response.setCode("00");
        response.setMsg("성공");

        return response;
    }

    public Response getFailureResult(ErrorCode errorCode) {
        Response result = new Response();
        result.setSuccess(false);
        result.setCode(errorCode.getCode());
        result.setMsg(errorCode.getMessage());
        result.setStatus(errorCode.getStatus());

        return result;
    }

    public Response getInValidateResult(ErrorCode errorCode, MethodArgumentNotValidException e) {
        Response result = new Response();
        result.setSuccess(false);
        result.setCode(errorCode.getCode());
        result.setMsg(e.getBindingResult().getFieldErrors().stream().map(x -> x.getDefaultMessage()).collect(Collectors.joining()));
        result.setStatus(errorCode.getStatus());

        return result;
    }
}

