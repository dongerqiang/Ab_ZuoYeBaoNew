package com.ourslook.zuoyeba.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ourslook.zuoyeba.AppConfig;
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
 * Created by huangyi on 16/5/18.
 * 更改手机号
 */
public class ChangePhoneNumberActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.et_changePhoneAty_pNum)
    EditText etChangePhoneAtyPNum;
    @Bind(R.id.et_changePhoneAty_newNum)
    EditText etChangePhoneAtyNewNum;
    @Bind(R.id.et_changePhoneAty_code)
    EditText etChangePhoneAtyCode;
    @Bind(R.id.tv_changePhoneAty_getCode)
    TextView tvChangePhoneAtyGetCode;

    private boolean isSent;//是否发送验证码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithDefaultTitle(R.layout.activity_change_phone_number, "更改手机号");
    }

    @Override
    protected void initView() {
        mTvTitleRight.setText("保存");
        mTvTitleRight.setVisibility(View.VISIBLE);
        setOnClickListeners(this,mTvTitleRight,tvChangePhoneAtyGetCode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_title_right://保存
                save();
                break;
            case R.id.tv_changePhoneAty_getCode://获取验证码
                getCode();
                break;
        }
    }

    /**
     * 保存
     */
    private void save(){
        if(!isSent){
            ToastUtil.showToastDefault(mContext,"请获取验证码");
            return;
        }
        if(etChangePhoneAtyPNum.getText().toString().trim().length()==0){//
            ToastUtil.showToastDefault(mContext,"请输入原手机号");
            return;
        }
        if(!VerificationUtil.checkMobile(etChangePhoneAtyPNum.getText().toString().trim())){//验证手机号
            ToastUtil.showToastDefault(mContext,"原手机号码格式不正确");
            return;
        }
        if(etChangePhoneAtyCode.getText().toString().trim().length()==0){
            ToastUtil.showToastDefault(mContext,"请输入验证码");
            return;
        }
        LoadingDialog.showLoadingDialog(mContext);
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", AppConfig.token);
        params.put("oldmobile", etChangePhoneAtyPNum.getText().toString().trim());//
        params.put("newmobile", etChangePhoneAtyNewNum.getText().toString().trim());
        params.put("code", etChangePhoneAtyCode.getText().toString().trim());
        EasyHttp.doPost(mContext, ServerURL.UPDMOBILE, params, null, null, false, new EasyHttp.OnEasyHttpDoneListener<Object>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<Object> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastDefault(mContext,"修改成功");
                finish();
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastDefault(mContext,message);
            }
        });
    }


    /**
     * 获取验证码
     */
    private void getCode() {
        if(etChangePhoneAtyNewNum.getText().toString().trim().length()==0){//
            ToastUtil.showToastDefault(mContext,"请输入新手机号");
            return;
        }
        if(!VerificationUtil.checkMobile(etChangePhoneAtyNewNum.getText().toString().trim())){//验证手机号
            ToastUtil.showToastDefault(mContext,"手机号码格式不正确");
            return;
        }
        LoadingDialog.showLoadingDialog(mContext);
        Map<String, String> params = new HashMap();
        params.put("mobile", etChangePhoneAtyNewNum.getText().toString().trim());// 电话
        params.put("type", "1");
        EasyHttp.doPost(mContext, ServerURL.GETMOBILECODE, params, null, null, false, new EasyHttp.OnEasyHttpDoneListener<Object>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<Object> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                tvChangePhoneAtyGetCode.setClickable(false);
                tvChangePhoneAtyGetCode.setBackgroundColor(mContext.getResources().getColor(R.color.gray_160));
                delayTime(120000,1000);
                isSent=true;
                etChangePhoneAtyNewNum.setEnabled(false);
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastDefault(mContext,message);
            }
        });
    }


    /**
     * 用于 重复发送验证码的延时时间
     *
     * @param sunTime   总共时间
     * @param dealyTime
     */
    private void delayTime(long sunTime, long dealyTime) {
        CountDownTimer mTimer = new CountDownTimer(sunTime, dealyTime) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvChangePhoneAtyGetCode.setText( + millisUntilFinished / 1000 + "秒");
            }

            @Override
            public void onFinish() {
                isSent=false;
                etChangePhoneAtyNewNum.setEnabled(true);
                tvChangePhoneAtyGetCode.setClickable(true);
                tvChangePhoneAtyGetCode.setText("获取验证码");
                tvChangePhoneAtyGetCode.setBackgroundResource(R.drawable.yanzhma_orange);
            }
        }.start();

    }
}
