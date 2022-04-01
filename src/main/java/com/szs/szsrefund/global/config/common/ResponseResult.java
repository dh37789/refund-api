package com.szs.szsrefund.global.config.common;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ResponseResult<T> extends Response{
    private T data;
}
