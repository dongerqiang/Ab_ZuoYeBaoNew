package com.ourslook.zuoyeba;

/**
 * Created by wy on 2015/12/10.
 * 接口地址
 */
public class ServerURL {

    public static final String SERVER_URL = "http://www.shcloudwindow.cn/zuoyebao";//服务端api地址
    //http://www.shcloudwindow.cn/zuoyebao
    //http://120.77.13.54:8080/zuoyebao
//    public static final String SERVER_URL = "http://223.202.123.125:8080/zuoyebao";//服务端api地址
//    public static final String SERVER_URL = "http://27.115.88.134:8080/zuoyebao";//服务端api地址

    /**
     * 公共接口
     * 上传图片
     */
    public static final String UPLOADIMAGE = SERVER_URL + "/api/common/uploadImage";
    /**
     * 省市区列表
     */
    public static final String GETAREA = SERVER_URL + "/api/common/getArea";
    /**
     * 年级列表
     */
    public static final String GETGRADELIST = SERVER_URL + "/api/common/getGradeList";
    /**
     * 科目列表
     */
    public static final String GETCOURSELIST = SERVER_URL + "/api/common/getCourseList";
    /**
     * 课程单价列表
     */
    public static final String GETCOURSEPRICELIST = SERVER_URL + "/api/common/getCoursePriceList";
    /**
     * 取消订单原因列表
     */
    public static final String GETCOURSECANCELREASONLIST = SERVER_URL + "/api/common/getCourseCancelReasonList";
    /**
     * 客服电话
     */
    public static final String GETSERVICETEL = SERVER_URL + "/api/common/getServiceTel";
    /**
     * 银行列表
     */
    public static final String GETBANKLIST = SERVER_URL + "/api/common/getBankList";
    /**
     * 获取版本
     */
    public static final String GETVERSION = SERVER_URL + "/api/common/getVersion";
    /**
     * 获取红包设定
     */
    public static final String GETREDPACKETSETTING = SERVER_URL + "/api/common/getRedPacketSetting";


    /**
     * 会员接口
     * 获取验证码
     */
    public static final String GETMOBILECODE = SERVER_URL + "/api/account/getMobileCode";
    /**
     * 注册
     */
    public static final String REGISTER = SERVER_URL + "/api/account/register";
    /**
     * 登录
     */
    public static final String LOGIN = SERVER_URL + "/api/account/login";
    /**
     * 修改手机号(仅学生端)
     */
    public static final String UPDMOBILE = SERVER_URL + "/api/account/updMobile";
    /**
     * 修改密码
     */
    public static final String UPDPWD = SERVER_URL + "/api/account/updPwd";
    /**
     * 忘记密码
     */
    public static final String FORGETPWD = SERVER_URL + "/api/account/forgetPwd";
    /**
     * 登出
     */
    public static final String LOGOUT = SERVER_URL + "/api/account/logout";
    /**
     * 修改坐标
     */
    public static final String UPDCOORDINATE = SERVER_URL + "/api/account/updCoordinate";
    /**
     * 个人信息
     */
    public static final String GETMEMBERINFO = SERVER_URL + "/api/account/getMemberInfo";
    /**
     * 首页预约
     */
    public static final String GETAPPOINTMENT = SERVER_URL + "/api/account/getAppointment";
    /**
     * 根据环信账号获取用户信息
     */
    public static final String GETMEMBERBYIM = SERVER_URL + "/api/account/getMemberByIm";
    /**
     * 获取老师经纬度
     */
    public static final String GETCOORDINATE = SERVER_URL + "/api/account/getCoordinate";


    /**
     * 学生接口
     * 修改学生资料
     */
    public static final String UPDSTUDENTINFO = SERVER_URL + "/api/student/updStudentInfo";
    /**
     * 发订单
     */
    public static final String ADDWORK = SERVER_URL + "/api/student/addWork";
    /**
     * 刷新订单
     */
    public static final String REFRESHWORK = SERVER_URL + "/api/student/refreshWork";
    /**
     * 查看老师详情
     */
    public static final String GETTEACHERDETAILBYID = SERVER_URL + "/api/student/getTeacherDetailById";
    /**
     * 确认老师
     */
    public static final String CONFIRMTEACHER = SERVER_URL + "/api/student/confirmTeacher";
    /**
     * 放弃订单(结束匹配)
     */
    public static final String DELWORK = SERVER_URL + "/api/student/delWork";
    /**
     * 订单列表
     */

    public static final String GETWORKLIST = SERVER_URL + "/api/student/getWorkList";
    /**
     * 订单详情
     */
    public static final String GETWORKDETAIL = SERVER_URL + "/api/student/getWorkDetail";
    /**
     * 取消订单
     */
    public static final String CANCELWORKBYID = SERVER_URL + "/api/student/cancelWorkById";
    /**
     * 订单评价
     */
    public static final String COMMENTWORKBYID = SERVER_URL + "/api/student/commentWorkById";
    /**
     * 获取订单价格
     */
    public static final String GETWORKCOSTBYID = SERVER_URL + "/api/student/getWorkCostById";
    /**
     * 我的红包
     */
    public static final String GETREDPACKETLIST = SERVER_URL + "/api/student/getRedPacketList";
    /**
     * 我的信用卡
     */
    public static final String GETCREDITCARD = SERVER_URL + "/api/student/getCreditCard";
    /**
     * 绑定信用卡
     */
    public static final String ADDCREDITCARD = SERVER_URL + "/api/student/addCreditCard";
    /**
     * 取消绑定信用卡
     */
    public static final String CANCELCREDITCARD = SERVER_URL + "/api/student/cancelCreditCard";
    /**
     * 我的积分
     */
    public static final String GETINTEGRATIONLIST = SERVER_URL + "/api/student/getIntegrationList";
    /**
     * 绑定信用卡(仅支付宝代扣)
     */
    public static final String ADDCREDITCARDALIPAY = SERVER_URL + "/api/student/addCreditCardAlipay";


    /**
     * 老师接口
     * 老师申请
     */
    public static final String APPLYTEACHER = SERVER_URL + "/api/teacher/applyTeacher";
    /**
     * 老师资料变更
     */
    public static final String APPLYUPDTEACHERINFO = SERVER_URL + "/api/teacher/applyUpdTeacherInfo";
    /**
     * 接课程
     */
    public static final String GRABORDERLIST = SERVER_URL + "/api/teacher/grabOrderList";
    /**
     * 抢单
     */
    public static final String GRABORDERBYID = SERVER_URL + "/api/teacher/grabOrderById";
    /**
     * 取消抢单
     */
    public static final String CANCELGRABORDERBYID = SERVER_URL + "/api/teacher/cancelGrabOrderById";
    /**
     * 订单列表
     *
     */
    public static final String GETORDERLIST = SERVER_URL + "/api/teacher/getOrderList";
    /**
     * 订单详情
     */
    public static final String GETORDERDETAIL = SERVER_URL + "/api/teacher/getOrderDetail";
    /**
     * 取消订单
     */
    public static final String CANCELORDERBYID = SERVER_URL + "/api/teacher/cancelOrderById";
    /**
     * 开始上门（仅上门）
     */
    public static final String DOORORDERBYID = SERVER_URL + "/api/teacher/doorOrderById";
    /**
     * 开始授课（仅上门）
     */
    public static final String STARTORDERBYID = SERVER_URL + "/api/teacher/startOrderById";
    /**
     * 结束授课（仅上门）
     */
    public static final String ENDORDERBYID = SERVER_URL + "/api/teacher/endOrderById";
    /**
     * 我的收益
     */
    public static final String GETINCOMELIST = SERVER_URL + "/api/teacher/getIncomeList";


    /**
     * 广告信息接口
     * 轮番广告
     */
    public static final String GETADVERTISEMENT = SERVER_URL + "/api/info/getAdvertisementList";
    /**
     * 关于我们
     */
    public static final String GETABOUTUS = SERVER_URL + "/api/info/getAboutUs";
    /**
     * 我的消息
     */
    public static final String GETMESSAGELIST = SERVER_URL + "/api/info/getMessageList";
    /**
     * 设置消息已读
     */
    public static final String READMESSAGE = SERVER_URL + "/api/info/readMessage";
    /**
     * 用户协议
     */
    public static final String GETPROTOCOL = SERVER_URL + "/api/info/getProtocol";


    /**
     * 作业接口
     * 开始作业（仅语音和视频）
     */
    public static final String STARTWORKBYID = SERVER_URL + "/api/work/startWorkById";
    /**
     * 结束作业（仅语音和视频）
     */
    public static final String ENDWORKBYID = SERVER_URL + "/api/work/endWorkById";
    //获取老师通知
    public static final String GET_TEACHER_NOTIFY_BY_TEACHER_ID = SERVER_URL + "/api/teacher/getTeacherNotifyByTeacherId";
}
