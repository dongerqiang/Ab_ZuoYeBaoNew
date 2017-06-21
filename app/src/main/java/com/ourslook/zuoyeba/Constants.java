package com.ourslook.zuoyeba;

import android.os.Environment;

/**
 * Created by wy on 2015/12/10.
 * 静态常量类
 */
public class Constants {


    /**
     * 文件夹路径
     */
    public static final String ROOT_PATH = "/ZUOYEBA";
    public static final String IMAGE_PATH = ROOT_PATH + "/IMAGE/";
    public static final String DOWNLOAD_PATH = ROOT_PATH + "/DOWNLOAD/";
    public static final String CHOOSE_PHOTO = Environment.getExternalStorageDirectory() + Constants.IMAGE_PATH + "zuoyebaUserIcon.jpg";
    public static final String CUT_PHOTO = Environment.getExternalStorageDirectory() + Constants.IMAGE_PATH + "zuoyebaUserCutIcon.jpg";
    /**
     * 请求码
     */
    public static final int CODE_201 = 201;
    public static final int CODE_202 = 202;
    public static final int CODE_203 = 203;
    public static final int CODE_204 = 204;

    public static final int CODE_500 = 500;
    public static final int CODE_501 = 501;
    public static final int CODE_502 = 502;
    public static final int CODE_503 = 503;
    public static final int CODE_504 = 504;
    public static final String POS_TOURIST = "1";//景点
    public static final String POS_HOTEL = "0";//酒店
    public static final String POS_MALL = "2";//商城

    public static final String GROUP_PRODUCT = "group_product";
    public static final String GROUP_HOTEL = "group_hotel";
    public static final String GROUP_TICKET = "group_ticket";

    /**
     * 账号密码的存储
     */
    public static final String ACCOUNT = "account";
    public static final String PASSWORD = "password";
    /**
     * 传递昵称
     */
    public static final String PUT_NIKE_NAME = "put_nike_name";
    public static final String RET_NIKE_NAME = "ret_nike_name";
    /**
     * 传递地址
     */

    public static final int CODE_REQUEST_CAMERA = 504;

    public static final int CODE_REQUEST_GALLERY = 505;

    public static final int CODE_REQUEST_ZOOM=506;
    public static final int CODE_507 = 507;
    public static final int CODE_508 = 508;
    /**
     * 关于降序 升序  价格排序  评价排序
     */
    public static final String DESC = "desc";
    public static final String ASC = "asc";
    public static final String PRICE = "price";
    public static final String SCORE = "score";
    public static final String SALES = "shopSales";



    /**
     * 传递订单
     */
    public static final String ORDER_VO = "order_vo";

    public static final String TICKET_VO = "ticket_vo";
    public static final String ORDER_NAME = "order_name";
    public static final String SHOPINFOS = "shopinfos";
    public static final String OBJECT_ID = "objectid";
    public static final String SHOP_NUM = "shop_num";
    public static final String ADDRESS_FLAG = "address";
    public static final String ORDER_NO = "orderno";
    public static final String BANNER_URL = "bannerurl";

    public static final String LEAD_KEY = "lead_key";//是不是第一次加载引导页
    public static final String RECOMMEND_CLUB = "recommend_club";//是不是第一次进入显示推荐俱乐部



    public static final String DIS_TYPE="dis_type";
    public static final String TEACHER="teacher";
    public static final String STUDENT="student";


    /**
     * 个人中心修改资料传递数据
     */
    public static final String PASS_NAME="pass_name";
    public static final String PASS_SEX="pass_sex";
    public static final String PASS_ADDRESS="pass_address";

    /**
     * 传递订单实体
     */
    public static final String PASS_ORDER="pass_order";

    public static int ROWS=10;

    /**
     *  下单到订单详情中要传的数据
     */
    public static final String PASS_STATUS="pass_status";
    public static final String TEACHER_ID="teacher_id";

    public static final String TYPE="104";

    //user type
    public static final String IS_STUDENT ="is_student";
    public static final String IS_TEACHER ="is_teacher";

    //address
    public static final String TEACHE_ADDRESS ="address";
    public static final String TEACHER_LIST ="teacher_list";
    public static final String TEACHE_LATITUDE ="address_latitude";
}
