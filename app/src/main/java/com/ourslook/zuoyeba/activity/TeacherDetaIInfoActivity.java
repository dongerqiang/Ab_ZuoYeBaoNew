package com.ourslook.zuoyeba.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.ourslook.zuoyeba.AppConfig;
import com.ourslook.zuoyeba.BaseActivity;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ServerURL;
import com.ourslook.zuoyeba.ZuoYeBaApplication;
import com.ourslook.zuoyeba.model.AccMemberModel;
import com.ourslook.zuoyeba.model.CourseModel;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.DisplayUtils;
import com.ourslook.zuoyeba.utils.ToastUtil;
import com.ourslook.zuoyeba.view.dialog.LoadingDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by huangyi on 16/5/18.
 */
public class TeacherDetaIInfoActivity extends BaseActivity implements CompoundButton.OnClickListener {
    @Bind(R.id.ll_detailInfoT_head)
    LinearLayout mLlHead;//用户图像
    @Bind(R.id.iv_detailInfoT_headBg)
    ImageView mIvHeadBg;
    @Bind(R.id.iv_detailInfoT_head)
    ImageView mIvHead;

    @Bind(R.id.ll_detailInfoT_name)
    LinearLayout mLlName;//姓名
    @Bind(R.id.tv_detailInfoT_name)
    TextView mTvName;

    @Bind(R.id.ll_detailInfoT_pNum)
    LinearLayout mLlPhoneNumber;//手机号
    @Bind(R.id.tv_detailInfoT_pNum)
    TextView mTvPhoneNumber;

    @Bind(R.id.ll_detailInfoT_sex)
    LinearLayout mLlSex;//性别
    @Bind(R.id.tv_detailInfoT_sex)
    TextView mTvSex;

    @Bind(R.id.ll_detailInfoT_tYear)
    LinearLayout mLlTYear;//教龄
    @Bind(R.id.tv_detailInfoT_tYear)
    TextView mTvTYear;

    @Bind(R.id.ll_detailInfoT_grade)
    LinearLayout mLlGrade;//教学年级科目
    @Bind(R.id.tv_detailInfoT_grade)
    TextView mTvGrade;

    @Bind(R.id.ll_detailInfoT_address)
    LinearLayout mLlAddress;//地址
    @Bind(R.id.tv_detailInfoT_address)
    TextView mTvAddress;

    @Bind(R.id.ll_detailInfoT_confim)
    LinearLayout mLlConfim;//认证
    @Bind(R.id.tv_detailInfoT_isQC)
    TextView mTvIsQC;//资格认证
    @Bind(R.id.tv_detailInfoT_isPC)
    TextView mTvIsPc;//平台认证

    @Bind(R.id.ll_detailInfoT_sign)
    LinearLayout mLlSign;//个性签名
    @Bind(R.id.tv_detailInfoT_sign)
    TextView mTvSign;

    @Bind(R.id.tv_detailInfoT_updateInfo)
    TextView mTvUpdateInfo;//申请更新资料

    AccMemberModel mAccMemberModel;//教师实体

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithDefaultTitle(R.layout.activity_teacher_detail_info, "详细信息");
    }

    @Override
    protected void initView() {
        setTeacherInfo();
        setOnClickListeners(this, mTvUpdateInfo);
//        setOnClickListeners(this, mLlHead, mLlName, mLlPhoneNumber, mLlSex,
//                mLlTYear, mLlGrade, mLlAddress, mLlConfim, mLlSign, mTvUpdateInfo);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_detailInfoT_head://用户图像

                break;
            case R.id.ll_detailInfoT_name://姓名

                break;
            case R.id.ll_detailInfoT_pNum://手机号

                break;
            case R.id.ll_detailInfoT_sex://性别

                break;
            case R.id.ll_detailInfoT_tYear://教龄

                break;
            case R.id.ll_detailInfoT_grade://教学年纪科目

                break;
            case R.id.ll_detailInfoT_address://地址

                break;
            case R.id.ll_detailInfoT_confim://认证

                break;
            case R.id.ll_detailInfoT_sign://个性签名

                break;
            case R.id.tv_detailInfoT_updateInfo://申请更新资料
                applyUpdTeacherInfo();
                break;
        }
    }

    /**
     * 设置教师个人信息
     */
    private void setTeacherInfo() {
        if (!TextUtils.isEmpty(AppConfig.userVo.photourl)) {
            if (AppConfig.userVo.photourl.length() > 0) {
                int cornerRadius = DisplayUtils.dp2px(18, mContext);
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .displayer(new RoundedBitmapDisplayer(cornerRadius))
                        .build();
                mIvHead.setVisibility(View.VISIBLE);
                mIvHeadBg.setVisibility(View.GONE);
                ZuoYeBaApplication.imageLoader.displayImage(AppConfig.userVo.photourl, mIvHead, options);
            }
        } else {
            mIvHead.setVisibility(View.GONE);
            mIvHeadBg.setVisibility(View.VISIBLE);
        }
        mTvName.setText(AppConfig.userVo.name);
        mTvPhoneNumber.setText(AppConfig.userVo.mobile);
        mTvSex.setText(AppConfig.userVo.sex == 1 ? "男" : (AppConfig.userVo.sex == 2 ? "女" : ""));
        mTvTYear.setText(AppConfig.userVo.teacher.schoolage + "年");

        String course = "";
        ArrayList<CourseModel> courseList = AppConfig.userVo.teacher.courseList;
        if (courseList.size() > 0) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < courseList.size(); i++) {
                sb.append(courseList.get(i).getCoursename() + "、");
            }
            course = sb.toString();
            mTvGrade.setText(AppConfig.userVo.teacher.grade + " " + course.substring(0, course.length() - 1));
        } else {
            mTvGrade.setText(AppConfig.userVo.teacher.grade);
        }

        mTvAddress.setText(AppConfig.userVo.province + AppConfig.userVo.city
                + AppConfig.userVo.region + AppConfig.userVo.address);

        if (AppConfig.userVo.teacher.isQC) {
            mTvIsQC.setVisibility(View.VISIBLE);
        } else {
            mTvIsQC.setVisibility(View.GONE);
        }
        if (AppConfig.userVo.teacher.isPC) {
            mTvIsPc.setVisibility(View.VISIBLE);
        } else {
            mTvIsPc.setVisibility(View.GONE);
        }
        mTvSign.setText(AppConfig.userVo.teacher.signature);
//        ToastUtil.showToastDefault(mContext,mTvSign.getText().toString().length()+"");
    }

    /**
     * 老师资料变更
     */
    private void applyUpdTeacherInfo() {
        LoadingDialog.showLoadingDialog(mContext);
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", AppConfig.token);
        EasyHttp.doPost(mContext, ServerURL.APPLYUPDTEACHERINFO, params, null, null, false, new EasyHttp.OnEasyHttpDoneListener<Object>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<Object> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastOnce(mContext, "老师资料变更申请已提交");
                finish();
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastOnce(mContext, message);
            }
        });
    }

}
