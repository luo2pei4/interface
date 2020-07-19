package com.lp.common.utils;

import com.lp.common.constant.Constants;

import java.util.UUID;

public class StringUtil {

    public static boolean isEmpty(String str) {

        return str == null || Constants.STRING_BLANK.equals(str.trim());
    }

    public static String getUuid() {

        return UUID.randomUUID().toString();
    }
}
