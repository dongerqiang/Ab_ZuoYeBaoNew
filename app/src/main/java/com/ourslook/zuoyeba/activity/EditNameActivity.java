package com.ourslook.zuoyeba.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.ourslook.zuoyeba.AppConfig;
import com.ourslook.zuoyeba.BaseActivity;
import com.ourslook.zuoyeba.Constants;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ServerURL;
import com.ourslook.zuoyeba.model.AccMemberModel;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by huangyi on 16/5/16.
 * 编辑姓名界面
 */
public class EditNameActivity extends BaseActivity implements View.OnClickListener {
    String name;//传递过来的姓名
    @Bind(R.id.et_setNameAty_name)
    EditText etSetNameAtyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        name = getIntent().getStringExtra(Constants.PASS_NAME);
        setContentViewWithDefaultTitle(R.layout.activity_edit_name, "姓名");
    }

    @Override
    protected void initView() {
        mTvTitleRight.setVisibility(View.VISIBLE);
        mTvTitleRight.setText("保存");
        if(name!=null){
            etSetNameAtyName.setText(name);
            etSetNameAtyName.setSelection(name.length());
        }
        setOnClickListeners(this,mTvTitleRight);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_title_right:
                changeNameNet();
                break;
        }
    }


    /**
     * 调用修改姓名接口
     */
    private void changeNameNet(){
        if(etSetNameAtyName.getText().toString()==null){
            ToastUtil.showToastDefault(mContext,"请输入姓名");
            return;
        }
        else if(etSetNameAtyName.getText().toString().trim().length()==0){
            ToastUtil.showToastDefault(mContext,"请输入姓名");
            return;
        }else if(etSetNameAtyName.getText().toString().trim().equals(name)){
            ToastUtil.showToastDefault(mContext,"姓名无修改!");
            return;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", AppConfig.token);
        params.put("name", etSetNameAtyName.getText().toString().trim());

        EasyHttp.doPost(mContext, ServerURL.UPDSTUDENTINFO, params, null, null, false, new EasyHttp.OnEasyHttpDoneListener<Object>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<Object> resultBean) {
                ToastUtil.showToastDefault(mContext,"操作成功!");
                AppConfig.userVo.name=etSetNameAtyName.getText().toString().trim();
                finish();
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
              ToastUtil.showToastDefault(mContext,message);
            }
        });

    }
}
