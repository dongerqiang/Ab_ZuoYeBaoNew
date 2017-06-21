package com.ourslook.zuoyeba;


import com.ourslook.zuoyeba.model.AccMemberModel;

/**
 * Created by wangyu on 2015/10/9.
 * 存放UserInfo等数据
 */
public final class AppConfig {
    public static AccMemberModel userVo;//用户实体
    //默认上海
    public static double latitude = 31.236305; //用户纬度
    public static double longitude = 121.480237; //用户经度
    //西安旺座现代城E座
    //public static double latitude = 34.13328; //用户纬度
    //public static double longitude = 108.531657; //用户经度
    public static String city = "上海市"; //城市名,默认上海市
    public static boolean isLogin;//登陆状态
    public static boolean iscreditvalid;//是否绑定支付宝

    public static String token = "";

    public static String imAcc = "";
    public static String imPWd = "";


    //修bug数据   在TeachTypeActivity  向订单详情传递数据     此为暴力解决法  勿动!!!
    public static long placeOrder;//下单的订单id

}
