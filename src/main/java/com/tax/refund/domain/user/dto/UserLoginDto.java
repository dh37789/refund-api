package com.tax.refund.domain.user.dto;

import com.tax.refund.domain.user.entity.User;
import com.tax.refund.global.security.jwt.TokenDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

public class UserLoginDto {

    @Getter
    @ApiModel(value = "사용자 로그인 request")
    @NoArgsConstructor
    public static class Request {

        @ApiModelProperty(name = "userId", example = "abc1234")
        @NotBlank(message = "userId 값이 없습니다.")
        private String userId;

        @ApiModelProperty(name = "password", example = "1234")
        @NotBlank(message = "password 값이 없습니다.")
        private String password;

        @Builder
        public Request(String userId, String password) {
            this.userId = userId;
            this.password = password;
        }

        public User toEntity() {
            return User.builder()
                    .userId(this.userId)
                    .password(this.password)
                    .build();
        }
    }

    @Getter
    @ApiModel(value = "사용자 로그인 response")
    @NoArgsConstructor
    public static class Response {

        @ApiModelProperty(name = "userId")
        private String userId;

        @ApiModelProperty(name = "token")
        private TokenDto token;

        @Builder
        public Response(String userId, TokenDto token) {
            this.userId = userId;
            this.token = token;
        }

    }

}
