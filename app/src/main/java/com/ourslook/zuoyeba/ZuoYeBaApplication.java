package com.ourslook.zuoyeba;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.ourslook.zuoyeba.receiver.CallReceiver;
import com.ourslook.zuoyeba.utils.DisplayUtils;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import cn.jpush.android.api.JPushInterface;


/**
 * 主要用作第三方初始化
 * Created by wy on 2015/10/15.
 */
public class ZuoYeBaApplication extends Application implements BDLocationListener {
    public static ImageLoader imageLoader;
    public static int heightPixels;
    public static int widthPixels;
    private static final String TAG = "ZuoYeBaApplication";

    public LocationClient locationClient;

    @Override
    public void onCreate() {
        Log.d(TAG, "[ZuoYeBaApplication] onCreate");
        super.onCreate();
        //bugly
        initBugly();

        // 获取屏幕高度
        heightPixels = DisplayUtils.getScreenHeight(this);
        // 获取屏幕宽度
        widthPixels = DisplayUtils.getScreenWidth(this);
        //初始化ImageLoader
        initImageLoader();
        //创建项目文件夹
        CreateFile();
        //初始化ShareSDK
//        ShareSDK.initSDK(this);
        //初始化百度定位
        initLocationClient();


        //初始化环信
        initEM();


        JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);// 初始化 JPush
    }

    private void initBugly() {
        Context context = getApplicationContext();
        // 获取当前包名
        String packageName = context.getPackageName();
        // 获取当前进程名
        String processName = getProcessName(android.os.Process.myPid());
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        // 初始化Bugly
        CrashReport.initCrashReport(context, "d739897806", false, strategy);
    }

    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    private void initEM() {
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        //SDK 中自动登录属性默认是 true 打开的，
        // 如果不需要自动登录，在初始化 SDK 初始化的时候，
        // 调用options.setAutoLogin(false);设置为 false 关闭。
        options.setAutoLogin(false);

        //初始化
        EMClient.getInstance().init(this, options);
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(false);


        IntentFilter callFilter = new IntentFilter(EMClient.getInstance().callManager().getIncomingCallBroadcastAction());
        registerReceiver(new CallReceiver(), callFilter);


        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        // 如果app启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的app会在以包名为默认的process name下运行，
        // 如果查到的process name不是app的process name就立即返回
        if (processAppName == null || !processAppName.equalsIgnoreCase("com.ourslook.zuoyeba")) {
            Log.e(TAG, "enter the service process!");
            //"com.easemob.chatuidemo"为demo的包名，换到自己项目中要改成自己包名

            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }
    }

    private void initLocationClient() {
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 20000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(false);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        locationClient = new LocationClient(this, option);
        locationClient.registerLocationListener(this);
        locationClient.start();
    }

    private void initImageLoader() {
        // Create default options which will be used for every
        // displayImage(...) call if no options will be passed to this method
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.image_temp_pic) // resource
                .showImageForEmptyUri(R.drawable.image_temp_pic) // resource or
                // drawable
                .showImageOnFail(R.drawable.image_temp_pic) // resource or
//                         drawable
                .displayer(new FadeInBitmapDisplayer(200, true, false, false)).cacheInMemory(true).cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .memoryCacheExtraOptions(widthPixels, heightPixels) // default
                .diskCacheExtraOptions(widthPixels, heightPixels, null).defaultDisplayImageOptions(defaultOptions)
                .build();

        ImageLoader.getInstance().init(config); // Do it on Application start
        imageLoader = ImageLoader.getInstance();
//     Then later, when you want to display image
//     DiningMasterApplication.imageLoader.displayImage( String imageUrl,
//     ImageView imageView);

    }

    private void CreateFile() {
        //SDCard路径
        final String SdcardPath =
                Environment.getExternalStorageDirectory().getAbsolutePath();
        //项目文件夹
        final String RootPath = SdcardPath + Constants.ROOT_PATH;
        //image文件夹
        final String ImagePath = SdcardPath + Constants.IMAGE_PATH;
        //download文件夹
        final String DownloadPath = SdcardPath + Constants.DOWNLOAD_PATH;

        File dir = new File(RootPath);

        if (!dir.exists()) {
            dir.mkdirs();
        }
        File image = new File(ImagePath);
        if (!image.exists()) {
            image.mkdirs();
        }
        File download = new File(DownloadPath);

        if (!download.exists()) {
            download.mkdirs();
        }
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
//        locationClient.stop(); //只定位1次
        //Receive Location
        StringBuffer sb = new StringBuffer(256);
        sb.append("time : ");
        sb.append(bdLocation.getTime());
        sb.append("\nerror code : ");
        sb.append(bdLocation.getLocType());
        sb.append("\nlatitude : ");
        sb.append(bdLocation.getLatitude());
        sb.append("\nlongitude : ");
        sb.append(bdLocation.getLongitude());
        sb.append("\nradius : ");
        sb.append(bdLocation.getRadius());


        if (bdLocation.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
            sb.append("\nspeed : ");
            sb.append(bdLocation.getSpeed());// 单位：公里每小时
            sb.append("\nsatellite : ");
            sb.append(bdLocation.getSatelliteNumber());
            sb.append("\nheight : ");
            sb.append(bdLocation.getAltitude());// 单位：米
            sb.append("\ndirection : ");
            sb.append(bdLocation.getDirection());// 单位度
            sb.append("\naddr : ");
            sb.append(bdLocation.getAddrStr());
            sb.append("\ndescribe : ");
            sb.append("gps定位成功");

            AppConfig.latitude = bdLocation.getLatitude();
            AppConfig.longitude = bdLocation.getLongitude();
            AppConfig.city = bdLocation.getCity();

        } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
            sb.append("\naddr : ");
            sb.append(bdLocation.getAddrStr());
            //运营商信息
            sb.append("\noperationers : ");
            sb.append(bdLocation.getOperators());
            sb.append("\ndescribe : ");
            sb.append("网络定位成功");

            AppConfig.latitude = bdLocation.getLatitude();
            AppConfig.longitude = bdLocation.getLongitude();
            AppConfig.city = bdLocation.getCity();
        } else if (bdLocation.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
            sb.append("\ndescribe : ");
            sb.append("离线定位成功，离线定位结果也是有效的");
            AppConfig.latitude = bdLocation.getLatitude();
            AppConfig.longitude = bdLocation.getLongitude();
            AppConfig.city = bdLocation.getCity();
        } else if (bdLocation.getLocType() == BDLocation.TypeServerError) {
            sb.append("\ndescribe : ");
            sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
        } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkException) {
            sb.append("\ndescribe : ");
            sb.append("网络不同导致定位失败，请检查网络是否通畅");
        } else if (bdLocation.getLocType() == BDLocation.TypeCriteriaException) {
            sb.append("\ndescribe : ");
            sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
        }
        sb.append("\nlocationdescribe : ");
        sb.append(bdLocation.getLocationDescribe());// 位置语义化信息
        List<Poi> list = bdLocation.getPoiList();// POI数据
        if (list != null) {
            sb.append("\npoilist size = : ");
            sb.append(list.size());
            for (Poi p : list) {
                sb.append("\npoi= : ");
                sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
            }
        }
        Log.d("BaiduLocationApiDem", sb.toString());
    }

    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }
}
