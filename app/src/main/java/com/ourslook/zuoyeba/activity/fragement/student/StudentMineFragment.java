package com.ourslook.zuoyeba.activity.fragement.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.ourslook.zuoyeba.AppConfig;
import com.ourslook.zuoyeba.Constants;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ZuoYeBaApplication;
import com.ourslook.zuoyeba.activity.DetailInfoActivity;
import com.ourslook.zuoyeba.activity.MessageActivity;
import com.ourslook.zuoyeba.activity.MyPointActivity;
import com.ourslook.zuoyeba.activity.OrderListActivity;
import com.ourslook.zuoyeba.activity.RedBagActivity;
import com.ourslook.zuoyeba.activity.SettingActivity;
import com.ourslook.zuoyeba.activity.fragement.BaseFragment;
import com.ourslook.zuoyeba.activity.login.LoginActivity;
import com.ourslook.zuoyeba.utils.DisplayUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 学生端我的
 * Created by wangyu on 16/5/16.
 */
public class StudentMineFragment extends BaseFragment implements View.OnClickListener {
    @Bind(R.id.iv_minefgmS_head)
    ImageView ivMinefgmSHead; //头像
    @Bind(R.id.tv_minefgmS_name)
    TextView tvMinefgmSName;//姓名
    @Bind(R.id.tv_minefgmS_grade)
    TextView tvMinefgmSGrade;//年级
    @Bind(R.id.tv_minefgmS_mineCount)
    TextView tvMinefgmSMineCount;//我的积分
    @Bind(R.id.tv_minefgmS_mineList)
    TextView tvMinefgmSMineList;//我的订单
    @Bind(R.id.tv_minefgmS_mineMsg)
    TextView tvMinefgmSMineMsg;//我的消息
    @Bind(R.id.tv_minefgmS_mineRed)
    TextView tvMinefgmSMineRed;//红包
    @Bind(R.id.tv_minefgmS_setting)
    TextView tvMinefgmSSetting;//设置

    DisplayImageOptions options;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_student_mine);
    }

    @Override
    protected void initView() {
        setOnClickListeners(this, ivMinefgmSHead, tvMinefgmSMineCount, tvMinefgmSMineList, tvMinefgmSMineMsg, tvMinefgmSMineRed, tvMinefgmSSetting);

    }


    private void setInfo() {
        tvMinefgmSName.setText(AppConfig.isLogin ? AppConfig.userVo.name : "");
        tvMinefgmSGrade.setText(AppConfig.isLogin ? AppConfig.userVo.student.grade : "");
        if (!AppConfig.isLogin) {
            ZuoYeBaApplication.imageLoader.displayImage("", ivMinefgmSHead, options);

        } else {
            int cornerRadius = DisplayUtils.dp2px(40, mContext);
            options = new DisplayImageOptions.Builder()
                    .displayer(new RoundedBitmapDisplayer(cornerRadius))
                    .build();
            //加载用户头像
            if (AppConfig.userVo.photourl != null) {
                if (AppConfig.userVo.photourl.length() > 0) {
                    ZuoYeBaApplication.imageLoader.displayImage(AppConfig.userVo.photourl, ivMinefgmSHead, options);
                }

            }
        }
    }
    /**
     * 设置默认数据
     */
    private void setDefaultInfo() {
        tvMinefgmSName.setText("");
        tvMinefgmSGrade.setText("");
        ZuoYeBaApplication.imageLoader.displayImage("", ivMinefgmSHead, options);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (AppConfig.isLogin && AppConfig.userVo.type == 1) {
            setInfo();
        } else {
            setDefaultInfo();
        }

    }

    @Override
    public void getHttpData() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        if (!AppConfig.isLogin) {
            Intent intentLogin = new Intent(mContext, LoginActivity.class);
            startActivity(intentLogin);
            return;
        }
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.iv_minefgmS_head://头像
                intent.setClass(mContext, DetailInfoActivity.class);
                break;
            case R.id.tv_minefgmS_mineCount://积分
                intent.setClass(mContext, MyPointActivity.class);
                break;
            case R.id.tv_minefgmS_mineList://订单
                //TODO 此处需要传入参数  判断是学生的订单
                intent.setClass(mContext, OrderListActivity.class);
                intent.putExtra(Constants.DIS_TYPE, Constants.STUDENT);
                break;
            case R.id.tv_minefgmS_mineMsg://消息
                intent.setClass(mContext, MessageActivity.class);
                break;
            case R.id.tv_minefgmS_mineRed://红包
                intent.setClass(mContext, RedBagActivity.class);
                break;
            case R.id.tv_minefgmS_setting://设置
                intent.setClass(mContext, SettingActivity.class);
                break;
        }
        startActivity(intent);
    }
}
