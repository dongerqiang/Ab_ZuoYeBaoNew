package com.ourslook.zuoyeba.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ourslook.zuoyeba.AppConfig;
import com.ourslook.zuoyeba.BaseActivity;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ServerURL;
import com.ourslook.zuoyeba.model.CourseModel;
import com.ourslook.zuoyeba.model.OrderTeacherDetailModel;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.ToastUtil;
import com.ourslook.zuoyeba.view.ImageLoaderUtil.ILutil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 老师详情界面
 * Created by zhaotianlong on 2016/5/19.
 */
public class TeacherInfoActivity extends BaseActivity {

    private ImageView iv_head;// 头像
    private TextView tv_name;// 姓名
    private TextView tv_lvl;// 老师级别
    private LinearLayout ll_stars;// 星星数量
    private TextView tv_sex;// 性别
    private TextView tv_teachYear;// 教龄
    private TextView tv_school;// 毕业院校
    private TextView tv_grade;// 年级
    private TextView tv_subject;// 科目
    private View ll_isQC;// 资格认证
    private TextView tv_isPC;// 平台认证
    private TextView tv_sign;// 个性签名

    private TextView tv_ok;// 确认老师
    private long teacherid;// 老师ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithDefaultTitle(R.layout.activity_teacher_info, "老师详情");
    }

    @Override
    protected void initView() {
        iv_head = (ImageView) findViewById(R.id.iv_teachInfoAty_head);
        tv_name = (TextView) findViewById(R.id.tv_teachInfoAty_name);
        tv_lvl = (TextView) findViewById(R.id.tv_teachInfoAty_teachlvl);
        ll_stars = (LinearLayout) findViewById(R.id.ll_teachInfo_stars);
        tv_sex = (TextView) findViewById(R.id.tv_teachInfoAty_sex);
        tv_teachYear = (TextView) findViewById(R.id.tv_teachInfoAty_teachYear);
        tv_school = (TextView) findViewById(R.id.tv_teachInfoAty_school);
        tv_grade = (TextView) findViewById(R.id.tv_teachInfoAty_grade);
        tv_subject = (TextView) findViewById(R.id.tv_teachInfoAty_subject);
        ll_isQC = findViewById(R.id.ll_teachInfoAty_isQC);
        tv_isPC = (TextView) findViewById(R.id.tv_teachInfoAty_isPC);
        tv_sign = (TextView) findViewById(R.id.tv_teachInfoAty_sign);
        tv_ok = (TextView) findViewById(R.id.tv_teachInfoAty_ok);

        teacherid = getIntent().getLongExtra("teacherid", -1);
        if (teacherid != -1) {
            getTeacherDetailById();

        }
    }

    /**
     * 查看老师详情
     */
    private void getTeacherDetailById() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", AppConfig.token);
        params.put("teacherid", teacherid + "");
        EasyHttp.doPost(mContext, ServerURL.GETTEACHERDETAILBYID, params, null, OrderTeacherDetailModel.class, false, new EasyHttp.OnEasyHttpDoneListener<OrderTeacherDetailModel>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<OrderTeacherDetailModel> resultBean) {
                OrderTeacherDetailModel response = resultBean.data;
                String photourl = response.getPhotourl();
                ImageLoader.getInstance().displayImage(photourl, iv_head, ILutil.getRoundImageOptions());
                tv_name.setText(response.getName());
                tv_lvl.setText(response.getLevel());
                double temp = response.getStar();
                int star = (int) (temp / 1);
                for (int i = 0; i < star; i++) {
                    ImageView imageView = new ImageView(mContext);
                    imageView.setImageResource(R.drawable.icon_fullstar);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                            ((int) getResources().getDimension(R.dimen.starSize)
                                    , (int) getResources().getDimension(R.dimen.starSize));
                    params.setMargins(5, 0, 5, 0);
                    imageView.setLayoutParams(params);
                    ll_stars.addView(imageView);
                }
                if (temp % 1 != 0) {
                    ImageView imageView = new ImageView(mContext);
                    imageView.setImageResource(R.drawable.icon_halfstar);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                            ((int) getResources().getDimension(R.dimen.starSize)
                                    , (int) getResources().getDimension(R.dimen.starSize));
                    params.setMargins(5, 0, 5, 0);
                    imageView.setLayoutParams(params);
                    ll_stars.addView(imageView);
                }
                int sex = response.getSex();
                if (sex == 1) {
                    tv_sex.setText("男");
                } else if (sex == 2) {
                    tv_sex.setText("女");
                }
                tv_teachYear.setText(response.getSchoolage() + "年");
                tv_school.setText(response.getGraduate());
                tv_grade.setText(response.getGrade());

                String course = "";
                ArrayList<CourseModel> courseList = (ArrayList<CourseModel>) response.courseList;
                if (courseList.size() > 0) {
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < courseList.size(); i++) {
                        sb.append(courseList.get(i).getCoursename() + "、");
                    }
                    course = sb.toString();
                    tv_subject.setText(course.substring(0, course.length() - 1));
                } else {
                    tv_subject.setText(course);
                }

                boolean qc = response.isQC();
                if (qc) {
                    ll_isQC.setVisibility(View.VISIBLE);
                } else {
                    ll_isQC.setVisibility(View.GONE);
                }
                boolean pc = response.isPC();
                if (pc) {
                    tv_isPC.setVisibility(View.VISIBLE);
                } else {
                    tv_isPC.setVisibility(View.GONE);
                }
                tv_sign.setText(response.getSignature());
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                ToastUtil.showToastDefault(mContext, message);
            }
        });

    }
}
