package com.ourslook.zuoyeba.utils;

import android.text.TextUtils;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/18.
 */
public class StringUtils {
    /**
     * 字符串判空
     * @param mTvTextView
     * @param content
     */
    public static void setTextNotEmpty(TextView mTvTextView, String content) {
        if(content==null){
            return;
        }
        if (!TextUtils.isEmpty(content)) {
            mTvTextView.setText(content);
        }
    }

    /**
     * 判断对象是否为NotEmpty(!null或元素>0)<br>
     * 实用于对如下对象做判断:String Collection及其子类 Map及其子类
     *
     * @param pObj
     *            待检查对象
     * @return boolean 返回的布尔值
     */
    @SuppressWarnings("rawtypes")
    public static boolean isNotEmpty(Object pObj) {
        if (pObj == null)
            return false;
        if (pObj.equals(""))
            return false;
        if (pObj instanceof String) {
            if (((String) pObj).length() == 0) {
                return false;
            }
        } else if (pObj instanceof Collection) {
            if (((Collection) pObj).size() == 0) {
                return false;
            }
        } else if (pObj instanceof Map) {
            if (((Map) pObj).size() == 0) {
                return false;
            }
        }
        return true;
    }


    /**
     * 判断对象是否Empty(null或元素为0)<br>
     * 实用于对如下对象做判断:String Collection及其子类 Map及其子类
     *
     * @param pObj
     *            待检查对象
     * @return boolean 返回的布尔值
     */
    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(Object pObj) {
        if (pObj == null)
            return true;
        if (pObj.equals(""))
            return true;
        if (pObj instanceof String) {
            if (((String) pObj).length() == 0) {
                return true;
            }
        } else if (pObj instanceof Collection) {
            if (((Collection) pObj).size() == 0) {
                return true;
            }
        } else if (pObj instanceof Map) {
            if (((Map) pObj).size() == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 格式化金额.
     *
     * @param value
     *            the value
     * @return the string
     */
    public static String formatCurrency2String(Object value) {
        if (value == null || "0".equals(value.toString())) {
            return "0.00";
        }
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(value);
    }

}
