package com.ourslook.zuoyeba.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by 关 on 2016/4/21.
 */
public class DateUtils {
    /**
     * 将字符串日期转换成短格式的字符串
     *
     * @param str
     * @return
     */
    public static String StrDateToSortStrDate(String str) {
        Date date = StrToDate(str);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);

    }

    /**
     * 将字符串装换成年月
     *
     * @param str
     * @return
     */
    public static String StrDateToYearAndMonthDate(String str) {
        if ("不限".equals(str)|| "请选择日期".equals(str)) {
            return "";
        } else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月");
            Date date = null;
            try {
                if (str != null) {
                    date = format.parse(str);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            format = new SimpleDateFormat("yyyy-MM");
            return format.format(date);
        }

    }

    /**
     * 字符串转日期
     *
     * @param str
     * @return
     */

    public static Date StrToDate(String str) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 日期转换成字符串
     *
     * @param date
     * @return str
     */
    public static String DateToStr(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = format.format(date);
        return str;
    }

    /**
     * 获取当前日期字符串
     */
    public static String getCurrentDateStr() {
        Date dt = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(dt);
    }

    /**
     * 将一个毫秒数时间转换为相应的时间格式 (首页预约时间)
     *
     * @param longSecond
     * @return
     */
    public static String formatDateLongToStringHome(long longSecond) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(longSecond);
        SimpleDateFormat format = new SimpleDateFormat("MM-dd  E  HH:mm");
        return format.format(gc.getTime());
    }

    /**
     * 将一个毫秒数时间转换为相应的时间格式(只有日期,如:yyyy年MM月dd日)
     *
     * @param longSecond
     * @return
     */
    public static String formateDateLongToStringOnlyDate2(long longSecond) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(longSecond);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        return format.format(gc.getTime());
    }

    /**
     * 日期格式字符串转换成时间戳
     */
    public static long date2TimeStamp(String date_str){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日\tHH:mm");
            return sdf.parse(date_str).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 将一个毫秒数时间转换为相应的时间格式(只有时间,如:HHmm)
     *
     * @param longSecond
     * @return
     */
    public static String formateDateLongToStringOnlyTime(long longSecond) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(longSecond);
        SimpleDateFormat format = new SimpleDateFormat("HHmm");
        return format.format(gc.getTime());
    }
}
//    /**
//     * 日期转毫秒值
//     */
//    public static long StrTOMillionSeconds(String str) {
//        // 日期转毫秒
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
//        long millionSeconds = 0;//毫秒
//        try {
//            millionSeconds = sdf.parse(str).getTime();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return millionSeconds;
//    }

