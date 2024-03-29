package com.tax.refund.domain.scrap.entity;

import com.tax.refund.domain.scrap.dto.ScrapJsonListDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "tb_scrap_response")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScrapResponse {

    /* pk */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* 에러메시지 */
    private String errMsg;

    /* 회사명 */
    private String company;

    /* 서비스 구분 */
    private String svcCd;

    /* 유저 아이디 */
    private String userId;

    @Builder
    public ScrapResponse (String errMsg, String company, String svcCd, String userId) {
        this.errMsg = errMsg;
        this.company = company;
        this.svcCd = svcCd;
        this.userId = userId;
    }

    public static ScrapResponse findResponseData(ScrapJsonListDto jsonList) {
        return ScrapResponse.builder()
                .errMsg(jsonList.getErrMsg())
                .company(jsonList.getCompany())
                .svcCd(jsonList.getSvcCd())
                .userId(jsonList.getUserId())
                .build();
    }
}
