package com.ourslook.zuoyeba.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.ourslook.zuoyeba.AppConfig;
import com.ourslook.zuoyeba.BaseActivity;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ServerURL;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by huangyi on 16/5/18.
 * 修改密码
 */
public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.et_changePwdAty_pwd)
    EditText etChangePwdAtyPwd;
    @Bind(R.id.et_changePwdAty_newPwd)
    EditText etChangePwdAtyNewPwd;
    @Bind(R.id.et_changePwdAty_confimPwd)
    EditText etChangePwdAtyConfimPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithDefaultTitle(R.layout.activity_change_password, "更改密码");
    }

    @Override
    protected void initView() {
        mTvTitleRight.setText("保存");
        mTvTitleRight.setVisibility(View.VISIBLE);
        setOnClickListeners(this,mTvTitleRight);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_title_right://保存
                savePassword();
                break;
        }
    }


    /**
     * 保存密码
     */
    private void savePassword(){
        if(etChangePwdAtyPwd.getText().toString().trim().length()==0){
            ToastUtil.showToastDefault(mContext,"请输入原密码");
        }else if(etChangePwdAtyNewPwd.getText().toString().trim().length()==0){
            ToastUtil.showToastDefault(mContext,"请输入新密码");
        }else if(etChangePwdAtyConfimPwd.getText().toString().trim().length()==0){
            ToastUtil.showToastDefault(mContext,"请确认新密码");
        }else if(!etChangePwdAtyNewPwd.getText().toString().trim().equals(etChangePwdAtyConfimPwd.getText().toString().trim())){
            ToastUtil.showToastDefault(mContext,"新密码输入不一致");
        }else{
            Map<String, String> params = new HashMap<String, String>();
            params.put("token", AppConfig.token);
            params.put("pwd", etChangePwdAtyNewPwd.getText().toString().trim());//
            params.put("oldpwd", etChangePwdAtyPwd.getText().toString().trim());
            EasyHttp.doPost(mContext, ServerURL.UPDPWD, params, null, null, false, new EasyHttp.OnEasyHttpDoneListener<Object>() {
                @Override
                public void onEasyHttpSuccess(ResultBean<Object> resultBean) {
                    ToastUtil.showToastDefault(mContext,"更改成功!");
                    finish();
                }

                @Override
                public void onEasyHttpFailure(String code, String message) {
                    ToastUtil.showToastDefault(mContext,message);
                }
            });
        }

    }
}
