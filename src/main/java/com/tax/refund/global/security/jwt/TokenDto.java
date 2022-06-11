package com.tax.refund.global.security.jwt;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@ApiModel(value = "사용자 토큰")
public class TokenDto {

    @ApiModelProperty(name = "accessToken")
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
