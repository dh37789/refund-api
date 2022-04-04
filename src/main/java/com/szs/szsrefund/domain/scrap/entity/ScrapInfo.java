package com.szs.szsrefund.domain.scrap.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "tb_scrap_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScrapInfo {

    /* pk */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* app version */
    private String appVer;

    /* 호스트 이름 */
    private String hostNm;

    /* 등록일자 */
    private LocalDateTime workerResDt;

    /* 요청일자 */
    private LocalDateTime workerReqDt;

    @Builder
    public ScrapInfo(String appVer, String hostNm, LocalDateTime workerResDt, LocalDateTime workerReqDt) {
        this.appVer = appVer;
        this.hostNm = hostNm;
        this.workerResDt = workerResDt;
        this.workerReqDt = workerReqDt;
    }
}
