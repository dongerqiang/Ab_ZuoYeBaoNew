package com.ourslook.zuoyeba.view.dialog.common;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.ourslook.zuoyeba.utils.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.Properties;


/**
 * 常用工具类
 *
 * @author tu
 */
public class CommonUtils {

    @SuppressWarnings("unused")
    private static final String tag = CommonUtils.class.getSimpleName();

    /**
     * 网络类型
     **/
    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;

    private ProgressDialog mProgress = null;    
    /**
     * 根据key获取config.properties里面的值
     *
     * @param context
     * @param key
     * @return
     */
    public static String getProperty(Context context, String key) {
        try {
            Properties props = new Properties();
            InputStream input = context.getAssets().open("config.properties");
            if (input != null) {
                props.load(input);
                return props.getProperty(key);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 检测网络是否可用
     *
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    /**
     * 获取当前网络类型
     *
     * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
     */
    public static int getNetworkType(Context context) {
        int netType = 0;
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if (!TextUtils.isEmpty(extraInfo)) {
                if (extraInfo.toLowerCase(Locale.getDefault()).equals("cmnet")) {
                    netType = NETTYPE_CMNET;
                } else {
                    netType = NETTYPE_CMWAP;
                }
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NETTYPE_WIFI;
        }
        return netType;
    }

    /**
     * 判断wifi网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                State networkState = mWiFiNetworkInfo.getState();
                if (networkState.equals(State.CONNECTED)
                        && mWiFiNetworkInfo.isAvailable()) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 判断3G网络状态 return boolean
     */
    public static boolean get3Gstatus(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileInfo = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileInfo != null) {
            if (mobileInfo.getState().toString().equals("CONNECTED")) {
                // mobileInfo.getState().toString();
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * 判断手机网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                State networkState = mMobileNetworkInfo.getState();
                if (networkState.equals(State.CONNECTED)
                        && mMobileNetworkInfo.isAvailable()) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 判断SDCard是否存在,并可写
     *
     * @return
     */
    public static boolean checkSDCard() {
        String flag = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(flag)) {
            return true;
        }
        return false;
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 获取屏幕显示信息对象
     *
     * @param context
     * @return
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm;
    }

    /**
     * dp转pixel
     */
    public static float dpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    /**
     * pixel转dp
     */
    public static float pixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / (metrics.densityDpi / 160f);
    }

    /*****************************分割线 calendar view use start**********************************/
    /**
     * 描述：根据分辨率获得字体大小.
     *
     * @param screenWidth  the screen width
     * @param screenHeight the screen height
     * @param textSize     the text size
     * @return the int
     */
    public static int resizeTextSize(int screenWidth, int screenHeight, int textSize) {
        float ratio = 1;
        try {
            float ratioWidth = (float) screenWidth / 480;
            float ratioHeight = (float) screenHeight / 800;
            ratio = Math.min(ratioWidth, ratioHeight);
        } catch (Exception e) {
        }
        return Math.round(textSize * ratio);
    }

    /**
     * 描述：dip转换为px
     *
     * @param context
     * @param dipValue
     * @return
     * @throws
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return Math.round(dipValue * scale);
    }

    /**
     * 描述：px转换为dip
     *
     * @param context
     * @param pxValue
     * @return
     * @throws
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return Math.round(pxValue / scale);
    }

    /**
     * 描述：px转换为sp
     *
     * @param context
     * @param pxValue
     * @return
     * @throws
     */
    public static int px2sp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return Math.round(pxValue / scale);
    }

    /**
     * 描述：sp转换为px
     *
     * @param context
     * @param spValue
     * @return
     * @throws
     */
    public static int sp2px(Context context, float spValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return Math.round(spValue * scale);
    }
    /*****************************分割线 calendar view use end**********************************/

    /**
     * 生成二维码图片
     *
     * @param str
     * @return
     */
    /*
     * public static Bitmap create2DCode(String str) { try { //
	 * 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败 BitMatrix matrix = new
	 * MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, 300, 300); int
	 * width = matrix.getWidth(); int height = matrix.getHeight();
	 * 
	 * // 二维矩阵转为一维像素数组,也就是一直横着排了 int[] pixels = new int[width * height]; for
	 * (int y = 0; y < height; y++) { for (int x = 0; x < width; x++) { if
	 * (matrix.get(x, y)) { pixels[y * width + x] = 0xff000000; } } }
	 * 
	 * Bitmap bitmap = Bitmap.createBitmap(width, height,
	 * Bitmap.Config.ARGB_8888); // 通过像素数组生成bitmap,具体参考api
	 * bitmap.setPixels(pixels, 0, width, 0, 0, width, height); return bitmap; }
	 * catch (WriterException e) { e.printStackTrace(); } return null; }
	 */

    /**
     * 短信分享
     *
     * @param mContext
     * @param smstext  短信分享内容
     * @return
     */
    public static Boolean sendSms(Context mContext, String smstext) {
        Uri smsToUri = Uri.parse("smsto:");
        Intent mIntent = new Intent(Intent.ACTION_SENDTO,
                smsToUri);
        mIntent.putExtra("sms_body", smstext);
        mContext.startActivity(mIntent);
        return null;
    }

    /**
     * 邮件分享
     *
     * @param mContext
     * @param title    邮件的标题
     * @param text     邮件的内容
     * @return
     */
    public static void sendMail(Context mContext, String title, String text) {
        // 调用系统发邮件
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        // 设置文本格式
        emailIntent.setType("text/plain");
        // 设置对方邮件地址
        emailIntent.putExtra(Intent.EXTRA_EMAIL, "");
        // 设置标题内容
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        // 设置邮件文本内容
        emailIntent.putExtra(Intent.EXTRA_TEXT, text);
        mContext.startActivity(Intent.createChooser(emailIntent,
                "Choose Email Client"));
    }

    /**
     * 隐藏软键盘
     *
     * @param activity
     */
    public static void hideKeyboard(Activity activity) {
        if (activity != null) {
            InputMethodManager imm = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                        .getWindowToken(), 0);
            }
        }
    }

    /**
     * 显示软键盘
     *
     * @param activity
     */
    public static void showKeyboard(Activity activity) {
        if (activity != null) {
            InputMethodManager imm = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (!imm.isActive()) {
                imm.showSoftInputFromInputMethod(activity.getCurrentFocus()
                        .getWindowToken(), 0);
            }
        }
    }

    /**
     * 是否横屏
     *
     * @param context
     * @return true为横屏，false为竖屏
     */
    public static boolean isLandscape(Context context) {
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是平板 这个方法是从 Google I/O App for Android 的源码里找来的，非常准确。
     *
     * @param context
     * @return
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * 取得设备sdcard路径
     *
     * @return
     */
    public static String getSdcardRootPathString() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString() + "/";
        }
        return null;
    }

    /**
     * 返回两位日期 字符串
     *
     * @param intDate 整形日期
     * @return
     */
    public static String getTwoDate(int intDate) {
        String strDate = "";
        if (intDate > 9) {
            strDate = "" + intDate;
        } else {
            strDate = "0" + intDate;
        }
        return strDate;
    }


    /**
     * 处理合适尺寸的图片，设置在imageview
     *
     * @param path
     * @param w
     * @param h
     * @return
     */
    @SuppressWarnings("deprecation")
    public static Bitmap disposeImage(String path, int w, int h) {
        // 图片解析的配置
        Options opts = new Options();
        // 不去真的解析图片，只是获取图片的头部信息，宽高
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        // 图片真实宽高
        int height = opts.outHeight;
        Log.i("iamge height", height + "");
        int width = opts.outWidth;
        Log.i("iamge width", width + "");
        // 控件宽高，按比例缩放图片分辨率
        float wmHeight = h;
        float wmWidth = w;
        // 真正解析图片
        opts.inJustDecodeBounds = false;
        if ((height / wmHeight) > (width / wmWidth)) {
            opts.inSampleSize = (int) (height / wmHeight);
        } else {
            opts.inSampleSize = (int) (width / wmWidth);
        }
        if (opts.inSampleSize <= 0)
            opts.inSampleSize = 1;
        Log.i("倍数", opts.inSampleSize + "");
        opts.inPreferredConfig = Config.ARGB_8888;// 该模式是默认的,可不设
        opts.inPurgeable = true;// 同时设置才会有效
        opts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收
        Bitmap bitmap = BitmapFactory.decodeFile(path, opts);
        // 将bitmap设置进imageview

        return bitmap;
    }

    /**
     * 图片压缩
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
        int options = 100;
        NLogUtil.logD("图片原始大小", baos.toByteArray().length + "");
        while (baos.toByteArray().length / 1024 > 2560) {  //循环判断如果压缩后图片是否大于2560kb,大于继续压缩
            baos.reset();//重置baos即清空baos  
            options -= 10;//每次都减少10  
            NLogUtil.logD("图片" + options + "%大小", baos.toByteArray().length + "");

            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片  
        return bitmap;
    }
//	1、将File、FileInputStream 转换为byte数组：
//    File file = new File("file.txt");
//    InputStream input = new FileInputStream(file);
//    byte[] byt = new byte[input.available()];
//    input.read(byt);
// 
//	2、将byte数组转换为InputStream：
//	    byte[] byt = new byte[1024];
//	    InputStream input = new ByteArrayInputStream(byt);
//	 
//	3、将byte数组转换为File：
//	    File file = new File('');
//	    OutputStream output = new FileOutputStream(file);
//	    BufferedOutputStream bufferedOutput = new BufferedOutputStream(output);
//	    bufferedOutput.write(byt);

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion(Context mContext) {
        try {
            PackageManager manager = mContext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return ("0000");
        }
    }

    /**
     * 获取设备唯一标示号
     * @param mContext
     * @return
     */
    public static String getDeviceId(Context mContext){
        TelephonyManager tm = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    /**
     * 金额处理，四舍五入 保留两位 0.00
     *
     * @param money
     * @return
     */
    public static String dealMoney(BigDecimal money) {
        return dealMoney(money, 2);
    }

    /**
     * 金额处理，四舍五入 保留两位 #,##0.00 ,三位一个逗号
     *
     * @param money
     * @param scale 精确多少位
     * @return
     */
    public static String dealMoney(BigDecimal money, int scale) {
        return dealMoney(money, 2, "#,##0.00");
    }

    /**
     * 金额处理，四舍五入 保留两位 0.00
     *
     * @param money
     * @param scale       精确多少位
     * @param formatStyle "0.00"
     * @return
     */
    public static String dealMoney(BigDecimal money, int scale, String formatStyle) {
        if (StringUtils.isNotEmpty(money)) {
            BigDecimal tempMoney = money.setScale(scale, BigDecimal.ROUND_HALF_UP);
            DecimalFormat myformat = new DecimalFormat(formatStyle);
            return myformat.format(tempMoney);
        } else {
            return "0.00";
        }
    }

    /**
     * 判断输入字符str长度是否 小于等于lenth
     *
     * @param str
     * @param lenth
     * @return
     */
    public static boolean VerificationStrLenth(String str, int lenth) {
        //int strLenth = VerificationUtil.getStrlength(str);
        int strLenth = str.length();
        return strLenth <= lenth;
    }

    //保留两位小数
    public static String getTwoXiaoShu(double value) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(value);
    }

    /**
     * 去除字符串的所有空格
     *
     * @param str
     * @return
     */
    public static String replaceStrAllSpace(String str) {
        if(TextUtils.isEmpty(str))
            return "";
        return str.replaceAll(" ", "");
    }

    /**
     * 去除字符串的所有标点符号
     *
     * @param s
     * @return
     */
    public static String format(String s) {
        String str = s.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]", "");
        return str;
    }
    
    /**
     * 去除字符串的指定字符
     *
     * @param s
     * @return
     */
    public static String format(String s,String sure_str) {
        String str = s.replaceAll(sure_str, "");
        return str;
    }
    
    /**
     * 将大写转换成小写
     *
     * @param s
     * @return
     */
    public static String formatX(String s) {
        String str = s.replaceAll("X", "x");
        return str;
    }
    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public static boolean isAppOnForeground(Context mContext) {
        // Returns a list of application processes that are running on the
        // device

        ActivityManager activityManager = (ActivityManager)mContext.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName =mContext.getApplicationContext().getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }
    /**
     * 判断应用是否已经启动
     *
     * @param context     一个context
     * @param packageName 要判断应用的包名
     * @return boolean
     */
    public static boolean isAppAlive(Context context, String packageName) {
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos
                = activityManager.getRunningAppProcesses();
        for (int i = 0; i < processInfos.size(); i++) {
            if (processInfos.get(i).processName.equals(packageName)) {
                NLogUtil.logI("NotificationLaunch",
                        String.format("the %s is running, isAppAlive return true", packageName));
                return true;
            }
        }
        NLogUtil.logI("NotificationLaunch",
                String.format("the %s is not running, isAppAlive return false", packageName));
        return false;
    }

    /**
	 * 判断字符串是否包含一些字符 contains
	 */
	public static boolean containsString(String src, String dest) {
		boolean flag = false;
		if (src.contains(dest)) {
			flag = true;
		}
		return flag;
	}
	
	/**
	 * 判断手机是否安装微信客户端
	 * @param context
	 * @return
	 */
	public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 自适应处理webview加载html数据 ，主要处理图片自适应屏幕宽度
     * 大于屏幕宽度的图片宽度设置为屏幕宽度减去左右padding20，高度auto
     * @param context
     * @param content
     * @return
     */
    public static String autoAdapterWebView(Context context,String content){
        return  "<head><style>img{max-width:"
                +(CommonUtils.pixelsToDp(CommonUtils.getScreenWidth(context),context) - 20)
                    +"px !important;height:auto !important;}</style></head>" + content;
    }

}
