package com.ourslook.zuoyeba.net;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ourslook.zuoyeba.AppConfig;
import com.ourslook.zuoyeba.ServerURL;
import com.ourslook.zuoyeba.model.CommonUploadModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * 简易的网络请求框架封装
 * <p/>
 * Created by wangyu on 2015/8/17.
 */
public class EasyHttp {

    private static final String TAG = "EasyHttp";
    private static final int timeOutMills = 5000;
    private static final int retryTimes = 0;

    private static AsyncHttpClient httpClient;


    /**
     * post方式请求接口
     *
     * @param <T>      实体泛型，可以为null
     * @param context  上下文对象
     * @param url      接口地址
     * @param params   接口参数，可以为null
     * @param files
     * @param clazz    实体类型，可以为null
     * @param isList   返回类型是否为List
     * @param listener 回调，onSuccess中获取实体时，需强制转型
     */
    @SuppressWarnings("unchecked")
    public static <T> void doPost(Context context, String url, Map<String, String> params, Map<String, File> files, Class clazz, boolean isList, OnEasyHttpDoneListener<T> listener) {
        if (httpClient == null) {
            httpClient = new AsyncHttpClient();
            httpClient.setMaxRetriesAndTimeout(retryTimes, timeOutMills);
        }
        Log.d(TAG, "post--------->" + url);
        //通常方式请求参数
        RequestParams requestParams = new RequestParams();
        if (params != null) {
            for (String key : params.keySet()) {
                requestParams.put(key, params.get(key));

            }
        }

        if (files != null) {
            for (String key : files.keySet()) {
                try {
                    requestParams.put(key, files.get(key));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
            }
        }


        Log.d(TAG, "params--------->" + requestParams.toString());


        if (isList) {
            httpClient.post(context, url, requestParams, new HandlerForList(listener, clazz));
        } else {
            httpClient.post(context, url, requestParams, new HandlerForObject(listener, clazz));
        }
    }

    /**
     * get 方式请求接口
     *
     * @param context  上下文对象
     * @param url      接口地址
     * @param params   接口参数，可以为null
     * @param clazz    实体类型，可以为null
     * @param isList   返回类型是否为List
     * @param listener 回调，onSuccess中获取实体时，需强制转型
     */
    @SuppressWarnings("unchecked")
    public static <T> void doGet(Context context, String url, Map<String, String> params, Class clazz, boolean isList, OnEasyHttpDoneListener<T> listener) {
        if (httpClient == null) {
            httpClient = new AsyncHttpClient();
            httpClient.setTimeout(timeOutMills);
        }

        Log.d(TAG, "get--------->" + url);
        RequestParams requestParams = new RequestParams();
        if (params != null) {
            for (String key : params.keySet()) {
                requestParams.put(key, params.get(key));
            }
        }


        Log.d(TAG, params + "------>" + requestParams.toString());


        if (isList) {
            httpClient.get(context, url, requestParams, new HandlerForList(listener, clazz));
        } else {
            httpClient.get(context, url, requestParams, new HandlerForObject(listener, clazz));
        }
    }

    private static class HandlerForObject<T> extends BaseJsonHttpResponseHandler<ResultBean<T>> {

        private Class clazz;
        private OnEasyHttpDoneListener<T> listener;

        public HandlerForObject(OnEasyHttpDoneListener<T> listener, Class clazz) {
            this.clazz = clazz;
            this.listener = listener;
        }


        @Override
        public void onSuccess(int i, Header[] headers, String s, ResultBean<T> result) {
            if (!result.rspCode.equals("0000")) {
                listener.onEasyHttpFailure(result.rspCode, result.rspDesc);
            } else {
                listener.onEasyHttpSuccess(result);
            }
        }

        @Override
        public void onFailure(int i, Header[] headers, Throwable throwable, String s, ResultBean<T> result) {
            String message = getFriendlyMessage(i, throwable);
            Log.e(TAG, message);
            listener.onEasyHttpFailure(i + "", message);
        }


        @SuppressWarnings("unchecked")
        @Override
        protected ResultBean<T> parseResponse(String data, boolean b) throws Throwable {
            ResultBean<T> result;
            Log.i(TAG, data);

            if (TextUtils.isEmpty(data) || !data.startsWith("{")) {
                throw new Exception(TAG + ":服务器错误");
            }

            try {
                if (clazz == null) {
                    result = JsonUtil.toJavaBeanResult(data);
                } else {
                    result = JsonUtil.toJavaBean(data, clazz);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception(TAG + ":json转换错误", e);
            }
            return result;
        }


    }


    private static class HandlerForList<T> extends BaseJsonHttpResponseHandler<ResultBean<List<T>>> {

        private Class clazz;
        private OnEasyHttpDoneListener<List<T>> listener;

        public HandlerForList(OnEasyHttpDoneListener<List<T>> listener, Class clazz) {
            this.clazz = clazz;
            this.listener = listener;
        }

        @Override
        public void onSuccess(int i, Header[] headers, String s, ResultBean<List<T>> result) {
            if (!result.rspCode.equals("0000")) {
                listener.onEasyHttpFailure(result.rspCode, result.rspDesc);
            } else {
                listener.onEasyHttpSuccess(result);
            }
        }

        @Override
        public void onFailure(int i, Header[] headers, Throwable throwable, String s, ResultBean<List<T>> result) {
            String message = getFriendlyMessage(i, throwable);
            Log.e(TAG, message);
            listener.onEasyHttpFailure(i + "", message);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected ResultBean<List<T>> parseResponse(String data, boolean b) throws Throwable {
            ResultBean<List<T>> result;

            Log.i(TAG, data);

            if (TextUtils.isEmpty(data) || !data.startsWith("{")) {
                throw new Exception(TAG + ":服务器错误");
            }

            try {
                if (clazz == null) {
                    result = JsonUtil.toJavaBeanListResult(data);
                } else {
                    result = JsonUtil.toJavaBeanList(data, clazz);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception(TAG + ":json转换错误", e);
            }
            return result;
        }

    }

    /**
     * 打断并结束所有正在执行的网络请求
     */
    public static void cancelAllRequest() {
        if (httpClient != null) {
            httpClient.cancelAllRequests(true);
        }
    }

    /**
     * 打断并结束于此context绑定的网络请求
     *
     * @param context
     */
    public static void cancelRequest(Context context) {
        if (httpClient != null && context != null) {
            httpClient.cancelRequests(context, true);
        }
    }


    private static String getFriendlyMessage(int statusCode, Throwable throwable) {

        //连不上服务器
        if (statusCode == 0) {

            //没有异常消息
            if (throwable == null) {
                return "未知的网络错误，请检查网络。错误代码：000";
            }
            //没有错误消息
            if (throwable.getMessage() == null) {
                return "未知的网络异常，请检查网络。错误代码：001";
            }

            if (throwable.getMessage().contains("INTERNET") || throwable.getMessage().contains("EACCES")) {
                return "没有添加网络权限。错误代码：010";
            }

            //没有错误原因
            if (throwable.getCause() == null) {
                return "未知的网络错误，请检查网络。错误代码：003";
            }

            //网络异常
            if (throwable.getCause().getClass().isInstance(SocketTimeoutException.class)) {
                return "网络异常，请检查网络。错误代码：011";
            }

            if (throwable.getCause().getMessage() == null) {
                return "未知的网络错误，请检查网络。错误代码：004";
            }

            //连接异常，通常为没有网络
            if (throwable.getCause().getMessage().contains("ENETUNREACH")) {
                return "网络异常，请检查网络。错误代码：012";
            }
            //连接异常，通常为后台服务没有启动
            if (throwable.getCause().getMessage().contains("ECONNREFUSED")) {
                return "服务异常，请联系管理员。错误代码：013";
            }

            //连接超时
            if (throwable.getCause().getMessage().contains("timed out")) {
                return "连接超时，请稍后再试。错误代码：014";
            }


        }

        if (statusCode == 200) {
            if (throwable.getMessage() == null) {
                return "请求成功，但解析异常。错误代码：015";
            }

            if (throwable.getMessage().contains("json")) {
                return "json转换异常。错误代码：016";
            }

            return throwable.getMessage();
        }

        //找不到资源，通常为URL地址错误
        if (statusCode == 404) {
            return "地址错误,。错误代码：017";
        }
        //服务器异常，通常是服务器处理请求过程产生了未经处理的异常
        if (statusCode > 499) {
            return "服务器异常，请联系管理员。错误代码：018";
        }

        return "未知错误。错误代码：" + statusCode;
    }

    /**
     * 网络请求回调
     * 成功or失败
     */
    public interface OnEasyHttpDoneListener<T> {

        void onEasyHttpSuccess(ResultBean<T> resultBean);

        void onEasyHttpFailure(String code, String message);
    }


    /******************************作业宝专用***********************************/
    /**
     * 作业宝上传图片
     *
     * @param context
     * @param type
     * @param file
     * @param listener
     */
    public static void zuoyebaUpLoadPicture(Context context, int type, File file, OnEasyHttpDoneListener<CommonUploadModel> listener) {
        if (httpClient == null) {
            httpClient = new AsyncHttpClient();
            httpClient.setMaxRetriesAndTimeout(retryTimes, timeOutMills);
        }

        String url = ServerURL.UPLOADIMAGE + "?type=" + type + "&token=" + AppConfig.token;
        Log.d(TAG, "post--------->" + url);
        //通常方式请求参数
        RequestParams requestParams = new RequestParams();

        if (file != null) {
            try {
                requestParams.put("imageFile", file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }
        }


        Log.d(TAG, "params--------->type=" + type + "&token=" + AppConfig.token + requestParams.toString());


        httpClient.post(context, url, requestParams, new HandlerForObject(listener, CommonUploadModel.class));
    }


}