package com.szs.szsrefund.global.security.jwt;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TokenDto {

    private String accessToken;

    @Builder
    public TokenDto(String accessToken) {
        this.accessToken = accessToken;
    }

    public static TokenDto of (String accessToken) {
        return TokenDto.builder()
                .accessToken(accessToken)
                .build();
    }
}
