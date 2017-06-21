package com.ourslook.zuoyeba.activity;

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
 * Created by huangyi on 16/5/17.
 * 编辑详细地址
 */
public class EditAddressActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.et_setAddressAty_address)
    EditText etSetAddressAtyAddress;

    String address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        address=getIntent().getStringExtra(Constants.PASS_ADDRESS);
        setContentViewWithDefaultTitle(R.layout.activity_edit_address, "详细地址");
    }

    @Override
    protected void initView() {
        mTvTitleRight.setText("保存");
        mTvTitleRight.setVisibility(View.VISIBLE);
        if(address!=null){
            etSetAddressAtyAddress.setText(address);
            etSetAddressAtyAddress.setSelection(address.length());
        }
        setOnClickListeners(this,mTvTitleRight);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_title_right:
                changeAddress();
                break;
        }
    }

    /**
     * 修改地址
     */
    private void changeAddress(){
        if(etSetAddressAtyAddress.getText().toString()==null){
            ToastUtil.showToastDefault(mContext,"请输入地址!");
            return;
        }else if(etSetAddressAtyAddress.getText().toString().trim().length()==0){
            ToastUtil.showToastDefault(mContext,"请输入地址!");
            return;
        }else if(etSetAddressAtyAddress.getText().toString().trim().equals(address)){
            ToastUtil.showToastDefault(mContext,"地址无修改");
            return;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", AppConfig.token);
        params.put("address", etSetAddressAtyAddress.getText().toString().trim());

        EasyHttp.doPost(mContext, ServerURL.UPDSTUDENTINFO, params, null, null, false, new EasyHttp.OnEasyHttpDoneListener<Object>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<Object> resultBean) {
                ToastUtil.showToastDefault(mContext,"操作成功!");
                AppConfig.userVo.address=etSetAddressAtyAddress.getText().toString().trim();
                finish();
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                ToastUtil.showToastDefault(mContext,message);
            }
        });

    }
}
