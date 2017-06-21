package com.ourslook.zuoyeba.utils;

/**
 * 关于数字的处理工具类
 * Created by dl on 2016/4/5.
 */
public class MathUtils {
    /**
     * 数字大于四位数时单位转换并保留一位小数，返回String类型字符串
     *
     * @param count     需要处理的数字
     * @param smallUnit 需要转换时的单位单位
     * @param bigunit   不需要转换时的单位单位
     * @return
     */
    public static String fourConvert(int count, String smallUnit, String bigunit) {
        double c;
        if (count > 10000) {
            c = ((double) (count)) / 10000;
            return String.format("%.1f", c) + bigunit;
        } else {
            return Integer.toString(count) + smallUnit;
        }
    }

    /**
     * 距离转换，当距离大于1000m时单位转换为km
     *
     * @param count
     * @return
     */
    public static String distanceConvert(int count) {
        double c;
        if (count > 1000) {
            c = ((double) (count)) / 1000;
            return String.format("%.1f", c) + "km";
        } else {
            return Integer.toString(count) + "m";
        }
    }

}
