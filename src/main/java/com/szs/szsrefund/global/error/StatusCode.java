package com.szs.szsrefund.global.error;

import lombok.Getter;

@Getter
public enum StatusCode {

    SUCCESS("E000","성공",200),
    CALL_API("E001","API 요청 완료",200),
    API_LODING("E002","1분뒤 다시 시도해주세요.",200),


    ALREADY_EXISTS_USER("E101", "이미 가입한 회원입니다.", 400),
    ALREADY_EXISTS_USERID("E102", "사용중인 ID 입니다.", 400),
    NOT_MATCHED_REG_NO("E103", "옳지 않은 주민등록번호입니다.", 400),
    NOT_FOUND_USER("E104", "존재 하지 않는 회원입니다.", 404),
    NOT_MATCHED_PASSWORD("E105", "맞지 않는 비밀번호 입니다.", 400),
    NO_PARAMETER("E106", "맞지 않는 비밀번호 입니다.", 400),

    JWT_TOKEN_EXCEPTION("E201", "인증토큰 에러입니다.", 401),
    JWT_TOKEN_EXPIRED("E202", "만료된 토큰입니다.", 401),
    JWT_TOKEN_NULL("E203", "인증 토큰이 없습니다.", 401),
    INVALID_JWT_TOKEN("E204", "토큰이 옳바른 형식이 아닙니다.", 401),

    NOT_FOUND_INCOME("E301","급여내역 정보를 찾을 수 없습니다.",404),
    NOT_FOUND_TAX("E302","산출세액 정보를 찾을 수 없습니다.",404),
    NOT_FOUND_SCRAP_INFO("E303","유저의 급여정보를 찾을 수 없습니다.",404),
    SCRAP_USER_DATA_NULL("E304","유저 정보가 없습니다.",400),

    NOT_COMPLETE_SCRAP("E401","유저정보를 수집중 입니다.",400),

    INVALID_HTTP_METHOD("E501", "올바르지 않은 HTTP 호출 방식 입니다.", 404),
    NOT_FOUND_API_PATH("E502", "올바르지 않은 API 입니다.", 404);

    private final String code;
    private final String message;
    private final int status;

    StatusCode(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

}
