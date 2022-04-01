package com.szs.szsrefund.global.config.common;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class Response implements Serializable {
    private boolean success;
    private String code;
    private String msg;
    private int status;
}
