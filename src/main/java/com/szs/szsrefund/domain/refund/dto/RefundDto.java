package com.szs.szsrefund.domain.refund.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RefundDto {

    @Getter
    @ApiModel(value = "환급금 response")
    @NoArgsConstructor
    public static class Response {

        @JsonProperty("이름")
        @ApiModelProperty(name = "이름")
        private String name;

        @JsonProperty("한도")
        @ApiModelProperty(name = "한도")
        private String limit;

        @JsonProperty("공제액")
        @ApiModelProperty(name = "공제액")
        private String deduction;

        @JsonProperty("환급액")
        @ApiModelProperty(name = "환급액")
        private String refund;

        @Builder
        public Response(String name, String limit, String deduction, String refund) {
            this.name = name;
            this.limit = limit;
            this.deduction = deduction;
            this.refund = refund;
        }
    }
}
