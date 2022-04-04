package com.szs.szsrefund.global.utill;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class StringUtils {

    /**
     * 주민등록번호 마스킹
     * @param regNo
     * @return
     */
    public static String maskingRegNo(String regNo) {
        return regNo.substring(0, 8) + "******";
    }

    /**
     * 금액 표기 포맷 변경
     * @param result
     * @return
     */
    public static String changeRefundWord(BigDecimal result) {
        return result.toString();
    }
}
