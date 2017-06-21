package com.ourslook.zuoyeba.activity.fragement.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.ourslook.zuoyeba.AppConfig;
import com.ourslook.zuoyeba.Constants;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ServerURL;
import com.ourslook.zuoyeba.ZuoYeBaApplication;
import com.ourslook.zuoyeba.activity.MessageActivity;
import com.ourslook.zuoyeba.activity.OrderListActivity;
import com.ourslook.zuoyeba.activity.SettingTeacherActivity;
import com.ourslook.zuoyeba.activity.TeacherDetaIInfoActivity;
import com.ourslook.zuoyeba.activity.TeacherEarningsActivity;
import com.ourslook.zuoyeba.activity.fragement.BaseFragment;
import com.ourslook.zuoyeba.activity.login.LoginActivity;
import com.ourslook.zuoyeba.model.AccMemberModel;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.DisplayUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by huangyi on 16/5/16.
 * 教师我的
 */
public class TeacherMineFragment extends BaseFragment implements View.OnClickListener {
    @Bind(R.id.iv_minefgmT_head)
    ImageView ivMinefgmTHead;//头像
    @Bind(R.id.tv_minefgmT_name)
    TextView tvMinefgmTName;//姓名
    @Bind(R.id.tv_minefgmT_lvl)
    TextView tvMinefgmTLvl;//教师级别
    @Bind(R.id.tv_minefgmT_money)
    TextView tvMinefgmTMoney;//本月收益
    @Bind(R.id.ll_minefgmT_money)
    LinearLayout llMinefgmTMoney;//点击本月收益
    @Bind(R.id.tv_minefgmT_mineStar)
    TextView tvMinefgmTMineStar;//多少星
    @Bind(R.id.ll_minefgmT_mineStar)
    LinearLayout llMinefgmTMineStar;//点击星级
    @Bind(R.id.tv_minefgmT_mineList)
    TextView tvMinefgmTMineList;//我的订单
    @Bind(R.id.tv_minefgmT_mineMsg)
    TextView tvMinefgmTMineMsg;//消息中心
    @Bind(R.id.tv_minefgmT_setting)
    TextView tvMinefgmTSetting;//设置

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_teacher_mine);
    }

    @Override
    protected void initView() {
        setOnClickListeners(this, ivMinefgmTHead, llMinefgmTMoney, llMinefgmTMineStar, tvMinefgmTMineList, tvMinefgmTMineMsg, tvMinefgmTSetting);
    }


    /**
     * 展示数据
     */
    private void showInfo() {
        int cornerRadius = DisplayUtils.dp2px(40, mContext);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(cornerRadius))
                .build();
        //加载用户头像
        if (AppConfig.userVo.photourl != null) {
            if (AppConfig.userVo.photourl.length() > 0) {
                ZuoYeBaApplication.imageLoader.displayImage(AppConfig.userVo.photourl, ivMinefgmTHead, options);
            }
        }
        tvMinefgmTName.setText(AppConfig.userVo.name);
        tvMinefgmTLvl.setText(AppConfig.userVo.teacher.level);
        tvMinefgmTMoney.setText(AppConfig.userVo.teacher.money + "");
        tvMinefgmTMineStar.setText(AppConfig.userVo.teacher.star + "");

    }

    /**
     * 设置默认数据
     */
    private void setDefaultInfo() {
        ivMinefgmTHead.setImageResource(R.drawable.default_head);
        tvMinefgmTName.setText("");
        tvMinefgmTLvl.setText("");
        tvMinefgmTMoney.setText(0 + "");
        tvMinefgmTMineStar.setText(0 + "");
    }

    @Override
    public void onStart() {
        super.onStart();
        if (AppConfig.isLogin) {
            if (AppConfig.userVo.teacher != null) {
                // 做到数据实时刷新  需要在此调用接口
                refreshUserInfo();
            }
        } else {
            setDefaultInfo();
        }
    }

    /**
     * 刷新用户信息
     */
    public void refreshUserInfo(){
        Map<String,String> params=new HashMap<>();
        params.put("token",""+AppConfig.token);
        EasyHttp.doPost(mContext, ServerURL.GETMEMBERINFO, params, null, AccMemberModel.class, false, new EasyHttp.OnEasyHttpDoneListener<AccMemberModel>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<AccMemberModel> resultBean) {
                 if(resultBean.data!=null){
                     AppConfig.userVo=resultBean.data;
                     showInfo();
                 }
            }
            @Override
            public void onEasyHttpFailure(String code, String message) {

            }
        });
    }

    @Override
    public void getHttpData() {

    }



    @Override
    public void onClick(View v) {
        if (!AppConfig.isLogin) {
            Intent intentLogin = new Intent(mContext, LoginActivity.class);
            startActivity(intentLogin);
            return;
        }
        switch (v.getId()) {
            case R.id.iv_minefgmT_head://点击头像
                Intent intentTeacherInfo = new Intent(mContext, TeacherDetaIInfoActivity.class);
                startActivity(intentTeacherInfo);
                break;
            case R.id.ll_minefgmT_money://点击收益
                // 收益
                Intent intentEarn = new Intent(mContext, TeacherEarningsActivity.class);
                startActivity(intentEarn);
                break;
            case R.id.ll_minefgmT_mineStar://点击星级
                //星级
                break;
            case R.id.tv_minefgmT_mineList://点击订单
                Intent intentOrder = new Intent(mContext, OrderListActivity.class);
                intentOrder.putExtra(Constants.DIS_TYPE, Constants.TEACHER);
                startActivity(intentOrder);
                break;
            case R.id.tv_minefgmT_mineMsg://点击消息中心
                Intent intentMessage = new Intent(mContext, MessageActivity.class);
                startActivity(intentMessage);
                break;
            case R.id.tv_minefgmT_setting://点击设置
                Intent intentSetting = new Intent(mContext, SettingTeacherActivity.class);
                startActivity(intentSetting);
                break;
        }
    }
}
