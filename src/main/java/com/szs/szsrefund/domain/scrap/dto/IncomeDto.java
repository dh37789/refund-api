package com.szs.szsrefund.domain.scrap.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szs.szsrefund.domain.scrap.entity.ScrapIncome;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IncomeDto {

    @JsonProperty("소득내역")
    private String incomeDetail;

    @JsonProperty("총지급액")
    private BigDecimal totalPayment;

    @JsonProperty("업무시작일")
    private String workStartDate;

    @JsonProperty("기업명")
    private String companyName;

    @JsonProperty("이름")
    private String name;

    @JsonProperty("주민등록번호")
    private String regNo;

    @JsonProperty("지급일")
    private String paymentDate;

    @JsonProperty("업무종료일")
    private String workEndDate;

    @JsonProperty("소득구분")
    private String incomeDivision;

    @JsonProperty("사업자등록번호")
    private String businessNumber;

    @Builder
    public IncomeDto(String incomeDetail, BigDecimal totalPayment, String workStartDate, String companyName
            , String name, String regNo, String paymentDate, String workEndDate, String incomeDivision, String businessNumber) {
        this.incomeDetail = incomeDetail;
        this.totalPayment = totalPayment;
        this.workStartDate = workStartDate;
        this.companyName = companyName;
        this.name = name;
        this.regNo = regNo;
        this.paymentDate = paymentDate;
        this.workEndDate = workEndDate;
        this.incomeDivision = incomeDivision;
        this.businessNumber = businessNumber;
    }

    public ScrapIncome toEntity() {
        return ScrapIncome.builder()
                .incomeDetail(incomeDetail)
                .totalPayment(totalPayment)
                .workStartDate(workStartDate)
                .companyName(companyName)
                .paymentDate(paymentDate)
                .workEndDate(workEndDate)
                .incomeDivision(incomeDivision)
                .businessNumber(businessNumber)
                .build();
    }
}
