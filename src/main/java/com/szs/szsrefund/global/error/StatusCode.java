package com.szs.szsrefund.global.error;

import lombok.Getter;

@Getter
public enum StatusCode {

    SUCCESS("E000","성공",200),

    ALREADY_EXISTS_USER("E001", "이미 가입한 회원입니다.", 400),
    ALREADY_EXISTS_USERID("E002", "사용중인 ID 입니다.", 400),
    NOT_MATCHED_REG_NO("E003", "옳지 않은 주민등록번호입니다.", 400),
    NOT_FOUND_USER("E004", "존재 하지 않는 회원입니다.", 404),
    NOT_MATCHED_PASSWORD("E005", "맞지 않는 비밀번호 입니다.", 400),
    NO_PARAMETER("E006", "맞지 않는 비밀번호 입니다.", 400),

    JWT_TOKEN_EXCEPTION("E007", "인증토큰 에러입니다.", 401),
    JWT_TOKEN_EXPIRED("E008", "만료된 토큰입니다.", 401),
    JWT_TOKEN_NULL("E009", "인증 토큰이 없습니다.", 401),
    INVALID_JWT_TOKEN("E010", "토큰이 옳바른 형식이 아닙니다.", 401);

    private final String code;
    private final String message;
    private final int status;

    StatusCode(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

}
