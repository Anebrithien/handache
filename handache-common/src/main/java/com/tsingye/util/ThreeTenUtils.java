package com.tsingye.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 老规矩的时间相关工具类
 * Created by tsingye on 16-6-1.
 */
public class ThreeTenUtils {

    private static final Map<String, DateTimeFormatter> DATE_TIME_FORMATTERS = new HashMap<>();

    private ThreeTenUtils() {
        // empty construct
    }

    static {
        DATE_TIME_FORMATTERS.put("u-M", DateTimeFormatter.ofPattern("u-M"));
        DATE_TIME_FORMATTERS.put("u-M-d", DateTimeFormatter.ofPattern("u-M-d"));
        DATE_TIME_FORMATTERS.put("H:m", DateTimeFormatter.ofPattern("H:m"));
        DATE_TIME_FORMATTERS.put("u-M-d H:m", DateTimeFormatter.ofPattern("u-M-d H:m"));

        DATE_TIME_FORMATTERS.put("uuuu-MM", DateTimeFormatter.ofPattern("uuuu-MM"));
        DATE_TIME_FORMATTERS.put("uuuu-MM-dd", DateTimeFormatter.ofPattern("uuuu-MM-dd"));
        DATE_TIME_FORMATTERS.put("HH:mm", DateTimeFormatter.ofPattern("HH:mm"));
        DATE_TIME_FORMATTERS.put("uuuu-MM-dd HH:mm", DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm"));
    }

    /**
     * 返回系统所在{@link ZoneId}
     *
     * @return
     */
    public static ZoneId defaultZoneId() {
        return ZoneId.systemDefault();
    }

    public static DateTimeFormatter ofPattern(String pattern) {
        return DATE_TIME_FORMATTERS.computeIfAbsent(pattern, k -> DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 将{@link LocalDate} 转换成 {@link Instant}
     *
     * @param localDate 日期，不可为null
     * @return
     */
    public static Instant toInstant(LocalDate localDate) {
        if (localDate == null) {
            throw new IllegalArgumentException("localDate should not be null!");
        }
        return toInstant(localDate.atStartOfDay());
    }

    /**
     * 将{@link LocalDateTime} 转换成 {@link Instant}
     *
     * @param localDateTime 日期时间，，不可为null
     * @return
     */
    public static Instant toInstant(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            throw new IllegalArgumentException("localDate should not be null!");
        }
        return localDateTime.atZone(defaultZoneId()).toInstant();
    }

    /**
     * 将{@link LocalDate} 转换为老式的{@link Date}，使用系统默认时区
     *
     * @param localDate 欲转换的日期
     * @return
     */
    public static Date toDate(LocalDate localDate) {
        return Date.from(toInstant(localDate));
    }

    /**
     * 将{@link LocalDateTime} 转换为老式的{@link Date}，使用系统默认时区
     *
     * @param localDateTime 欲转换的时间
     * @return
     */
    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(toInstant(localDateTime));
    }

    /**
     * Gets the number of seconds from the Java epoch of 1970-01-01T00:00:00Z
     *
     * @param localDate 日期，以该日期的00:00:00.000为准，不可为null
     * @return
     */
    public static long toEpochSecond(LocalDate localDate) {
        return toInstant(localDate).getEpochSecond();
    }

    /**
     * Gets the number of milliseconds from the Java epoch of 1970-01-01T00:00:00Z
     *
     * @param localDate 日期，以该日期的00:00:00.000为准，不可为null
     * @return
     */
    public static long toEpochMilli(LocalDate localDate) {
        return toInstant(localDate).toEpochMilli();
    }

    /**
     * Gets the number of seconds from the Java epoch of 1970-01-01T00:00:00Z
     *
     * @param localDateTime 日期时间，不可为null
     * @return
     */
    public static long toEpochSecond(LocalDateTime localDateTime) {
        return toInstant(localDateTime).getEpochSecond();
    }

    /**
     * Gets the number of milliseconds from the Java epoch of 1970-01-01T00:00:00Z
     *
     * @param localDateTime 日期时间，不可为null
     * @return
     */
    public static long toEpochMilli(LocalDateTime localDateTime) {
        return toInstant(localDateTime).toEpochMilli();
    }

    //============= format and etc =============//

    public static String format(LocalDate localDate, String pattern) {
        return localDate.format(ofPattern(pattern));
    }

    public static String format(LocalDateTime localDateTime, String pattern) {
        return localDateTime.format(ofPattern(pattern));
    }

    public static String format(long epochMilli, String pattern) {
        return format(localDateTimeOf(epochMilli), pattern);
    }

    public static LocalDateTime localDateTimeOf(Instant instant) {
        return LocalDateTime.ofInstant(instant, defaultZoneId());
    }

    public static LocalDateTime localDateTimeOf(long epochMilli) {
        return localDateTimeOf(Instant.ofEpochMilli(epochMilli));
    }

    public static LocalDate parseDate(String date, String pattern) {
        return LocalDate.parse(date, ofPattern(pattern));
    }

    public static LocalDateTime parseDateTime(String dateTime, String pattern) {
        return LocalDateTime.parse(dateTime, ofPattern(pattern));
    }

    /**
     * 简单过滤一下查询的时间参数，如果没有起止时间，则使用默认的“24小时前~当前”为查询起止时间
     *
     * @param startPoint 起点时间 传入null 则为1 天前
     * @param endPoint   截止时间 传入null 则为当前时间
     * @return
     */
    public static long[] determineTimePoints(Long startPoint, Long endPoint) {
        return determineTimePoints(startPoint, endPoint, 1L);
    }

    /**
     * 简单过滤一下查询的时间参数，如果没有起止时间，则使用默认的“24小时前~当前”为查询起止时间
     *
     * @param startPoint        起点时间 传入null 则为defaultOffsetDays 天前
     * @param endPoint          截止时间 传入null 则为当前时间
     * @param defaultOffsetDays 若 startPoint 为null，则此参数必须提供
     * @return
     */
    public static long[] determineTimePoints(Long startPoint, Long endPoint, long defaultOffsetDays) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        long[] result = new long[2];
        result[0] = startPoint != null ? startPoint :
                toEpochMilli(currentDateTime.minusDays(defaultOffsetDays));
        result[1] = endPoint != null ? endPoint :
                toEpochMilli(currentDateTime);
        return result;
    }

}
