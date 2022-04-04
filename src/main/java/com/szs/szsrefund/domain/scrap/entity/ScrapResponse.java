package com.szs.szsrefund.domain.scrap.entity;

import com.szs.szsrefund.global.config.common.BaseTimeEntity;
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

}
