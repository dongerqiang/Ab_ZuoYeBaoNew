package com.ourslook.zuoyeba.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.ourslook.zuoyeba.BaseActivity;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ServerURL;
import com.ourslook.zuoyeba.ZuoYeBaApplication;
import com.ourslook.zuoyeba.model.InfAboutUs;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.ToastUtil;
import com.ourslook.zuoyeba.view.dialog.LoadingDialog;

import butterknife.Bind;

/**
 * Created by huangyi on 16/5/17
 * 关于我们界面
 */
public class AboutOursActivity extends BaseActivity {
    @Bind(R.id.iv_aboutUsAty_companyLogo)
    ImageView ivAboutUsAtyCompanyLogo;
    @Bind(R.id.tv_aboutUsAty_companyName)
    TextView tvAboutUsAtyCompanyName;
    @Bind(R.id.tv_aboutUsAty_companyInfo)
    TextView tvAboutUsAtyCompanyInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithDefaultTitle(R.layout.activity_about_ours, "关于我们");
    }

    @Override
    protected void initView() {

        getHttpData();
    }


    /**
     * 获取关于我们数据
     */
    private void getHttpData() {
        LoadingDialog.showLoadingDialog(mContext);
        EasyHttp.doPost(mContext, ServerURL.GETABOUTUS, null, null, InfAboutUs.class, false, new EasyHttp.OnEasyHttpDoneListener<InfAboutUs>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<InfAboutUs> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                if (null != resultBean.data) {
                    ZuoYeBaApplication.imageLoader.displayImage(resultBean.data.logourl, ivAboutUsAtyCompanyLogo);
                    tvAboutUsAtyCompanyName.setText(resultBean.data.title);
                    tvAboutUsAtyCompanyInfo.setText(resultBean.data.content);
                }
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastDefault(mContext, message);

            }
        });
    }
}
