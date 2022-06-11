package com.tax.refund.domain.scrap.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class ScrapDto {

    @Getter
    @ApiModel(value = "Scrap 데이터 조회 request")
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Request {

        @ApiModelProperty(name = "name", example = "홍길동")
        @NotBlank(message = "name 값이 없습니다.")
        private String name;

        @ApiModelProperty(name = "regNo", example = "860824-1655068")
        @NotBlank(message = "regNo 값이 없습니다.")
        private String regNo;

        @Builder
        public Request(String name, String regNo){
            this.name = name;
            this.regNo = regNo;
        }
    }

    @Getter
    @ApiModel(value = "Scrap 데이터 조회 response")
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Response {

        @JsonProperty("jsonList")
        private ScrapJsonListDto scrapJsonList;

        @JsonProperty("appVer")
        private String appVer;

        @JsonProperty("hostNm")
        private String hostNm;

        @JsonProperty("workerResDt")
        private LocalDateTime workerResDt;

        @JsonProperty("workerReqDt")
        private LocalDateTime workerReqDt;

        @Builder
        public Response(ScrapJsonListDto scrapJsonList, String appVer, String hostNm, LocalDateTime workerResDt, LocalDateTime workerReqDt) {
            this.scrapJsonList = scrapJsonList;
            this.appVer = appVer;
            this.hostNm = hostNm;
            this.workerResDt = workerResDt;
            this.workerReqDt = workerReqDt;
        }

    }
}
