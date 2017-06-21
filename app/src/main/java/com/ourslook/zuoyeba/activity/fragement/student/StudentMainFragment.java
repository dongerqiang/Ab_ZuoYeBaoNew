package com.ourslook.zuoyeba.activity.fragement.student;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ourslook.zuoyeba.AppConfig;
import com.ourslook.zuoyeba.Constants;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ServerURL;
import com.ourslook.zuoyeba.ZuoYeBaApplication;
import com.ourslook.zuoyeba.activity.AdDetailActivity;
import com.ourslook.zuoyeba.activity.ApplyTeacherActivity;
import com.ourslook.zuoyeba.activity.TeachTypeActivity;
import com.ourslook.zuoyeba.activity.fragement.BaseFragment;
import com.ourslook.zuoyeba.activity.login.LoginActivity;
import com.ourslook.zuoyeba.model.AccMemberModel;
import com.ourslook.zuoyeba.model.AccStudentModel;
import com.ourslook.zuoyeba.model.CoursePriceModel;
import com.ourslook.zuoyeba.model.InfAdvertisementModel;
import com.ourslook.zuoyeba.model.OrderAppointmentModel;
import com.ourslook.zuoyeba.model.PayBindAlipayModel;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.DateUtils;
import com.ourslook.zuoyeba.utils.ToastUtil;
import com.ourslook.zuoyeba.view.dialog.LoadingDialog;
import com.ourslook.zuoyeba.view.dialog.SureBindingAddressDialog;
import com.ourslook.zuoyeba.view.dialog.TeachTypeDialog;
import com.ourslook.zuoyeba.view.dialog.WarningDailog;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerClickListener;
import com.youth.banner.loader.ImageLoader;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * 学生端首页
 * Created by wangyu on 16/5/16.
 */
public class StudentMainFragment extends BaseFragment implements View.OnClickListener {
    @Bind(R.id.banner_new)
    Banner mBanner;
    @Bind(R.id.ll_homefgmS_xiaoxue)
    LinearLayout mLLSmallSchool;//小学布局
    @Bind(R.id.ll_homefgmS_chuzhong)
    LinearLayout mLLJuniorSchool;//初中布局
    @Bind(R.id.iv_main_want_teacher)
    ImageView mIvWantTeacher;//我要当老师
    @Bind(R.id.ll_homefgmS_booking)
    LinearLayout mLLAppointment;//预约布局
    @Bind(R.id.ll_homefgmS_isLogin)
    LinearLayout mLLIsLogin;//预约布局
    @Bind(R.id.tv_homefgmS_teachType)
    TextView mTvTeachType;//授课类型
    @Bind(R.id.iv_homefgmS_teachType)
    ImageView mIvTeachType;
    @Bind(R.id.tv_homefgmS_time)
    TextView mTvTime;
    @Bind(R.id.tv_homefgmS_isLogin)
    TextView mTvIsLogin;
    private List<InfAdvertisementModel> bannerInfo = new ArrayList<>();
    private boolean isLogin = false;// 是否登录
    private boolean isBooking = false;// 是否预约
    private WarningDailog mLoginDailog;// 提示的dialog
    private WarningDailog mBindDialog;// 提示的dialog
    private boolean iscreditvalid;// 是否绑定支付宝
    private ArrayList<CoursePriceModel> coursePriceList = new ArrayList<>();
    private ArrayList<String> prices = new ArrayList<>();//  课程单价列表
    private TeachTypeDialog dialogTeachType;// 选择授课方式
    private Intent intent;
    int type;// 授课方式 1:电话 2:视频 3:上门
    int gradeType;
    public static final String TAG = "StudentMainFragment";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_student_main);
    }

    int mWidth;
    int mHeight;

    @Override
    protected void initView() {
        setOnClickListeners(this, mLLSmallSchool, mLLJuniorSchool, mLLIsLogin, mIvWantTeacher);

        DisplayMetrics metrics = new DisplayMetrics();

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        mWidth = metrics.widthPixels;
        mHeight = (int) Math.ceil((float) mWidth * (float) 3 / 5);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(mWidth, mHeight);
        mBanner.setLayoutParams(params);

        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        mBanner.setIndicatorGravity(BannerConfig.CENTER);
        mBanner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                ZuoYeBaApplication.imageLoader.displayImage(((InfAdvertisementModel) path).imgurl, imageView);
            }
        });
        mBanner.setOnBannerClickListener(new OnBannerClickListener() {
            @Override
            public void OnBannerClick(int position) {
                Intent it = new Intent(mContext, AdDetailActivity.class);
                it.putExtra(Constants.BANNER_URL, bannerInfo.get(position - 1).url);
                startActivity(it);
            }
        });
    }

    @Override
    public void getHttpData() {
        getBanner();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (AppConfig.isLogin && AppConfig.userVo.type == 1) {
            getMemberInfo();
            getAppointment();
        } else {
            mLLAppointment.setVisibility(View.GONE);
//            mIvWantTeacher.setVisibility(View.VISIBLE);
            mLLIsLogin.setVisibility(View.VISIBLE);
            mTvIsLogin.setText(R.string.noLogin_home);
        }
    }

    /**
     * Banner
     */
    private void getBanner() {
        EasyHttp.doPost(mContext, ServerURL.GETADVERTISEMENT, null, null, InfAdvertisementModel.class, true, new EasyHttp.OnEasyHttpDoneListener<List<InfAdvertisementModel>>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<List<InfAdvertisementModel>> resultBean) {
                bannerInfo = resultBean.data;
                if (mBanner != null && resultBean.data != null && resultBean.data.size() > 0) {
                    mBanner.setImages(resultBean.data);
                    mBanner.start();
                    mBanner.startAutoPlay();
                }
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                Log.w(TAG,"getBanner()  onEasyHttpFailure() message = "+message);
                ToastUtil.showToastDefault(mContext, message);
            }
        });
    }

    /**
     * 个人信息
     */
    private void getMemberInfo() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", AppConfig.token);
        Log.d("--", "StudentMainFragment");
        EasyHttp.doPost(mContext, ServerURL.GETMEMBERINFO, params, null, AccMemberModel.class, false, new EasyHttp.OnEasyHttpDoneListener<AccMemberModel>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<AccMemberModel> resultBean) {
                AccStudentModel student = resultBean.data.student;
                Log.w(TAG,"getMemberInfo()  onEasyHttpSuccess() AccStudentModel = "+student);
                iscreditvalid = student.iscreditvalid;
                AppConfig.iscreditvalid = iscreditvalid;
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                Log.w(TAG,"getMemberInfo()  onEasyHttpFailure() message = "+message);
                ToastUtil.showToastDefault(mContext, message);
            }
        });
    }

    /**
     * 首页预约
     */
    public void getAppointment() {
        Map<String, String> params = new HashMap();
        params.put("token", AppConfig.token);
        EasyHttp.doPost(mContext, ServerURL.GETAPPOINTMENT, params, null, OrderAppointmentModel.class, false, new EasyHttp.OnEasyHttpDoneListener<OrderAppointmentModel>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<OrderAppointmentModel> resultBean) {
                isBooking = resultBean.data.isappointment;
                if (isBooking) {
                    int type = resultBean.data.type;
                    if (type == 1) {
                        mTvTeachType.setText("电话");
                        mIvTeachType.setImageResource(R.drawable.dianhua);
                    } else if (type == 2) {
                        mTvTeachType.setText("视频");
                        mIvTeachType.setImageResource(R.drawable.shipin);
                    } else if (type == 3) {
                        mTvTeachType.setText("上门");
                        mIvTeachType.setImageResource(R.drawable.shangmen);
                    }
                    mTvTime.setText(DateUtils.formatDateLongToStringHome(resultBean.data.time));
                    mLLAppointment.setVisibility(View.VISIBLE);
                    mLLIsLogin.setVisibility(View.GONE);
                    mIvWantTeacher.setVisibility(View.GONE);
                } else {
                    mLLAppointment.setVisibility(View.GONE);
                    mLLIsLogin.setVisibility(View.VISIBLE);
                    mTvIsLogin.setText(R.string.isLogin_home);
                    mIvWantTeacher.setVisibility(View.GONE);
                }

            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                Log.w(TAG,"getAppointment()  onEasyHttpFailure() message = "+message);
                ToastUtil.showToastDefault(mContext, message);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_main_want_teacher://申请老师
                Intent intent = new Intent();
                intent.setClass(mContext, ApplyTeacherActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_homefgmS_xiaoxue:
                gradeType = 1;
                //1.检查登录
                if (AppConfig.isLogin) {
                    //2.检查是否绑定支付宝
                    /*if (!iscreditvalid) {
                        showWarningDialogBink();
                    } else {
                        // 3.（1和2）都满足后弹出选择授课弹框
                        getCoursePriceList();
                    }*/
                    getCoursePriceList();
                } else {
                    showWarningDialogLogin();
                }
                break;
            case R.id.ll_homefgmS_chuzhong:
                gradeType = 2;
                if (AppConfig.isLogin) {
                    //2.检查是否绑定支付宝
                    /*if (!iscreditvalid) {
                        showWarningDialogBink();
                    } else {
//               // 3.（1和2）都满足后弹出选择授课弹框
                        getCoursePriceList();
                    }*/
                    getCoursePriceList();
                } else {
                    showWarningDialogLogin();
                }
                break;
            case R.id.ll_homefgmS_isLogin:
                if (!AppConfig.isLogin) {
                    showWarningDialogLogin();
                }
                break;
        }
    }

    /**
     * 课程单价列表
     */
    private void getCoursePriceList() {
        EasyHttp.doPost(mContext, ServerURL.GETCOURSEPRICELIST, null, null, CoursePriceModel.class, true, new EasyHttp.OnEasyHttpDoneListener<ArrayList<CoursePriceModel>>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<ArrayList<CoursePriceModel>> resultBean) {
                coursePriceList = resultBean.data;
                if (coursePriceList.size() == 0) {
                    ToastUtil.showToastDefault(mContext, "课程单价信息获取失败，请稍后再试！");
                } else {
                    for (int i = 0; i < coursePriceList.size(); i++) {
                        prices.add(coursePriceList.get(i).getPrice() + "");
                    }
                    showPickDialogTeachType(resultBean.data);
                }
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                Log.w(TAG,"getCoursePriceList()  onEasyHttpFailure() message = "+message);
                ToastUtil.showToastDefault(mContext, message);
            }
        });
    }


    /**
     * 弹出选择对话框(授课方式)
     */
    private void showPickDialogTeachType(final List<CoursePriceModel> coursePriceList) {

        if (dialogTeachType == null)
            dialogTeachType = new TeachTypeDialog(mContext, R.style.FullHeightDialog, coursePriceList, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isHas = false;//是否失效
                    switch (v.getId()) {
                        case R.id.ll_dialogTeachType_tel:
                            for (int i = 0; i < coursePriceList.size(); i++) {
                                if (coursePriceList.get(i).type == 1) {
                                    if (AppConfig.isLogin) {
                                        if (TextUtils.isEmpty(AppConfig.userVo.address) ||
                                                TextUtils.isEmpty(AppConfig.userVo.province) ||
                                                AppConfig.userVo.sex == 0) {
                                            //弹出强制绑定详细地址对话框
                                            new SureBindingAddressDialog(mContext).show();
                                            return;
                                        }
                                    }
                                    type = 1;
                                    dialogTeachType.setSel(type);
                                    intent = new Intent(getActivity(), TeachTypeActivity.class);
                                    intent.putExtra("gradeType", gradeType);
                                    intent.putExtra("type", 1);
                                    startActivity(intent);
                                    dialogTeachType.dismiss();
                                    isHas = true;
                                }
                            }
                            if (!isHas) {
                                ToastUtil.showToastOnce(mContext, "此功能暂未开通");
                            }
                            break;
                        case R.id.ll_dialogTeachType_video:
                            for (int i = 0; i < coursePriceList.size(); i++) {
                                if (coursePriceList.get(i).type == 2) {
                                    if (AppConfig.isLogin) {
                                        if (TextUtils.isEmpty(AppConfig.userVo.address) ||
                                                TextUtils.isEmpty(AppConfig.userVo.province) ||
                                                AppConfig.userVo.sex == 0) {
                                            //弹出强制绑定详细地址对话框
                                            new SureBindingAddressDialog(mContext).show();
                                            return;
                                        }
                                    }
                                    type = 2;
                                    dialogTeachType.setSel(type);
                                    intent = new Intent(getActivity(), TeachTypeActivity.class);
                                    intent.putExtra("gradeType", gradeType);
                                    intent.putExtra("type", 2);
                                    startActivity(intent);
                                    dialogTeachType.dismiss();
                                    isHas = true;
                                }
                            }

                            if (!isHas) {
                                ToastUtil.showToastOnce(mContext, "此功能暂未开通");
                            }
                            break;
                        case R.id.ll_dialogTeachType_goHome://判断是否完善个人信息
                            for (int i = 0; i < coursePriceList.size(); i++) {
                                if (coursePriceList.get(i).type == 3) {
                                    if (AppConfig.isLogin) {
                                        if (TextUtils.isEmpty(AppConfig.userVo.address) ||
                                                TextUtils.isEmpty(AppConfig.userVo.province) ||
                                                AppConfig.userVo.sex == 0) {
                                            //弹出强制绑定详细地址对话框
                                            new SureBindingAddressDialog(mContext).show();
                                            return;
                                        }
                                    }
                                    type = 3;
                                    dialogTeachType.setSel(type);
                                    intent = new Intent(getActivity(), TeachTypeActivity.class);
                                    intent.putExtra("gradeType", gradeType);
                                    intent.putExtra("type", 3);
                                    startActivity(intent);
                                    dialogTeachType.dismiss();
                                    isHas = true;
                                }
                            }

                            if (!isHas) {
                                ToastUtil.showToastOnce(mContext, "此功能暂未开通");
                            }
                            break;
                        default:
                            break;
                    }
                }
            });

        Window window = dialogTeachType.getWindow();
        // 设置Dialog进出动画
        window.setWindowAnimations(R.style.dialogWindowAnim);
        // 设置Dialog显示位置
        window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        type = 3;
        dialogTeachType.setSel(0);
        dialogTeachType.show();
    }

    /**
     * 弹出提示框(未登录)
     */
    private void showWarningDialogLogin() {
        if (mLoginDailog == null)
            mLoginDailog = new WarningDailog(getActivity(), R.style.FullHeightDialog, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLoginDailog.dismiss();
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            });
        mLoginDailog.show();
    }

    /**
     * 弹出提示框(未绑定)
     */
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
            ToastUtil.showToastDefault(getActivity(), "请先安装支付宝客户端");
        }

    }

}
