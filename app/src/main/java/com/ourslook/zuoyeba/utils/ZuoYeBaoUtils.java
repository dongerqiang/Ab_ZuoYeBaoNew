package com.ourslook.zuoyeba.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by huangyi on 16/5/18.
 */
public class ZuoYeBaoUtils {


    public static String getStringData(Long time){
        Date date=new Date(time);
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       return format.format(date);
    }
    public static String getStringDataCorrectToMin(Long time){
        Date date=new Date(time);
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    }
    public static String getStringDataCorrectToSec(Long time){
        Date date=new Date(time);
        SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss");
        return format.format(date);
    }
    public static String getStringDataCorrectToDay(Long time){
        Date date=new Date(time);
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

}
