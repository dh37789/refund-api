package com.tax.refund.global.config.common;

import java.math.BigDecimal;

public class Constants {
    public static final String CONTENT_TYPE = "application/json;charset=UTF-8";
    public static final String CONTROLLER_PATH = "com.szs.szsrefund.domain";

    public static final String USERINFO_HEAD_KEY = "userInfo:";
    public static final String TOKEN_HEAD_KEY = "token:";
    public static final String REFUND_HEAD_KEY = "refundInfo:";
    public static final String NAME_KEY = ":name";
    public static final String LIMIT_KEY = ":limit";
    public static final String DEDUCTION_KEY = ":deduction";
    public static final String REFUND_KEY = ":refund";
    public static final String REG_NO_KEY = ":regNo";

    public static final String REG_NO_REGEX = "\\d{6}\\-[1-4]\\d{6}";

    public static final String[] IGNORING_PATH = {
            "/swagger-resources",
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v2/api-docs",
            "/favicon.ico",
            "/error",
            "/szs/signup",
            "/szs/login"
    };

    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final BigDecimal TOTAL_PAYMENT_MIN = new BigDecimal(33000000);
    public static final BigDecimal TOTAL_PAYMENT_MAX = new BigDecimal(70000000);

    public static final BigDecimal LIMIT_MAX = new BigDecimal(740000);
    public static final BigDecimal LIMIT_MID = new BigDecimal(660000);
    public static final BigDecimal LIMIT_MIN = new BigDecimal(500000);

    public static final BigDecimal LIMIT_MID_RATE = new BigDecimal(0.008);
    public static final BigDecimal LIMIT_MAX_RATE = new BigDecimal(0.5);

    public static final BigDecimal TAX_STANDARD = new BigDecimal(1300000);
    public static final BigDecimal TAX_MIN_LATE = new BigDecimal(0.55);
    public static final BigDecimal TAX_MAX = new BigDecimal(715000);
    public static final BigDecimal TAX_MAX_LATE = new BigDecimal(0.3);
}
