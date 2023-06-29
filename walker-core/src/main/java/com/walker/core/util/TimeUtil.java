package com.walker.core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
    public final static String ymdhms1 = "yyyyMMddHHmmss";
    public final static String hms = "HH:mm:ss";
    public final static String ymd = "yyyy-MM-dd";
    public final static String ymd1 = "yyyyMMdd";
    public final static String ymdh = "yyyy-MM-dd HH";
    public final static String ymdhm = "yyyy-MM-dd HH:mm";
    public final static String ymdhms = "yyyy-MM-dd HH:mm:ss";
    public final static String ymdhmsS = "yyyy-MM-dd HH:mm:ss:SSS";
    public final static String sss = "SSS";
    public final static String seq = "yyyyMMddHHmmss";
    public final static String seq1 = "yyyyMMddHHmmssSSS";
    public final static long daymills = 24 * 3600 * 1000L;

    /**
     * 获取指定格式的时间yyyy-MM-dd HH:mm:ss:SSS
     */
    public static String getTime(Date d, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(d);
    }

    /**
     * 获取指定格式的时间yyyy-MM-dd HH:mm:ss:SSS
     */
    public static String getTime(long time, String format) {
        return getTime(new Date(time), format);
    }

    /**
     * 获取指定格式的时间yyyy-MM-dd HH:mm:ss:SSS
     */
    public static String getTime(String format) {
        return getTime(new Date(), format);
    }

    /**
     * 解获取时间yyyyMMddHHmmss
     */
    public static String getTimeSequence() {
        return getTime(new Date(), seq);
    }

    /**
     * 获取当前时间 yyyy-MM-dd
     */
    public static String getTimeYmd() {
        return getTime(new Date(), ymd);
    }

    /**
     * 获取当前时间 yyyy-MM-dd
     */
    public static String getTimeYmd(long time) {
        return getTime(time, ymd);
    }

    /**
     * 获取当前时间 HH:mm:ss
     */
    public static String getTimeHms() {
        return getTime(new Date(), "HH:mm:ss");
    }

    /**
     * 获取当前时间 HH:mm:ss
     */
    public static String getTimeHms(long time) {
        return getTime(time, "HH:mm:ss");
    }

    /**
     * 获取当前时间 yyyy-MM-dd HH:mm:ss
     */
    public static String getTimeYmdHms() {
        return getTime(new Date(), ymdhms);
    }

    /**
     * 获取当前时间 yyyy-MM-dd HH:mm:ss
     */
    public static String getTimeYmdHms(long time) {
        return getTime(time, ymdhms);
    }

    /**
     * 获取当前时间 yyyy-MM-dd HH:mm:ss:sss
     */
    public static String getTimeYmdHmss() {
        return getTime(new Date(), ymdhmsS);
    }

    /**
     * 获取当前时间 yyyy-MM-dd HH:mm:ss:sss
     */
    public static String getTimeYmdHmss(long time) {
        return getTime(time, ymdhmsS);
    }


    /**
     * 获取差值几天的 指定格式的时间yyyy-MM-dd HH:mm:ss:SSS
     */
    public static String getTime(String format, int detaDays) {
        return getTime(format, detaDays * daymills);
    }

    /**
     * 获取差值几天的 指定格式的时间yyyy-MM-dd HH:mm:ss:SSS
     */
    public static String getTime(String fromStr, String format, int detaDays) {
        return getTime(fromStr, format, detaDays * daymills);
    }

    /**
     * 获取差值s 指定格式的时间yyyy-MM-dd HH:mm:ss:SSS
     */
    public static String getTime(String format, long detaMills) {
        long t = System.currentTimeMillis() + detaMills;
        return getTime(t, format);
    }

    /**
     * 获取差值s 指定格式的时间yyyy-MM-dd HH:mm:ss:SSS
     */
    public static String getTime(String fromStr, String format, long detaMills) {
        long t = format(fromStr, format) + detaMills;
        return getTime(t, format);
    }

    /**
     * 2018-91-12, yyyy-MM-dd HH:mm:ss:SSS
     *
     * @param time
     * @param format
     */
    public static long format(String time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date res = new Date();
        try {
            res = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res.getTime();
    }

    /**
     * 根据时间差 决定格式化到 分钟 还是 小时 天
     * 2342342342 yyyy-MM-dd HH:mm:ss:SSS
     *
     * @param time 11231213123
     * @param deta 60 * 1000
     */
    public static String formatAuto(long time, int deta) {
        deta = Math.abs(deta);
        String format = "";
        if (deta <= 0) {
            format = "yyyy-MM-dd HH:mm:ss";
        } else if (deta < 1000) {
            format = "SSS";    //:123
        } else if (deta < 1000 * 60) {
            format = "ss:SSS";
        } else if (deta < 1000 * 60 * 60) {
            format = "mm:ss";
        } else if (deta < 1000 * 60 * 60 * 24) {
            format = "HH:mm";
        } else if (deta < 1000 * 60 * 60 * 24 * 30) {
            format = "dd HH";
        } else if (deta < 1000 * 60 * 60 * 24 * 30 * 365) {
            format = "MM-dd";
        } else {
            format = "yyyy-MM";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String res = sdf.format(new Date(time));
        return res;
    }

    /**
     * 取得当月天数
     */
    public static int getDays() {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.DATE, 1);// 把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);// 日期回滚一天，也就是最后一天
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 得到指定月的天数201901
     */
    public static int getDays(String yyyymmdd) {
        int year = Integer.parseInt(yyyymmdd.substring(0, 4));
        int month = Integer.parseInt(yyyymmdd.substring(5, 7));

        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);// 把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);// 日期回滚一天，也就是最后一天
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 获取时间戳
     */
    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }

}
