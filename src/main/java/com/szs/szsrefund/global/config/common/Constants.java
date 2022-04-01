package com.szs.szsrefund.global.config.common;

public class Constants {
    public static final String CONTENT_TYPE = "application/json;charset=UTF-8";
    public static final String CONTROLLER_PATH = "com.szs.szsrefund.domain.user.api";

    public static final String NAME_KEY = "name";
    public static final String REG_NO_REGEX = "\\d{6}\\-[1-4]\\d{6}";

    public static final String[] IGNORING_PATH = {
            "/swagger-resources",
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/szs/signup",
            "/szs/login"
    };

    public static final String AUTHORIZATION_HEADER = "Authorization";

}
