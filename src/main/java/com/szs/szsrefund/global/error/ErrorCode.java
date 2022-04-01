package com.szs.szsrefund.global.error;

import lombok.Getter;

@Getter
public enum ErrorCode {

    ALREADY_EXISTS_USER("E001", "이미 가입한 회원입니다.", 400),
    ALREADY_EXISTS_USERID("E002", "사용중인 ID 입니다.", 400),
    NOT_MATCHED_REG_NO("E003", "옳지 않은 주민등록번호입니다.", 400),
    NOT_FOUND_USER("E004", "존재 하지 않는 회원입니다.", 404),
    NOT_MATCHED_PASSWORD("E005", "맞지 않는 비밀번호 입니다.", 400),
    NO_PARAMETER("E006", "맞지 않는 비밀번호 입니다.", 400);

    private final String code;
    private final String message;
    private final int status;

    ErrorCode(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

}
