package com.ourslook.zuoyeba.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

/**
 * Created by wy on 2015/11/25.
 */
public class PackageUtils {

    private static final String TAG = "PackageUtils";

    public static int getAndroidSDKVersion() {
        return Build.VERSION.SDK_INT;
    }


    public static boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    Log.i("后台", appProcess.processName);
                    return true;
                } else {
                    Log.i("前台", appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }

    public static String getVersionName(Context context){
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getVersionCode(Context context){
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将版本名转换为int
     * @param versionName
     * @return
     */
    public static int dealVersionlName(String versionName) {
        int version = 0;
        int currentindex =1;
        //r1.1.8
        try {
            if(!TextUtils.isEmpty(versionName)){
                String substring = versionName.substring(1,versionName.length());
                String[] split = substring.split("\\.");
                for(int i =0; i<split.length;i++){
                    if(i ==0){
                        currentindex = 1000;
                    }
                    if(i ==1){
                        currentindex = 100;
                    }
                    if(i == 2){
                        currentindex = 10;
                    }
                    version += (Integer.parseInt(split[i]) *currentindex);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.d("deq","version = "+version);
        return version;
    }
}
