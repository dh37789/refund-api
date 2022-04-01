package com.szs.szsrefund.domain.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserInfoDto {

    @Getter
    @NoArgsConstructor
    public static class Response {

        private String userId;
        private String name;
        private String regNo;

        @Builder
        public Response(String userId, String name, String regNo) {
            this.userId = userId;
            this.name = name;
            this.regNo = regNo;
        }

    }

}
