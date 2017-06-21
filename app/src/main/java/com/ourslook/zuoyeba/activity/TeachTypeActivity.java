package com.ourslook.zuoyeba.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ourslook.zuoyeba.AppConfig;
import com.ourslook.zuoyeba.BaseActivity;
import com.ourslook.zuoyeba.Constants;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ServerURL;
import com.ourslook.zuoyeba.model.AccMemberModel;
import com.ourslook.zuoyeba.model.CommonUploadModel;
import com.ourslook.zuoyeba.model.CourseModel;
import com.ourslook.zuoyeba.model.GradeModel;
import com.ourslook.zuoyeba.model.PayBindAlipayModel;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.DateUtils;
import com.ourslook.zuoyeba.utils.DisplayUtils;
import com.ourslook.zuoyeba.utils.StringUtils;
import com.ourslook.zuoyeba.utils.ToastUtil;
import com.ourslook.zuoyeba.view.ScrollerNumberPicker;
import com.ourslook.zuoyeba.view.dialog.DelDialog;
import com.ourslook.zuoyeba.view.dialog.GradePickDialog;
import com.ourslook.zuoyeba.view.dialog.LoadingDialog;
import com.ourslook.zuoyeba.view.dialog.MyTimePickerDialog;
import com.ourslook.zuoyeba.view.dialog.WarningDailog;
import com.ourslook.zuoyeba.view.dialog.common.PhotoUtils;
import com.ourslook.zuoyeba.view.dialog.common.TakePhotoDialog;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * 提交授课方式界面
 * Created by zhaotianlong on 2016/5/16.
 */
public class TeachTypeActivity extends BaseActivity implements View.OnClickListener, View.OnLongClickListener, MyTimePickerDialog.OnChooseTimeListener {

    @Bind(R.id.ll_howToTeachAty_grade)
    LinearLayout mLLGrade;// 年级点击布局
    @Bind(R.id.tv_howToTeachAty_grade)
    TextView mTvGrade;// 年级文字布局
    @Bind(R.id.ll_howToTeachAty_subject)
    LinearLayout mLLSubject;//科目点击布局
    @Bind(R.id.tv_howToTeachAty_subject)
    TextView mTvSubject;// 科目文字布局
    @Bind(R.id.ll_howToTeachAty_time)
    LinearLayout mLLTime;//时间点击布局
    @Bind(R.id.tv_howToTeachAty_time)
    TextView mTvTime;// 时间文字布局
    @Bind(R.id.ll_how_class)
    LinearLayout mLLAddress;//地址点击布局
    @Bind(R.id.et_howToTeachAty_address)
    TextView mEdtAddress;// 地址文字布局
    @Bind(R.id.fl_howToTeachAty_add1)
    FrameLayout mfrAddPic;//添加图片
    @Bind(R.id.fl_howToTeachAty_add2)
    FrameLayout mfrAddPic1;//添加图片
    @Bind(R.id.tv_howToTeachAty_ok)
    TextView tv_ok;
    @Bind(R.id.line_address)
    View lineAddress;

    private Intent intent;
    private ArrayList<Integer> imgid = new ArrayList<>();//  图片id列表
    private String filePath;// 上传图片路径
    //像是要上传的6张图片
    private ImageView iv_1, iv_2, iv_3, iv_4, iv_5, iv_6;
    private ImageView mIvdelete1, mIvdelete2, mIvdelete3, mIvdelete4, mIvdelete5, mIvdelete6;
    private FrameLayout fl_1, fl_2, fl_3, fl_4, fl_5, fl_6;
    private DelDialog dialogdel;// 删除照片对话框
    private int[] pics = {0, 0, 0, 0, 0, 0};
    private ArrayList<ImageView> ivs;
    private ArrayList<ImageView> mIvDeletes;
    private ArrayList<FrameLayout> fls;
    private ArrayList<Uri> uris;
    private List<GradeModel> gradeList = new ArrayList<>();
    private ArrayList<String> grades = new ArrayList<>();//  年级列表
    private List<CourseModel> courseList = new ArrayList<>();
    private ArrayList<String> courses = new ArrayList<>();//  科目列表
    private boolean isOpenGrade;//年级
    private boolean isOpenSubject;//科目
    private boolean isOpenTime;//授课时间
    private boolean isOpenAddress;//年级
    private GradePickDialog mGradeDialog;// 选择年级
    private GradePickDialog mSubjectDialog;// 选择科目
    //    private TimePickDialog mTimeDialog;// 选择时间
    private TakePhotoDialog takePhotoDialog;// 选择照片
    LoadingDialog loadingDialog;
    private int gradeIndex = 0;
    private int subjectIndex = 0;
    private int gradeType = 0;// 学校类型
    private String mAddress;
    /**
     * 传入的参数(必须)
     */
    long gradeid;// 年级
    long courseid;// 科目
    long time = 0;// 授课时间
    int type;// 授课方式 1:电话 2:视频 3:上门
    /**
     * 传入的参数(可选),授课方式为上门时必填
     */
    long areaid = -1;// 授课地址Id
    String address;// 授课详细地址
    double coordinatex;// 授课坐标X
    double coordinatey;// 授课坐标Y
    String imgids;// 问题图片列表
    private LatLng mLatlng;//授课坐标

    MyTimePickerDialog mMyTimePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithDefaultTitle(R.layout.activity_teach_type, "服务详情");
        gradeType = getIntent().getIntExtra("gradeType", 0);
        type = getIntent().getIntExtra("type", 1);
        getMemberInfo();
        getGradeList();
        initView();
    }

    @Override
    protected void initView() {
        mLatlng = new LatLng(AppConfig.latitude, AppConfig.longitude);//设置默认经纬度

        iv_1 = getViewById(R.id.iv_howToTeachAty_1);
        iv_2 = getViewById(R.id.iv_howToTeachAty_2);
        iv_3 = getViewById(R.id.iv_howToTeachAty_3);
        iv_4 = getViewById(R.id.iv_howToTeachAty_4);
        iv_5 = getViewById(R.id.iv_howToTeachAty_5);
        iv_6 = getViewById(R.id.iv_howToTeachAty_6);
        fl_1 = getViewById(R.id.fl_howToTeachAty_1);
        fl_2 = getViewById(R.id.fl_howToTeachAty_2);
        fl_3 = getViewById(R.id.fl_howToTeachAty_3);
        fl_4 = getViewById(R.id.fl_howToTeachAty_4);
        fl_5 = getViewById(R.id.fl_howToTeachAty_5);
        fl_6 = getViewById(R.id.fl_howToTeachAty_6);

        //删除图标
        mIvdelete1 = getViewById(R.id.iv__howToTeachAty_delete1);
        mIvdelete2 = getViewById(R.id.iv__howToTeachAty_delete2);
        mIvdelete3 = getViewById(R.id.iv__howToTeachAty_delete3);
        mIvdelete4 = getViewById(R.id.iv__howToTeachAty_delete4);
        mIvdelete5 = getViewById(R.id.iv__howToTeachAty_delete5);
        mIvdelete6 = getViewById(R.id.iv__howToTeachAty_delete6);

        uris = new ArrayList<>();

        ivs = new ArrayList<>();
        ivs.add(iv_1);
        ivs.add(iv_2);
        ivs.add(iv_3);
        ivs.add(iv_4);
        ivs.add(iv_5);
        ivs.add(iv_6);
        fls = new ArrayList<>();
        fls.add(fl_1);
        fls.add(fl_2);
        fls.add(fl_3);
        fls.add(fl_4);
        fls.add(fl_5);
        fls.add(fl_6);

        mIvDeletes = new ArrayList<>();
        mIvDeletes.add(mIvdelete1);
        mIvDeletes.add(mIvdelete2);
        mIvDeletes.add(mIvdelete3);
        mIvDeletes.add(mIvdelete4);
        mIvDeletes.add(mIvdelete5);
        mIvDeletes.add(mIvdelete6);

        for (int i = 0; i < pics.length; i++) {
            //fls.get(i).setOnLongClickListener(this);
            fls.get(i).setOnClickListener(this);
            mIvDeletes.get(i).setOnClickListener(this);
        }

        if (type == 3) {
            mLLAddress.setVisibility(View.VISIBLE);
            lineAddress.setVisibility(View.VISIBLE);
        } else {
            mLLAddress.setVisibility(View.GONE);
            lineAddress.setVisibility(View.GONE);
        }
        setOnClickListeners(this, mLLGrade, mLLSubject, mLLTime, mLLAddress, mfrAddPic, mfrAddPic1, tv_ok);
        mMyTimePickerDialog = new MyTimePickerDialog(mContext, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_howToTeachAty_grade://年级
                getGradeList();
                break;
            case R.id.ll_howToTeachAty_subject://科目
                getCourseList();
                break;
            case R.id.ll_howToTeachAty_time://日期
                showPickDialogTime();
                break;
            case R.id.ll_how_class://地址
                //跳到 选择上门地址界面
                Intent intent1 = new Intent();
                intent1.setClass(this, ChooseLocationActivity.class);
                intent1.putExtra(Constants.PASS_ADDRESS, mAddress);
                if (mLatlng != null) {
                    intent1.putExtra("latlng", mLatlng);
                } else {
                    intent1.putExtra("latlng", new LatLng(AppConfig.latitude, AppConfig.longitude));
                }
                startActivityForResult(intent1, Constants.CODE_201);
                break;
            case R.id.tv_howToTeachAty_ok://提交

                //peter2 bind zhifubao
                if (!AppConfig.iscreditvalid) {
                    showWarningDialogBink();
                } else {
                    if (type == 1 || type == 2) {
                        if (checked1()) {
                            addWork1();
                        }
                    } else if (type == 3) {
                        if (checked2()) {
                            addWork2();
                        }
                    } else {
                        ToastUtil.showToastDefault(this, "请选择授课方式");
                    }
                }
                break;
            case R.id.fl_howToTeachAty_1:
                intent = new Intent(this, ImageDisplayActivity.class);
                intent.putParcelableArrayListExtra("uri", uris);
                intent.putExtra("p", 0);
                startActivity(intent);
                break;
            case R.id.fl_howToTeachAty_2:
                intent = new Intent(this, ImageDisplayActivity.class);
                intent.putParcelableArrayListExtra("uri", uris);
                intent.putExtra("p", 1);
                startActivity(intent);
                break;
            case R.id.fl_howToTeachAty_3:
                intent = new Intent(this, ImageDisplayActivity.class);
                intent.putParcelableArrayListExtra("uri", uris);
                intent.putExtra("p", 2);
                startActivity(intent);
                break;
            case R.id.fl_howToTeachAty_4:
                intent = new Intent(this, ImageDisplayActivity.class);
                intent.putParcelableArrayListExtra("uri", uris);
                intent.putExtra("p", 3);
                startActivity(intent);
                break;
            case R.id.fl_howToTeachAty_5:
                intent = new Intent(this, ImageDisplayActivity.class);
                intent.putParcelableArrayListExtra("uri", uris);
                intent.putExtra("p", 4);
                startActivity(intent);
                break;
            case R.id.fl_howToTeachAty_6:
                intent = new Intent(this, ImageDisplayActivity.class);
                intent.putParcelableArrayListExtra("uri", uris);
                intent.putExtra("p", 5);
                startActivity(intent);
                break;
            case R.id.fl_howToTeachAty_add1:
                showTakePhotoDialog();
                break;
            case R.id.fl_howToTeachAty_add2:
                showTakePhotoDialog();
                break;
            case R.id.iv__howToTeachAty_delete1:
                setDialogDel(0);
                break;
            case R.id.iv__howToTeachAty_delete2:
                setDialogDel(1);
                break;
            case R.id.iv__howToTeachAty_delete3:
                setDialogDel(2);
                break;
            case R.id.iv__howToTeachAty_delete4:
                setDialogDel(3);
                break;
            case R.id.iv__howToTeachAty_delete5:
                setDialogDel(4);
                break;
            case R.id.iv__howToTeachAty_delete6:
                setDialogDel(5);
                break;
        }

    }

    /**
     * 弹出提示框(未绑定)
     */
    private  WarningDailog mBindDialog;

    private void showWarningDialogBink() {
        if (mBindDialog == null)
            mBindDialog = new WarningDailog(mContext, R.style.FullHeightDialog, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBindDialog.dismiss();
                    toBindAlipay();
                }
            });
        mBindDialog.setText("请先绑定支付宝！");
        mBindDialog.show();
    }

    private void toBindAlipay() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", AppConfig.token);// 登录token
        params.put("returnUrl", "zuoyebao://");// 新增参数
        LoadingDialog.showLoadingDialog(mContext);
        EasyHttp.doPost(mContext, ServerURL.ADDCREDITCARDALIPAY, params, null, PayBindAlipayModel.class, false, new EasyHttp.OnEasyHttpDoneListener<PayBindAlipayModel>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<PayBindAlipayModel> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                toSign(resultBean.data);

            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                LoadingDialog.dismissLoadingDialog();
                //ToastUtil.showToastDefault(mContext, message);
            }
        });
    }

    private void toSign(PayBindAlipayModel response) {
        String requestUrl = "https://mapi.alipay.com/gateway.do?_input_charset=utf-8&external_sign_no=" +
                response.getExternal_sign_no() +
//                "&notify_url=http://223.202.123.125:8080/zuoyebao/api/payment/backDaishowBindIdAlipay&return_url=zuoyebao://&partner=2088121701890788&product_code=GENERAL_WITHHOLDING_P&scene=INDUSTRY|DIGITAL_MEDIA&service=alipay.dut.customer.agreement.page.sign&sign=" +
                "&notify_url=" + response.getNotify_url() + "&return_url=zuoyebao://&partner=2088121701890788&product_code=GENERAL_WITHHOLDING_P&scene=INDUSTRY|DIGITAL_MEDIA&service=alipay.dut.customer.agreement.page.sign&sign=" +
                response.getSign() +
                "&sign_type=MD5";
        try {
            String url1 = java.net.URLEncoder.encode(requestUrl, "utf-8");
            requestUrl = "alipays://platformapi/startapp?appId=20000067&url=" + url1;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Uri uri = Uri.parse(requestUrl);
        try {
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(it);
        } catch (ActivityNotFoundException activityNotFoundException) {
            ToastUtil.showToastDefault(this, "请先安装支付宝客户端");
        }

    }

    /**
     * 个人信息
     */
    private void getMemberInfo() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", AppConfig.token);
        LoadingDialog.showLoadingDialog(mContext);
        EasyHttp.doPost(mContext, ServerURL.GETMEMBERINFO, params, null, AccMemberModel.class, false, new EasyHttp.OnEasyHttpDoneListener<AccMemberModel>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<AccMemberModel> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                if (StringUtils.isNotEmpty(resultBean)) {
                    int regionid = resultBean.data.regionid;
                    if (regionid != 0) {
                        areaid = regionid;
                    } else {
                        int cityid = resultBean.data.cityid;
                        if (cityid != 0) {
                            areaid = cityid;
                        } else {
                            int provinceid = resultBean.data.provinceid;
                            if (provinceid != 0)
                                areaid = provinceid;
                        }
                    }
                    mAddress = resultBean.data.address;
                    if (!StringUtils.isNotEmpty(address)) {
                        mEdtAddress.setText(mAddress);
                        TeachTypeActivity.this.address = address;
                    }
                }
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastDefault(mContext, message);
            }
        });
    }

    /**
     * 年级列表
     */
    private void getGradeList() {

        EasyHttp.doPost(mContext, ServerURL.GETGRADELIST, null, null, GradeModel.class, true, new EasyHttp.OnEasyHttpDoneListener<List<GradeModel>>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<List<GradeModel>> resultBean) {
                gradeList = resultBean.data;
                if (gradeList.size() == 0) {
                    ToastUtil.showToastDefault(TeachTypeActivity.this, "暂无数据");
                } else {
                    if (gradeType == 1) {
                        for (int i = 0; i < 6; i++) {
                            grades.add(gradeList.get(i).gradename);
                        }
                    } else if (gradeType == 2) {
                        for (int i = 6; i < gradeList.size(); i++) {
                            grades.add(gradeList.get(i).gradename);
                        }
                    } else {
                        for (int i = 0; i < gradeList.size(); i++) {
                            grades.add(gradeList.get(i).gradename);
                        }
                    }

                    showPickDialogGrade();
                }

            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                ToastUtil.showToastDefault(mContext, message);
            }
        });
    }

    /**
     * 弹出选择对话框(年级)
     */
    private void showPickDialogGrade() {

        if (mGradeDialog == null)
            mGradeDialog = new GradePickDialog(this, R.style.FullHeightDialog, grades, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mGradeDialog.getId() >= 0) {
                        mTvGrade.setText(mGradeDialog.getText());
                        if (gradeType == 2) {
                            gradeid = gradeList.get(mGradeDialog.getId() + 6).gradeid;
                        } else {
                            gradeid = gradeList.get(mGradeDialog.getId()).gradeid;
                        }

                        mTvGrade.setTextColor(getResources().getColor(R.color.tc6));
                        mGradeDialog.dismiss();
                        if (isOpenSubject != true) {
                            getCourseList();
                        }
                    }
                }
            }, new ScrollerNumberPicker.OnSelectListener() {
                @Override
                public void endSelect(int id, String text) {
                    if (text.equals("") || text == null)
                        return;
                    if (gradeIndex == id)
                        return;
                    gradeIndex = id;

                }

                @Override
                public void selecting(int id, String text) {
                }
            });
        mGradeDialog.setCanceledOnTouchOutside(false);
        Window window = mGradeDialog.getWindow();
        // 设置Dialog进出动画
        window.setWindowAnimations(R.style.dialogWindowAnim);
        // 设置Dialog显示位置
        window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mGradeDialog.show();

    }

    /**
     * 科目列表
     */
    private void getCourseList() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("type", gradeType + "");
        EasyHttp.doPost(mContext, ServerURL.GETCOURSELIST, params, null, CourseModel.class, true, new EasyHttp.OnEasyHttpDoneListener<List<CourseModel>>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<List<CourseModel>> resultBean) {

                courseList = resultBean.data;
                if (courseList.size() == 0) {
                    ToastUtil.showToastDefault(TeachTypeActivity.this, "科目信息获取失败，请稍后再试！");
                } else {
                    for (int i = 0; i < courseList.size(); i++) {
                        courses.add(courseList.get(i).coursename);
                    }
                    showPickDialogSubject();
                }
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                ToastUtil.showToastDefault(mContext, message);
            }
        });
    }

    /**
     * 弹出选择对话框(科目)
     */
    private void showPickDialogSubject() {
        if (mSubjectDialog == null)
            mSubjectDialog = new GradePickDialog(this, R.style.FullHeightDialog, courses, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSubjectDialog.getId() >= 0) {
                        mTvSubject.setText(mSubjectDialog.getText());
                        courseid = courseList.get(mSubjectDialog.getId()).getCourseid();
                        mTvSubject.setTextColor(getResources().getColor(R.color.tc6));
                        mSubjectDialog.dismiss();
                        if (isOpenTime != true) {
                            showPickDialogTime();
                        }
                    }
                }
            }, new ScrollerNumberPicker.OnSelectListener() {
                @Override
                public void endSelect(int id, String text) {
                    if (text.equals("") || text == null)
                        return;
                    if (subjectIndex == id)
                        return;
                    subjectIndex = id;

                }

                @Override
                public void selecting(int id, String text) {
                }
            });
        mSubjectDialog.setCanceledOnTouchOutside(false);
        Window window = mSubjectDialog.getWindow();
        // 设置Dialog进出动画
        window.setWindowAnimations(R.style.dialogWindowAnim);
        // 设置Dialog显示位置
        window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mSubjectDialog.show();
        isOpenSubject = true;
    }

    int date = -1;

    /**
     * 弹出选择对话框(选择时间)
     */
    private void showPickDialogTime() {
        mMyTimePickerDialog.setCancelable(false);
        mMyTimePickerDialog.show();
        isOpenTime = true;

        final ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mMyTimePickerDialog.mView, "translationY", mMyTimePickerDialog.mView.getTranslationY() + DisplayUtils.dp2px(200, mContext), mMyTimePickerDialog.mView.getTranslationY());
        objectAnimator.setDuration(500);
        objectAnimator.start();
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (objectAnimator.getAnimatedFraction() == 1) {
                    mMyTimePickerDialog.llChooseTimeBack.setBackgroundColor(mContext.getResources().getColor(R.color.transparency_125));
                } else {
                    mMyTimePickerDialog.llChooseTimeBack.setBackgroundColor(mContext.getResources().getColor(R.color.transparency));
                }
            }
        });
//        if (mTimeDialog == null)
//            mTimeDialog = new TimePickDialog(this, R.style.FullHeightDialog, new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (date > 0) {
//                        mTvTime.setText(mTimeDialog.getText());
//                        mTvTime.setTextColor(getResources().getColor(R.color.tc6));
//                        time = DateUtils.date2TimeStamp(mTimeDialog.getText());
//                        mTimeDialog.dismiss();
//
//                    } else {
//                        long l = System.currentTimeMillis();
//                        String t = DateUtils.formateDateLongToStringOnlyTime(l);
//                        int tc = Integer.parseInt(t);
//                        int td = Integer.parseInt(mTimeDialog.getTime());
//
//                        if (td > tc) {
//                            mTvTime.setText(mTimeDialog.getText());
//                            mTvTime.setTextColor(getResources().getColor(R.color.tc6));
//                            time = DateUtils.date2TimeStamp(mTimeDialog.getText());
//                            mTimeDialog.dismiss();
//                        } else {
//                            ToastUtil.showToastDefault(TeachTypeActivity.this, "请选择有效的时间");
//                        }
//                    }
//                }
//            }, new ScrollerNumberPicker.OnSelectListener() {
//                @Override
//                public void endSelect(int id, String text) {
//                    date = id;
//                    if (text.equals("立即")){
//                       handler.sendEmptyMessage(0);
//                    }else{
//                        handler.sendEmptyMessage(1);
//
//                    }
//                }
//
//                @Override
//                public void selecting(int id, String text) {
//
//                }
//            }, new ScrollerNumberPicker.OnSelectListener() {
//                @Override
//                public void endSelect(int id, String text) {
//
//                }
//
//                @Override
//                public void selecting(int id, String text) {
//
//                }
//            }, new ScrollerNumberPicker.OnSelectListener() {
//                @Override
//                public void endSelect(int id, String text) {
//
//                }
//
//                @Override
//                public void selecting(int id, String text) {
//
//                }
//            });
//
//        mTimeDialog.setCanceledOnTouchOutside(false);
//        Window window = mTimeDialog.getWindow();
//        // 设置Dialog进出动画
//        window.setWindowAnimations(R.style.dialogWindowAnim);
//        // 设置Dialog显示位置
//        window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
//        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        mTimeDialog.show();
//        mLLTime.setClickable(true);
//        isOpenTime = true;


    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
//                    mTimeDialog.pickHour.setVisibility(View.GONE);
//                    mTimeDialog.pickMinute.setVisibility(View.GONE);
//                    mTimeDialog.pickDate.setVisibility(View.VISIBLE);
//                    mTimeDialog.tv_maohao.setVisibility(View.INVISIBLE);
                    break;
                case 1:
//                    mTimeDialog.pickHour.setVisibility(View.VISIBLE);
//                    mTimeDialog.pickMinute.setVisibility(View.VISIBLE);
//                    mTimeDialog.pickDate.setVisibility(View.VISIBLE);
//                    mTimeDialog.tv_maohao.setVisibility(View.GONE);
                    break;
            }
        }
    };

    /**
     * 弹出选择对话框(照片)
     */
    private void showTakePhotoDialog() {
        if (takePhotoDialog == null)
            takePhotoDialog = new TakePhotoDialog(this);
        takePhotoDialog.setOnPhotoUrlResultListener(new TakePhotoDialog.OnPhotoUrlResultListener() {
            //file:///storage/emulated/0/Pictures/Screenshots/Screenshot_2016-07-06-18-29-38.png
            @Override
            public void onPhotoResult(Uri uri) {
                String path = takePhotoDialog.getmPhotoUtils().getPath(TeachTypeActivity.this, uri);
                filePath = path;
                uris.add(uri);
                //图片质量压缩
                Log.d("--", "图片质量压缩");
                try {
                    PhotoUtils.compressImage(path);
                } catch (Exception e) {
                    Log.d("--", "catch");
                }
                Log.d("--", "uploadImage");
                uploadImage();
            }

            @Override
            public void onPhotoCancel() {

            }
        });
        takePhotoDialog.showDialog();
    }

    /**
     * 上传图片
     */
    private void uploadImage() {
        LoadingDialog.showLoadingDialog(mContext);
        EasyHttp.zuoyebaUpLoadPicture(mContext, 2, new File(filePath), new EasyHttp.OnEasyHttpDoneListener<CommonUploadModel>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<CommonUploadModel> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                imgid.add(resultBean.data.pathid);
                setImage();

            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastDefault(mContext, message);
            }
        });

    }

    /**
     * 设置图片
     */
    private void setImage() {
        for (int i = 0; i < pics.length; i++) {
            if (pics[i] == 0) {
                pics[i] = 1;
                ImageLoader.getInstance().displayImage(uris.get(i).toString(), ivs.get(i));
                fls.get(i).setVisibility(View.VISIBLE);
                if (i == 2) {
                    mfrAddPic.setVisibility(View.GONE);
                    mfrAddPic1.setVisibility(View.VISIBLE);
                }
                if (i == 5) {
                    mfrAddPic1.setVisibility(View.GONE);
                }
                break;
            }

        }
    }

    /**
     * 删除图片
     *
     * @param j
     */
    private void setDialogDel(final int j) {
        dialogdel = new DelDialog(this, R.style.FullHeightDialog, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int count = -1;
                for (int i = 0; i < pics.length; i++) {
                    if (pics[i] == 1) {
                        count++;
                    }
                }
                uris.remove(j);
                imgid.remove(j);
                for (int i = 0; i < count; i++) {
                    ImageLoader.getInstance().displayImage(uris.get(i).toString(), ivs.get(i));
                }

                pics[count] = 0;
                fls.get(count).setVisibility(View.GONE);

                if (count < 3) {
                    mfrAddPic.setVisibility(View.VISIBLE);
                    mfrAddPic1.setVisibility(View.GONE);
                }
                if (count >= 3 && count < 6) {
                    mfrAddPic.setVisibility(View.GONE);
                    mfrAddPic1.setVisibility(View.VISIBLE);
                }
                dialogdel.dismiss();
            }
        });
        dialogdel.show();
    }

    @Override
    public boolean onLongClick(View v) {

        switch (v.getId()) {

            default:
                break;
        }

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == Constants.CODE_201) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    mLatlng = data.getParcelableExtra("latlng");
                    mAddress = data.getStringExtra("chooseAddress");
                    if (!TextUtils.isEmpty(mAddress)) {
                        mEdtAddress.setText(mAddress);
                    }
                }
            }
        } else {
            takePhotoDialog.getmPhotoUtils().onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 订单检查 1
     */
    private boolean checked1() {

        if (gradeid == 0) {
            ToastUtil.showToastDefault(this, "请选择年级");
            return false;
        }

        if (courseid == 0) {
            ToastUtil.showToastDefault(this, "请选择科目");
            return false;
        }
        if (time == 0) {
            ToastUtil.showToastDefault(this, "请选择授课时间");
            return false;

        }
        return true;
    }

    /**
     * 订单检查 2
     */
    private boolean checked2() {
        address = mEdtAddress.getText().toString().trim();
        if (gradeid == 0) {
            ToastUtil.showToastDefault(this, "请选择年级");
            return false;
        }

        if (courseid == 0) {
            ToastUtil.showToastDefault(this, "请选择科目");
            return false;
        }
        if (time == 0) {
            ToastUtil.showToastDefault(this, "请选择授课时间");
            return false;
        }

        if (areaid == -1) {
            ToastUtil.showToastDefault(this, "获取所在城市失败，请完善个人资料");
            return false;
        }
        if (address.equals("")) {
            ToastUtil.showToastDefault(this, "请输入授课详细地址");
            return false;
        }
        if (AppConfig.longitude == -1 && AppConfig.latitude == -1) {
            ToastUtil.showToastDefault(this, "定位失败");
            return false;
        }
        return true;
    }

    /**
     * 发订单 1
     */
    private void addWork1() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", AppConfig.token);
        params.put("gradeid", gradeid + "");
        params.put("courseid", courseid + "");
        params.put("time", time + "");
        params.put("type", type + "");
        Log.d("PickUpTeacherActivity","addWork1   type = "+type);
        if (imgid.size() > 0) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < imgid.size(); i++) {
                sb.append(imgid.get(i) + ";");
            }
            String s = sb.toString();
            imgids = s.substring(0, s.length() - 1);
            System.out.println(imgids);
            params.put("imgids", imgids);
        }
        LoadingDialog.showLoadingDialog(mContext);
        EasyHttp.doPost(mContext, ServerURL.ADDWORK, params, null, Object.class, false, new EasyHttp.OnEasyHttpDoneListener<Object>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<Object> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                //ToastUtil.showToastDefault(TeachTypeActivity.this, "下单成功");
                //跳转到选择老师界面
                Intent sendIntent = new Intent(mContext, PickUpTeacherActivity.class);
                sendIntent.putExtra(Constants.PASS_ORDER, resultBean.data + "");
                AppConfig.placeOrder = Long.parseLong(resultBean.data + "");
                startActivity(sendIntent);
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastDefault(mContext, message);
            }
        });
    }

    /**
     * 发订单 2
     */
    private void addWork2() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", AppConfig.token);
        params.put("gradeid", gradeid + "");
        params.put("courseid", courseid + "");
        params.put("time", time + "");
        params.put("type", type + "");
        params.put("areaid", areaid + "");
        params.put("address", address);
//        params.put("coordinatex", AppConfig.longitude + "");
//        params.put("coordinatey", AppConfig.latitude + "");
        params.put("coordinatex", mLatlng.longitude + "");//2016年8月31日修改
        params.put("coordinatey", mLatlng.latitude + "");//2016年8月31日修改
        Log.d("PickUpTeacherActivity","addWork1   type = "+type);
        if (imgid.size() > 0) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < imgid.size(); i++) {
                sb.append(imgid.get(i) + ";");
            }
            String s = sb.toString();
            imgids = s.substring(0, s.length() - 1);
            params.put("imgids", imgids);
        }
        LoadingDialog.showLoadingDialog(mContext);
        EasyHttp.doPost(mContext, ServerURL.ADDWORK, params, null, Object.class, false, new EasyHttp.OnEasyHttpDoneListener<Object>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<Object> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                //ToastUtil.showToastDefault(TeachTypeActivity.this, "下单成功");
                //跳转到选择老师界面
                Intent sendIntent = new Intent(mContext, PickUpTeacherActivity.class);
                sendIntent.putExtra(Constants.PASS_ORDER, resultBean.data + "");
                AppConfig.placeOrder = Long.parseLong(resultBean.data + "");
                sendIntent.putExtra(Constants.TEACHE_ADDRESS,address);
                sendIntent.putExtra(Constants.TEACHE_LATITUDE,mLatlng);
                sendIntent.putExtra("type", type);
                startActivity(sendIntent);
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastDefault(mContext, message);
            }
        });
    }


    @Override
    public void onChoose(String timeString) {
        mTvTime.setText(timeString);
        time = DateUtils.date2TimeStamp(timeString);
    }

}
