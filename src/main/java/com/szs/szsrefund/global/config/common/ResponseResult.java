package com.szs.szsrefund.global.config.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class ResponseResult<T> extends Response{
    private T data;
}
