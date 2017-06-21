package com.ourslook.zuoyeba.activity.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;

import com.ourslook.zuoyeba.BaseActivity;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ServerURL;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.ToastUtil;
import com.ourslook.zuoyeba.view.dialog.LoadingDialog;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

/**
 * 用户许可协议
 * Created by DuanLu on 2016/5/18.
 */
public class UserAgreementActivity extends BaseActivity {
    @Bind(R.id.web_userAgreement)
    WebView mWebUesrAgreement;//用户许可协议

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithDefaultTitle(R.layout.activity_user_agreement, "用户许可协议");
    }

    @Override
    protected void initView() {
        getData();
    }

    /**
     * 获取用户许可协议父文本接口
     */
    private void getData() {
        LoadingDialog.showLoadingDialog(mContext);
        Map<String, String> params = new HashMap<String, String>();
        EasyHttp.doPost(mContext, ServerURL.GETPROTOCOL, params, null, null, false, new EasyHttp.OnEasyHttpDoneListener<Object>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<Object> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                if (!TextUtils.isEmpty(resultBean.data.toString())) {
                    mWebUesrAgreement.loadUrl(resultBean.data.toString());
                }
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastOnce(mContext, message);
            }
        });
    }

}
