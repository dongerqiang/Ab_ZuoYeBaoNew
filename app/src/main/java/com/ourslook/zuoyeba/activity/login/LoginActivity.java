package com.ourslook.zuoyeba.activity.login;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.ourslook.zuoyeba.utils.VerificationUtil;
import com.ourslook.zuoyeba.view.dialog.LoadingDialog;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * 登录
 * Created by wangyu on 16/5/16.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.iv_loginAty_back)
    ImageView mIvBack;//返回
    @Bind(R.id.et_loginAty_pwd)
    EditText etLoginAtyPwd;
    @Bind(R.id.tv_loginAty_forgetPwd)
    TextView tvLoginAtyForgetPwd;
    @Bind(R.id.et_loginAty_pNum)
    EditText etLoginAtyPNum;
    @Bind(R.id.tv_loginAty_ok)
    TextView tvLoginAtyOk;
    @Bind(R.id.tv_loginAty_register)
    TextView tvLoginAtyRegister;

    int source;//来源:1、退出登录
    long timeStamp;
    private int fromChoice; // l来源：22 ChoiceWorkActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
        tvLoginAtyForgetPwd.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvLoginAtyRegister.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        setOnClickListeners(this, mIvBack, tvLoginAtyOk, tvLoginAtyForgetPwd, tvLoginAtyRegister);
        //读取Share  如果有值则读取
        String account = PreferencesManager.getInstance(mContext).get(Constants.ACCOUNT);
        if (account != null) {
            etLoginAtyPNum.setText(account);
            etLoginAtyPNum.setSelection(account.length());
        }
        source = getIntent().getIntExtra("source", 0);
        fromChoice = getIntent().getIntExtra("choice_login", 0);
        if (source == 1) {
            mIvBack.setVisibility(View.GONE);
            AppManager.getInstance().finishOtherActivity(LoginActivity.class);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_loginAty_back:
                finish();
                break;
            case R.id.tv_loginAty_ok:
                login();
                break;
            case R.id.tv_loginAty_forgetPwd:
                openActivity(ForgetPwdActivity.class);
                break;

            case R.id.tv_loginAty_register:
                openActivity(RegisterActivity.class);
                break;
        }
    }

    private void login() {
        if (!VerificationUtil.checkMobile(etLoginAtyPNum.getText().toString())) {
            ToastUtil.showToastOnce(mContext, "请输入正确的手机号");
            return;
        }
        if (etLoginAtyPwd.getText().length() < 6) {
            ToastUtil.showToastOnce(mContext, "密码不能小于6位");
            return;
        }
        if (etLoginAtyPwd.getText().toString().contains(" ")) {
            ToastUtil.showToastOnce(mContext, "密码不能包含空格");
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("mobile", etLoginAtyPNum.getText().toString());// 电话
        params.put("pwd", etLoginAtyPwd.getText().toString());//密码
        LoadingDialog.showLoadingDialog(mContext);
        EasyHttp.doPost(mContext, ServerURL.LOGIN, params, null, AccMemberModel.class, false, new EasyHttp.OnEasyHttpDoneListener<AccMemberModel>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<AccMemberModel> resultBean) {
                if (mContext != null) {
                    LoadingDialog.dismissLoadingDialog();
                }
                AppConfig.isLogin = true;
                if (resultBean.data != null) {
                    AppConfig.userVo = resultBean.data;
                    AppConfig.token = resultBean.data.token;
                    AppConfig.imAcc = resultBean.data.imacc;
                    AppConfig.imPWd = resultBean.data.impwd;
                    // 此处需要记录账号以及密码
                    PreferencesManager.getInstance(mContext).put(Constants.ACCOUNT, etLoginAtyPNum.getText().toString());
                    PreferencesManager.getInstance(mContext).put(Constants.PASSWORD, etLoginAtyPwd.getText().toString());
                    //登录环信
                    loginEM();
                    //TODO 还要上传坐标,

                    //设置推送别名
                    JPushInterface.setAlias(mContext, resultBean.data.mobile, new TagAliasCallback() {
                        @Override
                        public void gotResult(int i, String s, Set<String> set) {
                            logForDebug(s);
                        }
                    });
                    if (source == 1) {
                        openActivity(HomeActivity.class);
                    } else {
                        if(fromChoice == 22){
                            openActivity(HomeActivity.class);
                            AppManager.getInstance().finishActivity(ChoiceWorkActivity.class);
                            finish();
                        }else{
                            finish();
                        }
                    }

                }
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                if (mContext != null) {
                    LoadingDialog.dismissLoadingDialog();
                }
                ToastUtil.showToastOnce(mContext, message);
            }
        });
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


    @Override
    public void onBackPressed() {
        if (source == 1) {
            if (System.currentTimeMillis() - timeStamp > 2000) {
                ToastUtil.showToastOnce(this, "再按一次退出程序");
            } else {
                AppManager.getInstance().AppExit(this);
            }
            timeStamp = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }

}
