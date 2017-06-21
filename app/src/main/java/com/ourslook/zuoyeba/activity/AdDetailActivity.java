package com.ourslook.zuoyeba.activity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import com.ourslook.zuoyeba.BaseActivity;
import com.ourslook.zuoyeba.Constants;
import com.ourslook.zuoyeba.R;

import butterknife.Bind;

/**
 * Created by Administrator on 2016/2/22.
 */
public class AdDetailActivity extends BaseActivity {
    @Bind(R.id.wv_addetail)
    WebView wvAddetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithDefaultTitle(R.layout.activity_ad_detail, "作业吧");
    }

    @Override
    protected void initView() {
        wvAddetail.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wvAddetail.getSettings().setJavaScriptEnabled(true);
        wvAddetail.loadUrl(getIntent().getStringExtra(Constants.BANNER_URL));
        wvAddetail.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }
}
