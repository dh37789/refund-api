package com.szs.szsrefund.global.utill;

public class StringUtils {

    public static String maskingRegNo(String regNo) {
        return regNo.substring(0, 8) + "******";
    }
}
