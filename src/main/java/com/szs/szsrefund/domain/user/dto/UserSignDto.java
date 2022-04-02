package com.szs.szsrefund.domain.user.dto;

import com.szs.szsrefund.domain.user.entity.User;
import com.szs.szsrefund.global.utill.CrytptoUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

public class UserSignDto {

    @Getter
    @ApiModel(value = "사용자 회원가입 request")
    @NoArgsConstructor
    public static class Request {

        @ApiModelProperty(name = "userId", example = "abc1234")
        @NotBlank(message = "userId 값이 없습니다.")
        private String userId;

        @ApiModelProperty(name = "password", example = "1234")
        @NotBlank(message = "password 값이 없습니다.")
        private String password;

        @ApiModelProperty(name = "name", example = "홍길동")
        @NotBlank(message = "name 값이 없습니다.")
        private String name;

        @ApiModelProperty(name = "regNo", example = "860824-1655068")
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
    @ApiModel(value = "사용자 회원가입 response")
    public static class Response {

        @ApiModelProperty(name = "userId")
        private String userId;

        @ApiModelProperty(name = "name")
        private String name;

        @Builder
        public Response (String userId, String name) {
            this.userId = userId;
            this.name = name;
        }
    }


}
