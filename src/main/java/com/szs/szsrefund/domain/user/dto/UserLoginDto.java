package com.szs.szsrefund.domain.user.dto;

import com.szs.szsrefund.domain.user.entity.User;
import com.szs.szsrefund.global.security.jwt.TokenDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

public class UserLoginDto {

    @Getter
    @NoArgsConstructor
    public static class Request {

        @NotBlank(message = "userId 값이 없습니다.")
        private String userId;

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

        public void encodePassword(String password) {
            this.password = password;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Response {

        private String userId;
        private TokenDto token;

        @Builder
        public Response(String userId, TokenDto token) {
            this.userId = userId;
            this.token = token;
        }

    }

}
