package com.ourslook.zuoyeba.view.dialog.common;

import android.util.Log;

/**
 * log工具
 * @author tu
 *
 */
public class NLogUtil {
	private static boolean isDebug = true;
	/**
	 * 打印信息(提示信息)
	 * @param tag
	 * @param msg
	 */
	public static void logI(String tag,String msg){
		if(isDebug){
			try {
				Log.i(tag, msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 打印信息(错误信息)
	 * @param tag
	 * @param msg
	 */
	public static void logE(String tag,String msg){
		if(isDebug){
			try {
				Log.e(tag, msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 打印信息(警告信息)
	 * @param tag
	 * @param msg
	 */
	public static void logW(String tag,String msg){
		if(isDebug){
			try {
				Log.w(tag, msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 打印信息(警告信息)
	 * @param tag
	 * @param msg
	 */
	public static void logD(String tag,String msg){
		if(isDebug){
			try {
				Log.d(tag, msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * system 输出
	 * @param s
	 */
	public static void sysOut(String s){
		if(isDebug){
			System.out.println(s);
		}
	}
	/**
	 * system 输出
	 * @param s
	 * @param TAG
	 */
	public static void sysOut(String TAG,Object s){
		if(isDebug){
			System.out.println(TAG + ":>>> " + s);
		}
	}
}
