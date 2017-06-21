package com.ourslook.zuoyeba.activity.login;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.chat.EMClient;
import com.ourslook.zuoyeba.AppConfig;
import com.ourslook.zuoyeba.AppManager;
import com.ourslook.zuoyeba.BaseActivity;
import com.ourslook.zuoyeba.Constants;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ServerURL;
import com.ourslook.zuoyeba.activity.ChoiceWorkActivity;
import com.ourslook.zuoyeba.activity.HomeActivity;
import com.ourslook.zuoyeba.model.AccMemberModel;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.PreferencesManager;
import com.ourslook.zuoyeba.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * 加载页
 * Created by DuanLu on 2016/7/6.
 */
public class LoadingActivity extends BaseActivity {
    private static int WHAT_1 = 1;
    private static int DELAY_TIME = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Translucent navigation bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    @Override
    protected void initView() {
        mHandler.sendEmptyMessageDelayed(WHAT_1, DELAY_TIME);
    }

    @Override
    protected void baseHandleMessage(Message msg) {
        super.baseHandleMessage(msg);
        //判断 是否进入引导页,引导页放出来后就把下面的代码放开
        Intent intent = new Intent();
        if (PreferencesManager.getInstance(this).get(Constants.LEAD_KEY, true)) {
            //加载引导页
            intent.setClass(this, LeadActivity.class);
            startActivity(intent);
            finish();
        } else {
            if (checkAccountAndPassword()) {
                //先要使用这个密码进行登录  然后进入HomeActivity
                getLoginHttp();
            } else {
                boolean isStudent = PreferencesManager.getInstance(this).get(Constants.IS_STUDENT, false);
                boolean isTeacher = PreferencesManager.getInstance(this).get(Constants.IS_TEACHER, false);
                if(!isStudent && !isTeacher){
                    intent.setClass(mContext, ChoiceWorkActivity.class);
                }else{
                    intent.setClass(mContext, HomeActivity.class);
                }
                startActivity(intent);

                finish();
            }
        }


    }

    /**
     * 请求登录接口
     */
    private void getLoginHttp() {
        //获取share中存储的用户的信息
        PreferencesManager pm = PreferencesManager.getInstance(this);
        final String account = pm.get(Constants.ACCOUNT, "");
        final String password = pm.get(Constants.PASSWORD, "");
        Map<String, String> params = new HashMap<>();
        params.put("mobile", account);
        params.put("pwd", password);
        EasyHttp.doPost(mContext, ServerURL.LOGIN, params, null, AccMemberModel.class, false, new EasyHttp.OnEasyHttpDoneListener<AccMemberModel>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<AccMemberModel> resultBean) {
                AppConfig.isLogin = true;
                if (resultBean.data != null) {
                    AppConfig.userVo = resultBean.data;
                    AppConfig.token = resultBean.data.token;
                    AppConfig.imAcc = resultBean.data.imacc;
                    AppConfig.imPWd = resultBean.data.impwd;
                    //登录环信
                    loginEM();
                    //TODO 还要上传坐标,

                    //设置推送别名
                    JPushInterface.setAlias(mContext, resultBean.data.mobile, new TagAliasCallback() {
                        @Override
                        public void gotResult(int i, String s, Set<String> set) {
                            Log.d("--", i == 0 ? "别名设置成功" : "推送注册失败");
                            logForDebug(s);
                        }
                    });
                    if (JPushInterface.isPushStopped(mContext)) {
                        JPushInterface.resumePush(mContext);
                    }
                    openActivity(HomeActivity.class);
                    finish();
                }
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                ToastUtil.showToastDefault(mContext, message + "-----");
                //如果登陆失败，就用游客身份登陆
                AppConfig.isLogin = false;
                //游客身份进入HomeActivity
                openActivity(HomeActivity.class);
                finish();
            }
        });
    }

    /**
     * 判断之前是否登录过
     *
     * @return 登陆过返回true  没登陆过  返回 false
     */
    private boolean checkAccountAndPassword() {
        PreferencesManager pm = PreferencesManager.getInstance(this);
        String password = pm.get(Constants.PASSWORD, "");
        String account = pm.get(Constants.ACCOUNT, "");

        if (password.equals("") || account.equals("")) {
            return false;
        } else {
            return true;
        }
    }

    private void loginEM() {
        EMClient.getInstance().login(AppConfig.imAcc, AppConfig.imPWd, new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        EMClient.getInstance().groupManager().loadAllGroups();
                        EMClient.getInstance().chatManager().loadAllConversations();
                        Log.d("loginEMC---success", "登陆聊天服务器成功！");
                        EMClient.getInstance().addConnectionListener(new MyConnectionListener());
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(int code, String message) {
                Log.d("loginEMC---error", "登陆聊天服务器失败！");
            }
        });
    }

    /**
     * 环信登录掉线重新登录
     */
    private class MyConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
            //已连接到服务器
        }

        @Override
        public void onDisconnected(final int error) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Log.e("环信连接监听", "环信错误连接错误-->" + error);
                    if (error == 206) {
                        ToastUtil.showToastOnce(mContext, "您的账号在其它地方登录!");
                        //退出极光推送
                        //设置推送别名
                        JPushInterface.setAlias(mContext, "", new TagAliasCallback() {
                            @Override
                            public void gotResult(int i, String s, Set<String> set) {
                                logForDebug(s);
                            }
                        });
                        AppConfig.isLogin = false;
                        AppConfig.userVo = null;
                        AppConfig.token = "";
                        AppConfig.iscreditvalid = false;
                        Intent intentLogin = new Intent(mContext, LoginActivity.class);
                        intentLogin.putExtra("source", 1);
                        startActivity(intentLogin);
                        AppManager.getInstance().finishOtherActivity(LoginActivity.class);
                    }
                }
            });
        }
    }

}
