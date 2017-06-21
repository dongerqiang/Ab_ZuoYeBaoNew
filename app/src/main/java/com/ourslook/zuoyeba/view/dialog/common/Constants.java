package com.ourslook.zuoyeba.view.dialog.common;

/**
 * 常量类
 *
 * @author tu
 */
public class Constants {

    /**
     * 下拉刷新时间 id
     */
    /**
     * 主页下拉刷新时间id
     **/
    public static final int HOME_MAIN_ID = 1;

    /**
     * 文件保存地址
     */
    /**
     * 今资讯根目录
     **/
    public static final String ROOT_PATH = "zuoyebao";
    /**
     * 图片目录
     **/
    public static final String IMAGE_PATH = ROOT_PATH + "image";
    /**
     * 下载目录
     **/
    public static final String DOWNLOAD_PATH = ROOT_PATH + "download";
    /** 数据库名字**/
    public static final String DATABASE_NAME = "jnews_databse";

    /**
     * shared 保存数据xml文件名
     */
    /** 批发搜索历史*/
    public static final String SHARED_SEARCH_HISTORY_WHOLSEALE = "shared_search_wholseale";
    /** 第一次安装使用app*/
    public static final String SHARED_FIRST_APP = "first_uses_app";
    /** 保存用户信息*/
    public static final String SHARED_USER_INFO = "user_login_info";
    
    /**
     * Bundler 数据传输 key
     */
    public static final String BUNDLER_INFO = "INFO"; // 详细信息
    /**
     * Bundler 数据传输 key string
     */
    public static final String BUNDLER_INFO_S = "INFO_S"; // 详细信息
    /**
     * Bundler activity  return result 数据 key
     */
    public static final String BUNDLER_RETURN_INFO = "INFO_RETURN"; // activity返回结果数据
    /**
     * Bundler 数据传输 tag 用于区分界面跳转
     */
    public static final String BUNDLER_TAG = "tags"; // 跳转标示
    /**
     * shareSDKID dcbdf0180538
     * shareSDKSECRET 9f05dd25238fc328f8ab1c021432d2e6
     * 原来的微信签名  MD5 : B0:26:2E:FE:DD:B4:0E:44:81:86:D0:E9:8F:CF:4D:A1
     */
    /**
     * QQAPPID
     */
    public static final String QQ_APP_ID = "1105042926";
    /**
     * QQAPPKEY
     */
    public static final String QQ_APP_KEY = "tgH5hW90enfEJVrp";
    /**
     * 新浪APPKEY
     */
    public static final String SINA_APP_KEY = "4253486339";
    /**
     * 新浪SECRET
     */
    public static final String SINA_SECRET = "eaaf874cae35b71fba8ac2ec4018d64f";
    /**
     * 微信APPID
     */
    public static final String WX_APP_ID = "wx6a89b435ec5bde68";
    /**
     * 微信APPSECRET
     */
    public static final String WX_APP_SECRET = "98b519295e122948a9fbcf717653beb9";
    /**
     * 微信商户号ID
     */
    public static final String WX_APP_PARTNER_ID = "1301424901";
    /**
     * 保存本地手机号
     */
    public static final String USER_MOBILE = "mobile";
    /**
     * 保存本地密码
     */
    public static final String USER_PWD = "password";
    /**
     * 保存本地是否记住密码标记
     */
    public static final String USER_ISREMEMBER_PWD = "isRememberPwd";
    /**
     * 保存第三方账号
     */
    public static final String THREE_USER_ACCOUNT = "threeUserAccount";
    /**
     * 保存第三方昵称
     */
    public static final String THREE_USER_NICKNAME = "threeUserNickName";
    /**
     * 保存第三方图像
     */
    public static final String THREE_USER_PHOTO = "threeUserPhoto";
    /**
     * 保存第三方登录类型
     */
    public static final String THREE_USER_TYPE = "threeUserType";
    /**
     * 包含"http://"字符串常量
     */
    public static final String HTTP_STR = "http://";
    /**
     * 支付宝合作者身份(PID)
     */
    public static final String ZHIFUBAO_PID = "2088121183315728";//已确认
    /**
     * 支付宝公钥
     */
    public static final String ZHIFUBAO_RSA_PUBLIC = 
    	   "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXy" +
    	   "iUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJB" +
    	   "VHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9j" +
    	   "e9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB";
    /**
     * 商户收款账号
     */
    public static final String ZHIFUBAO_ACCOUNT = "hlwwkdm@163.com";//已确认
    /**
     * 商户私钥，pkcs8格式
     */
    public static final String ZHIFUBAO_RSA_PRIVATE = 
    	   "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANFAMeZLu" +
    	   "exByzNsJbxv2RsQcxAiIGB3HQar20GgIwOgDaikgDGBQqiaPA9O9ehHOY" +
    	   "k4tWiAa24iGmy5yhu8JHyQa2ak1tlh+Vhwl362fiHurtnTQlN4hCbvMcI" +
    	   "DWOwAQ0tPcLldWuLCoiA5CU1JgeShVa9IT+CLZD/xp22n5kWJAgMBAAEC" +
    	   "gYARj42ggiJsTWk8O4s/MTPLLao6ZGLLNbPd53ZTH5lQqJH84TFQ9kQd2" +
    	   "jPIJRxGDhQqC/RNfNmHOJ13GnB3u2ZkVu6/Jr8uwbNaCGX27P/CAjvwrx" +
    	   "4jOhHsG9m0UjWoU9t+y2y6bnmZeWIjNFXnJw6E+ytGtkfJpQbDh7+RPty" +
    	   "UAQJBAOzY35psTkSrHPxNJh6mc7p5V4OVNczC2pMJOvENIJTNfIDboXOa" +
    	   "2wtIUgiDpaD3kquSmQqT+WF5Y58+cpmqMeECQQDiLAfPGgr68zOlcUoeg" +
    	   "2LkzgrTjD0gQFvqsG6vhxGKMD0TrZVAIxTL2VAHgrMoLuczAP90Wufe3f" +
    	   "z5FbYOJlipAkEAqNq5F3vtlPE1Of66lZFv4lrN9IZ1E6U7dSZYejA0sUG" +
    	   "KaMesZCHb1kBaE63fcFHwpBdgunTijwae6pH32+vFoQJAYVnUbqdmDIh8" +
    	   "NX0jCylAev3ZGR+m++fX6JfSvMjlmtaSo5K2yGmRPQEn+mOem/A8Ye7PB" +
    	   "hVO8tYYWlqnB0YoKQJBAIb+EuxN8dSyDOBIzvGJE/uMImm9Fk7B6Df261" +
    	   "6V9vUkE7rJwkr4DfPx07wI+1FvNVsmTBXAxmajUSn/zdD+jig=";
    /**
     * 支付宝安全支付服务apk的名称，必须与assets目录下的apk名称一致
     */
    public static final String ZHIFUBAO_ALIPAY_PLUGIN_NAME = "alipay_plugin223_0309.apk";
    /**
     * 支付宝请求网址
     */
    public final static String ZHIFUBAO_SERVER_URL = "https://msp.alipay.com/x.htm";
    /**
     * 保存手机登录状态时的密码
     */
    public final static String PHONE_PWD_STATUS = "phone_pwd_status";
    /**
     * 线程延时数
     */
    public final static int HANDLER_DELAYED = 200;
    /**
     * 分享所需要的链接
     */
    public final static String SHARE_LINK = "http://www.wkdmei.com";
    /**
     * 分享所需要的链接shareSDK
     */
    public final static String SHARE_SDK = "http://www.sharesdk.cn";
	/**
	 * 以下为要调用的接口
	 */
    /**
     * 服务地址 url
     */
    public static final String SERVER_URL = "http://114.215.84.189:8089/jnews/";
//    public static final String SERVER_URL = "http://zl0318.6655.la:12457/jnews";
    /**
     * 获取验证码
     */
    public static String GET_CODE = SERVER_URL+"api/user/sendCode";
    /**
     * 注册
     */
    public static String REGISTER = SERVER_URL+"api/user/register";
    /**
     * 忘记密码
     */
    public static String FORGET_PWD = SERVER_URL+"api/user/forgetPwd";
    /**
     * 登录(手机号登录)
     */
    public static String LOGIN = SERVER_URL+"api/user/login";
    /**
     * 检查版本更新
     */
    public static String VERSION = SERVER_URL+"api/versionInfo/findVersionInfo";
    /**
     * 意见反馈
     */
    public static String SET_SUGGESTION = SERVER_URL+"api/feedBack/saveFeedBack";
    /**
     * 修改资料
     */
    public static String UPDATE_USER_INFO = SERVER_URL+"api/user/updateUser";
    /**
     * 修改密码
     */
    public static String UPDATE_PWD = SERVER_URL+"api/user/updatePwd";
    /**
     * 用户信息
     */
    public static String USER_INFO = SERVER_URL+"api/user/findUserDetail";
    /**
     * 所有频道信息
     */
    public static String FIND_CHANNELS = SERVER_URL+"api/channel/findChannels";
    /**
     * 所有默认频道信息（未登录）
     */
    public static String FIND_DEFAULT_CHANNELS = SERVER_URL+"api/channel/findDefaultChannels";
    /**
     * 兴趣管理(我的频道)
     */
    public static String INTEREST_MANAGER = SERVER_URL+"api/channel/findMyChannels";
    /**
     * 可添加的频道（）
     */
    public static String FIND_ADD_CHANNELS = SERVER_URL+"api/channel/findAddChannels";
    /**
     * 删除频道（）
     */
    public static String DEL_MY_CHANNELS = SERVER_URL+"api/channel/delMyChannel";
    /**
     * 频道排序
     */
    public static String MY_CHANNELS_SORT = SERVER_URL+"api/channel/changeSort";

    /**
     * 上传图像
     */
    public static String UPLOAD_PHOTO = SERVER_URL+"api/user/photoUpload";
    /**
     * 签到
     */
    public static String SIGN = SERVER_URL+"api/user/sign";
    /**
     * 商品信息列表
     */
    public static String FIND_SHOPS = SERVER_URL+"api/shop/findShops";
    /**
     * 根据频道id获取咨询列表
     */
    public static String GET_NEWS_LIST = SERVER_URL+"api/information/findInformations";
    /**
     * 单个商品详情
     */
    public static String GOOD_DETAIL = SERVER_URL+"api/shop/findShopDetail";
    /**
     * 确认兑换
     */
    public static String CONFIRM_EXCHANGE = SERVER_URL+"api/shop/exchangeShop";
    /**
     * 消息列表
     */
    public static String MESSAGE_LIST = SERVER_URL+"api/message/findMessages";
    /**
     * 消息详情
     */
    public static String MESSAGE_DETAIL = SERVER_URL+"api/message/findMessageDetail";
    /**
     * 查询活动信息（活动）
     */
    public static String FIND_ACTIVITY_LIST = SERVER_URL+"api/activity/findActivitys";
    /**
     * 活动详情
     */
    public static String FIND_ACTIVITY_DETAIL = SERVER_URL+"api/activity/findActivityDetail";
    /**
     * 查询活动信息（合作）
     */
    public static String FIND_COOPERATION_LIST = SERVER_URL+"api/cooperation/findCooperations";
    /**
     * 资讯详情
     */
    public static String FIND_NEWS_DETAILS = SERVER_URL+"api/information/findInformationDetail";
    /**
     * 积分明细
     */
    public static String MY_CREDITS = SERVER_URL+"api/aboutMy/findMyCreditsDetail";
    /**
     * 订阅（添加频道）
     */
    public static String ADD_CHANNEL = SERVER_URL+"api/channel/addMyChannel";
    /**
     * 收藏资讯
     */
    public static String GET_NEWS = SERVER_URL+"api/commentAndLike/saveMyHistory";
    /**
     * 点赞
     */
    public static String CLICK_ZAN = SERVER_URL+"api/commentAndLike/saveInfoLike";
    /**
     * 删除资讯
     */
    public static String DEL_NEWS = SERVER_URL+"api/information/delInformation";
    /**
     * 资讯评论列表
     */
    public static String NEWS_COMMENT = SERVER_URL+"api/commentAndLike/findInfoComments";
    /**
     * 推荐订阅
     */
    public static String  RECOMMENT_SUB= SERVER_URL+"api/find/recommendSubscription";
    /**
     * 发现观点
     */
    public static String  FIND_VIEW_POINTS= SERVER_URL+"api/find/findViewpoints";
    /**
     * 参加活动
     */
    public static String JOIN_ACTIVITY = SERVER_URL+"api/aboutMy/SaveActivity";
    /**
     * 我的观点-评论
     */
    public static String MY_COMMENT = SERVER_URL+"api/aboutMy/findMyComments";
    /**
     * 我的观点-通知
     */
    public static String MY_NOTICE = SERVER_URL+"api/aboutMy/findReplyMeComments";
    /**
     * 我的收藏
     */
    public static String MY_GET = SERVER_URL+"api/aboutMy/findMyHistory";
    /**
     * 保存第三方账号
     */
    public static String THREE_ACCOUNT = SERVER_URL+"api/user/saveThreeAccount";
    /**
     * 评论
     */
    public static String COMMIT_COMMENT = SERVER_URL+"api/commentAndLike/saveInfoComment";
    /**
     * 下单
     */
    public static String SAVE_ORDER = SERVER_URL+"api/order/saveOrder";
    /**
     * 支付宝异步请求
     */
    public static String ORDER_NOTIFY = SERVER_URL+"api/order/notify";
    /**
     * 银联获取TN码
     */
    public static String UN_TN = SERVER_URL+"api/unionpay/getTns";
    /**
     * 微信支付
     */
    public static String WX_PAY = SERVER_URL+"api/wxpay/getPayParams";
    /**
     * 合作点击
     */
    public static String COOPERATION_CLICK = SERVER_URL+"api/cooperation/cooperationClick";
    /**
     * 分享得积分
     */
    public static String SHARE_GET_CREDITS = SERVER_URL+"api/information/sharedGetCredits";
    /**
     * 活动是否参加过
     */
    public static String PLAY_ACTIVITYED = SERVER_URL+"api/activity/playActivityed";
    /**
     * 查询评论的回复列表
     */
    public static String FIND_REPLY_COMMENT_LIST = SERVER_URL+"api/commentAndLike/findReplyCommentList";
    //56af3815e0f55a533e000f6b  友盟key
}
