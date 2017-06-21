package com.ourslook.zuoyeba.activity;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ourslook.zuoyeba.AppConfig;
import com.ourslook.zuoyeba.BaseActivity;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ServerURL;
import com.ourslook.zuoyeba.activity.login.ForgetPwdActivity;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.StringUtils;
import com.ourslook.zuoyeba.utils.ToastUtil;
import com.ourslook.zuoyeba.view.dialog.LoadingDialog;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

/**
 * 取消绑定支付宝
 * Created by zhaotianlong on 2016/5/18.
 */
public class CancelBindAlipayActivity extends BaseActivity {

    @Bind(R.id.edt_login_pwd)
    EditText edtLoginPwd;//密码输入框
    @Bind(R.id.tv_cancel_bind)
    TextView tvCancelBind;//确认按钮
    @Bind(R.id.tv_cancel_bind_forgetPwd)
    TextView mTvForgetPwd;//忘记密码


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithDefaultTitle(R.layout.activity_cancel_bind, "取消绑定支付宝");
    }
//13474002382
    @Override
    protected void initView() {
        mTvForgetPwd.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mTvForgetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(ForgetPwdActivity.class);
            }
        });
        tvCancelBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.isNotEmpty(edtLoginPwd.getText().toString().trim())) {
                    cancelBind();
                } else {
                    ToastUtil.showToastDefault(CancelBindAlipayActivity.this, "请输入登录密码");
                }
            }
        });
    }

    /**
     * 取消绑定支付宝界面
     */
    private void cancelBind() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", AppConfig.token);// 登录token
        params.put("pwd", edtLoginPwd.getText().toString().trim());// 登录密码

        LoadingDialog.showLoadingDialog(mContext);
        EasyHttp.doPost(mContext, ServerURL.CANCELCREDITCARD, params, null, Object.class, false, new EasyHttp.OnEasyHttpDoneListener<Object>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<Object> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastDefault(CancelBindAlipayActivity.this, "取消绑定支付宝成功");
                AppConfig.iscreditvalid = false;
                finish();
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastDefault(mContext, message);
            }
        });
    }
}
