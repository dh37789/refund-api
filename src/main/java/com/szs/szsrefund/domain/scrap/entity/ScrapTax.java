package com.szs.szsrefund.domain.scrap.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "tb_scrap_tax")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScrapTax {

    /* pk */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* 소득구분 */
    private String incomeDivision;

    /* 총사용금액 */
    private BigDecimal totalUseAmount;

    @Builder
    public ScrapTax(String incomeDivision, BigDecimal totalUseAmount) {
        this.incomeDivision = incomeDivision;
        this.totalUseAmount = totalUseAmount;
    }
}
