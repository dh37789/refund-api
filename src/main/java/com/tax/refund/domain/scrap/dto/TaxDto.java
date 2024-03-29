package com.tax.refund.domain.scrap.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tax.refund.domain.scrap.entity.ScrapTax;
import com.tax.refund.domain.scrap.exception.NotFoundTaxException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TaxDto {

    @JsonProperty("소득구분")
    private String incomeDivision;

    @JsonProperty("총사용금액")
    private BigDecimal totalUseAmount;

    @Builder
    public TaxDto(String incomeDivision, BigDecimal totalUseAmount) {
        this.incomeDivision = incomeDivision;
        this.totalUseAmount = totalUseAmount;
    }

    public static TaxDto findTaxData(ScrapJsonListDto jsonList) {
        return jsonList
                .getScrap002()
                .stream()
                .findFirst()
                .orElseThrow(NotFoundTaxException::new);
    }

    public ScrapTax toEntity() {
        return ScrapTax.builder()
                .incomeDivision(incomeDivision)
                .totalUseAmount(totalUseAmount)
                .build();
    }
}
