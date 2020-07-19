package com.lp.common.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    public static LocalDateTime getLocalDateTime(String str, String format) {

        if (StringUtil.isEmpty(str) || StringUtil.isEmpty(format)) {

            return null;

        } else {

            DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
            return LocalDateTime.parse(str, df);
        }
    }

}
