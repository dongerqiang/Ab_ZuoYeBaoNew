package com.ourslook.zuoyeba.activity.login;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
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
import com.ourslook.zuoyeba.activity.QuanActivity;
import com.ourslook.zuoyeba.model.AccMemberModel;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.PreferencesManager;
import com.ourslook.zuoyeba.utils.ToastUtil;
import com.ourslook.zuoyeba.utils.VerificationUtil;
import com.ourslook.zuoyeba.view.dialog.LoadingDialog;
import com.ourslook.zuoyeba.view.dialog.RegisteDialog;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * 注册界面
 * Created by DuanLu on 2016/5/18.
 */
public class RegisterActivity extends BaseActivity implements CompoundButton.OnClickListener {
    @Bind(R.id.iv_register_back)
    ImageView mIvBack;//返回
    @Bind(R.id.edt_register_phoneNumber)
    EditText mEdtPhoneNumber;//手机号
    @Bind(R.id.edt_register_code)
    EditText mEdtVCode;//验证码
    @Bind(R.id.tv_register_getCode)
    TextView mTvGetVCode;//获取验证码
    @Bind(R.id.edt_register_pwd)
    EditText mEdtPasswordd;//密码
    @Bind(R.id.iv_register_agree)
    ImageView mIvAgree;//同意
    @Bind(R.id.tv_register_userAgreement)
    TextView mTvUserAgreement;//用户许可协议
    @Bind(R.id.tv_register_submit)
    TextView mTvSubmit;//提交

    private static final int TOTAL_TIME = 120000;//总延迟时间
    boolean isSent;//是否发送验证码
    boolean isCheck = true;// 是否勾选
    private RegisteDialog registeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    @Override
    protected void initView() {
        mTvUserAgreement.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        setOnClickListeners(this, mIvBack, mTvGetVCode, mIvAgree, mTvUserAgreement, mTvSubmit);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_register_back:
                finish();
                break;
            case R.id.tv_register_getCode://获取验证码
                if (TextUtils.isEmpty(mEdtPhoneNumber.getText().toString())) {
                    ToastUtil.showToastDefault(mContext, "请输入手机号码");
                } else if (!VerificationUtil.checkMobile(mEdtPhoneNumber.getText().toString())) {
                    ToastUtil.showToastDefault(mContext, "手机号码格式不正确");
                } else {
                    sendVCode();
                }
                break;
            case R.id.iv_register_agree:
                if (isCheck) {
                    isCheck = !isCheck;
                    mIvAgree.setImageResource(R.drawable.check2);
                } else {
                    isCheck = !isCheck;
                    mIvAgree.setImageResource(R.drawable.check);
                }
                break;
            case R.id.tv_register_userAgreement://用户许可协议
                openActivity(UserAgreementActivity.class);
                break;
            case R.id.tv_register_submit://提交
                if (TextUtils.isEmpty(mEdtPhoneNumber.getText().toString().trim())) {
                    ToastUtil.showToastOnce(mContext, "手机号不能为空");
                } else if (!VerificationUtil.checkMobile(mEdtPhoneNumber.getText().toString())) {
                    ToastUtil.showToastDefault(mContext, "手机号码格式不正确");
                } else if (TextUtils.isEmpty(mEdtVCode.getText().toString().trim())) {
                    ToastUtil.showToastDefault(mContext, "验证码不能为空");
                } else if (TextUtils.isEmpty(mEdtPasswordd.getText().toString().trim())) {
                    ToastUtil.showToastDefault(mContext, "密码不能为空");
                } else if (mEdtPasswordd.getText().toString().trim().length() < 6) {
                    ToastUtil.showToastDefault(mContext, "密码不能小于6位");
                } else if (!isCheck) {
                    ToastUtil.showToastDefault(mContext, "请勾选用户许可协议");
                } else {
                    submitRegister();
                }
                break;
        }
    }

    //发送验证码接口
    private void sendVCode() {
        LoadingDialog.showLoadingDialog(this);
        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile", mEdtPhoneNumber.getText().toString());// 手机号
        params.put("type", "1");
        EasyHttp.doPost(mContext, ServerURL.GETMOBILECODE, params, null, null, false, new EasyHttp.OnEasyHttpDoneListener<Object>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<Object> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                getVCodeDelay(TOTAL_TIME, 1000);
                mEdtPhoneNumber.setEnabled(false);
                mTvGetVCode.setClickable(false);
                mTvGetVCode.setSelected(true);
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastOnce(mContext, message);
            }
        });
    }

    /**
     * 提交注册接口
     */
    private void submitRegister() {
        LoadingDialog.showLoadingDialog(mContext);
        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile", mEdtPhoneNumber.getText().toString());// 手机号
        params.put("pwd", mEdtPasswordd.getText().toString());//密码
        params.put("code", mEdtVCode.getText().toString());//验证码

        EasyHttp.doPost(mContext, ServerURL.REGISTER, params, null, null, false, new EasyHttp.OnEasyHttpDoneListener<Object>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<Object> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastOnce(mContext, "注册成功");
                // STOPSHIP: 16/6/21 改成跳转至登录
                login();
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastOnce(mContext, message);
            }
        });
    }

    /**
     * 验证码倒计时
     *
     * @param time    总延迟时间
     * @param picTime 延迟时间
     */
    private void getVCodeDelay(int time, int picTime) {
        CountDownTimer timer = new CountDownTimer(time, picTime) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTvGetVCode.setText(millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                Log.d("RegisterActivity", "onFinish=");
                mTvGetVCode.setSelected(false);
                mTvGetVCode.setText("获取验证码");
                mEdtPhoneNumber.setEnabled(true);
                mEdtVCode.setText("");
                mTvGetVCode.setClickable(true);
            }
        }.start();
    }


    private void login() {

        Map<String, String> params = new HashMap<>();
        params.put("mobile", mEdtPhoneNumber.getText().toString());// 电话
        params.put("pwd", mEdtPasswordd.getText().toString());//密码

        EasyHttp.doPost(mContext, ServerURL.LOGIN, params, null, AccMemberModel.class, false, new EasyHttp.OnEasyHttpDoneListener<AccMemberModel>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<AccMemberModel> resultBean) {
                AppConfig.isLogin = true;
                if (resultBean.data != null) {
                    AppConfig.userVo = resultBean.data;
                    AppConfig.token = resultBean.data.token;
                    AppConfig.imAcc = resultBean.data.imacc;
                    AppConfig.imPWd = resultBean.data.impwd;
                    // 此处需要记录账号以及密码
                    PreferencesManager.getInstance(mContext).put(Constants.ACCOUNT, mEdtPhoneNumber.getText().toString());
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

                    // deq change 2017/2/18

                    if(registeDialog == null){
                        registeDialog = new RegisteDialog(mContext, R.style.FullHeightDialog, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                openActivity(QuanActivity.class);
                            }

                        });

                    }
                    registeDialog.setCanceledOnTouchOutside(false);
                    registeDialog.setCancelable(false);
                    registeDialog.show();


                }
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
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
}
