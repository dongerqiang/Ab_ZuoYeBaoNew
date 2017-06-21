package com.ourslook.zuoyeba.activity.login;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ourslook.zuoyeba.BaseActivity;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ServerURL;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.ToastUtil;
import com.ourslook.zuoyeba.utils.VerificationUtil;
import com.ourslook.zuoyeba.view.dialog.LoadingDialog;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

/**
 * 忘记密码界面
 * Created by DuanLu on 2016/5/18.
 */
public class ForgetPwdActivity extends BaseActivity implements CompoundButton.OnClickListener {
    @Bind(R.id.iv_forgetPwd_back)
    ImageView mIvBack;//返回
    @Bind(R.id.edt_forgetPwd_pNumber)
    EditText mEdtPhoneNumber;//手机号
    @Bind(R.id.edt_forgetPwd_code)
    EditText mEdtVCode;//验证码
    @Bind(R.id.tv_forgetPwd_getCode)
    TextView mTvGetVCode;//获取验证码
    @Bind(R.id.edt_forgetPwd_pwd)
    EditText mEdtPassWord;//密码
    @Bind(R.id.edt_forgetPwd_confimPwd)
    EditText mEdtConfimPwd;//再次确认密码
    @Bind(R.id.tv_forgetPwd_submit)
    TextView mTvSubmit;//提交

    private static final int TOTAL_TIME = 120000;//总延迟时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
    }

    @Override
    protected void initView() {
        setOnClickListeners(this, mIvBack, mTvGetVCode, mTvSubmit);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_forgetPwd_back://返回
                finish();
                break;
            case R.id.tv_forgetPwd_getCode://获取验证码
                if (TextUtils.isEmpty(mEdtPhoneNumber.getText().toString())) {
                    ToastUtil.showToastDefault(mContext, "请输入手机号码");
                } else if (!VerificationUtil.checkMobile(mEdtPhoneNumber.getText().toString())) {
                    ToastUtil.showToastDefault(mContext, "手机号码格式不正确");
                } else {
                    sendVCode();
                }
                break;
            case R.id.tv_forgetPwd_submit://提交
                if (TextUtils.isEmpty(mEdtPhoneNumber.getText().toString().trim())) {
                    ToastUtil.showToastOnce(mContext, "手机号不能为空");
                } else if (!VerificationUtil.checkMobile(mEdtPhoneNumber.getText().toString().trim())) {
                    ToastUtil.showToastOnce(mContext, "手机号格式不正确");
                } else if (TextUtils.isEmpty(mEdtVCode.getText().toString().trim())) {
                    ToastUtil.showToastOnce(mContext, "验证码不能为空");
                } else if (TextUtils.isEmpty(mEdtPassWord.getText().toString().trim())) {
                    ToastUtil.showToastOnce(mContext, "密码不能为空");
                } else if (mEdtPassWord.getText().toString().trim().length() < 6) {
                    ToastUtil.showToastOnce(mContext, "密码不能小于6位");
                } else if (TextUtils.isEmpty(mEdtConfimPwd.getText().toString().trim())) {
                    ToastUtil.showToastOnce(mContext, "请再次输入密码");
                } else if (!(mEdtPassWord.getText().toString().trim()).equals(mEdtConfimPwd.getText().toString().trim())) {
                    ToastUtil.showToastOnce(mContext, "两次密码不一致");
                } else {
                    forgetPwd();
                }
                break;
        }
    }

    private void forgetPwd() {
        LoadingDialog.showLoadingDialog(mContext);
        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile", mEdtPhoneNumber.getText().toString().trim());// 手机号
        params.put("pwd", mEdtPassWord.getText().toString().trim());//密码
        params.put("code", mEdtVCode.getText().toString().trim());//验证码
        EasyHttp.doPost(mContext, ServerURL.FORGETPWD, params, null, null, false, new EasyHttp.OnEasyHttpDoneListener<Object>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<Object> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastOnce(mContext, "找回密码成功");
                finish();
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastOnce(mContext, message);
            }
        });
    }

    //发送验证码接口
    private void sendVCode() {
        LoadingDialog.showLoadingDialog(this);
        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile", mEdtPhoneNumber.getText().toString());// 手机号
        params.put("type", "2");
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

}
