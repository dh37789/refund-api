package com.tax.refund.domain.scrap.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScrapJsonListDto {

    @JsonProperty("scrap001")
    private List<IncomeDto> scrap001;

    @JsonProperty("scrap002")
    private List<TaxDto> scrap002;

    @JsonProperty("errMsg")
    private String errMsg;

    @JsonProperty("company")
    private String company;

    @JsonProperty("svcCd")
    private String svcCd;

    @JsonProperty("userId")
    private String userId;

    @Builder
    public ScrapJsonListDto(List<IncomeDto> scrap001, List<TaxDto> scrap002,
                            String errMsg, String company, String svcCd, String userId) {
        this.scrap001 = scrap001;
        this.scrap002 = scrap002;
        this.errMsg = errMsg;
        this.company = company;
        this.svcCd = svcCd;
        this.userId = userId;
    }

}
