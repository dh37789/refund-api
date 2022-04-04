package com.szs.szsrefund.domain.scrap.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "tb_scrap_income")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScrapIncome{

    /* pk */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* 소득내역 */
    private String incomeDetail;

    /* 총지급액 */
    private BigDecimal totalPayment;

    /* 업무 시작일 */
    private String workStartDate;

    /* 기업명 */
    private String companyName;

    /* 지급일 */
    private String paymentDate;

    /* 업무종료일 */
    private String workEndDate;

    /* 소득구분 */
    private String incomeDivision;

    /* 사업자등록번호 */
    private String businessNumber;

    @Builder
    public ScrapIncome(String incomeDetail, BigDecimal totalPayment, String workStartDate, String companyName
            , String paymentDate, String workEndDate, String incomeDivision, String businessNumber) {
        this.incomeDetail = incomeDetail;
        this.totalPayment = totalPayment;
        this.workStartDate = workStartDate;
        this.companyName = companyName;
        this.paymentDate = paymentDate;
        this.workEndDate = workEndDate;
        this.incomeDivision = incomeDivision;
        this.businessNumber = businessNumber;
    }

}
