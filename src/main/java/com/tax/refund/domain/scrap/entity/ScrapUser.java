package com.tax.refund.domain.scrap.entity;

import com.tax.refund.domain.scrap.dto.IncomeDto;
import com.tax.refund.domain.scrap.dto.TaxDto;
import com.tax.refund.global.config.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "tb_scrap_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScrapUser extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String regNo;

    @OneToOne(cascade = CascadeType.ALL)
    private ScrapIncome scrapIncome;

    @OneToOne(cascade = CascadeType.ALL)
    private ScrapTax scrapTax;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ScrapResponse scrapResponse;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ScrapInfo scrapInfo;

    @Builder
    public ScrapUser(String name, String regNo, IncomeDto incomeDto, TaxDto taxDto, ScrapResponse scrapResponse, ScrapInfo scrapInfo) {
        this.name = name;
        this.regNo = regNo;
        this.scrapIncome = incomeDto.toEntity();
        this.scrapTax = taxDto.toEntity();
        this.scrapResponse = scrapResponse;
        this.scrapInfo = scrapInfo;
    }

}
