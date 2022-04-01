package com.szs.szsrefund.domain.user.dto;

import com.szs.szsrefund.domain.user.entity.User;
import com.szs.szsrefund.global.utill.CrytptoUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

public class UserSignDto {

    @Getter
    @NoArgsConstructor
    public static class Request {

        @NotBlank(message = "userId 값이 없습니다.")
        private String userId;

        @NotBlank(message = "password 값이 없습니다.")
        private String password;

        @NotBlank(message = "name 값이 없습니다.")
        private String name;

        @NotBlank(message = "regNo 값이 없습니다.")
        private String regNo;

        @Builder
        public Request(String userId, String password, String name, String regNo){
            this.userId = userId;
            this.password = password;
            this.name = name;
            this.regNo = regNo;
        }

        public User toEntity() {
            return User.builder()
                    .name(this.name)
                    .userId(this.userId)
                    .password(this.password)
                    .regNo(this.regNo)
                    .build();
        }

        public void encryptRegNo(String regNo) throws Exception {
            this.regNo = CrytptoUtils.encrypt(regNo);
        }

        public void encodePassword(String password) {
            this.password = password;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Response {

        private String userId;
        private String name;

        @Builder
        public Response (String userId, String name) {
            this.userId = userId;
            this.name = name;
        }
    }


}
