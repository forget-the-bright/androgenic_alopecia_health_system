package com.hairloss.system.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间工具类（统一使用北京时间 GMT+8）
 */
public class TimeUtil {

    /**
     * 北京时间时区
     */
    private static final ZoneId BEIJING_ZONE = ZoneId.of("Asia/Shanghai");

    /**
     * 日期时间格式化（北京时间）
     */
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取当前北京时间
     * @return 北京时间 LocalDateTime
     */
    public static LocalDateTime now() {
        return ZonedDateTime.now(BEIJING_ZONE).toLocalDateTime();
    }

    /**
     * 获取当前北京时间（格式化字符串）
     * @return 格式化后的时间字符串
     */
    public static String nowString() {
        return now().format(FORMATTER);
    }

    /**
     * 获取当前北京时间（日期部分）
     * @return 北京时间 LocalDate
     */
    public static java.time.LocalDate today() {
        return now().toLocalDate();
    }

    /**
     * 格式化时间为字符串
     * @param localDateTime 时间
     * @return 格式化后的时间字符串
     */
    public static String format(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return "";
        }
        return localDateTime.format(FORMATTER);
    }
}
