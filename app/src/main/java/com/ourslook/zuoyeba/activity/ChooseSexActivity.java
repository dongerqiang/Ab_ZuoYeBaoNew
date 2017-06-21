package com.ourslook.zuoyeba.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ourslook.zuoyeba.AppConfig;
import com.ourslook.zuoyeba.BaseActivity;
import com.ourslook.zuoyeba.Constants;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ServerURL;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by huangyi on 16/5/17.
 * 修改性别界面
 */
public class ChooseSexActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.tv_setSexAty_man)
    TextView tvSetSexAtyMan;
    @Bind(R.id.tv_setSexAty_woman)
    TextView tvSetSexAtyWoman;

    int sex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sex = getIntent().getIntExtra(Constants.PASS_SEX, -1);
        if (sex == -1) {
            ToastUtil.showToastDefault(mContext, "参数异常");
            finish();
        }
        setContentViewWithDefaultTitle(R.layout.activity_choose_sex, "性别");
    }

    @Override
    protected void initView() {
        mTvTitleRight.setText("保存");
        mTvTitleRight.setVisibility(View.VISIBLE);
        setOnClickListeners(this, mTvTitleRight, tvSetSexAtyMan, tvSetSexAtyWoman);
        if (sex == 1) {//男
            tvSetSexAtyMan.setSelected(true);
        } else if (sex == 2) {//女
            tvSetSexAtyWoman.setSelected(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_title_right://保存
                if (!tvSetSexAtyMan.isSelected() && !tvSetSexAtyWoman.isSelected()) {
                    ToastUtil.showToastOnce(mContext, "请选择性别");
                } else {
                    changeSex();
                }
                break;
            case R.id.tv_setSexAty_man:
                tvSetSexAtyMan.setSelected(true);
                tvSetSexAtyWoman.setSelected(false);
                break;
            case R.id.tv_setSexAty_woman:
                tvSetSexAtyMan.setSelected(false);
                tvSetSexAtyWoman.setSelected(true);
                break;
        }
    }

    /**
     * 修改用户性别
     */
    private void changeSex() {
        if (sex == (tvSetSexAtyMan.isSelected() ? 1 : 2)) {
            ToastUtil.showToastDefault(mContext, "性别无修改!");
            return;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", AppConfig.token);
        params.put("sex", tvSetSexAtyMan.isSelected() ? "1" : "2");
        EasyHttp.doPost(mContext, ServerURL.UPDSTUDENTINFO, params, null, null, false, new EasyHttp.OnEasyHttpDoneListener<Object>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<Object> resultBean) {
                ToastUtil.showToastDefault(mContext, "操作成功!");
                AppConfig.userVo.sex = tvSetSexAtyMan.isSelected() ? 1 : 2;
                finish();
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                ToastUtil.showToastDefault(mContext, message);
            }
        });

    }
}
