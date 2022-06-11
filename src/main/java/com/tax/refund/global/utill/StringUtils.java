package com.tax.refund.global.utill;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class StringUtils {

    private StringUtils() {}

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
     * @param money
     * @return
     */
    public static String changeRefundWord(BigDecimal money) {
        DecimalFormat comma = new DecimalFormat("#,####");
        String[] moneyType = new String[]{"천", "만","억","조"};
        String[] moneyArr = comma.format(money).split(",");
        String result = "";

        int cnt = 0;
        for(int i=moneyArr.length;i>0;i--) {
            if (i == moneyArr.length) {
                if (moneyArr[moneyArr.length-1].charAt(0) != '0') {
                    result = " " + moneyArr[moneyArr.length-1].charAt(0) + moneyType[cnt];
                }
            } else if (Integer.parseInt(moneyArr[i-1]) != 0) {
                result = Integer.parseInt(moneyArr[i-1]) + moneyType[cnt] + result;
            }
            cnt++;
        }

        return result + "원";
    }
}