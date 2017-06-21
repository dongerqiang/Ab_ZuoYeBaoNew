package com.ourslook.zuoyeba.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.ourslook.zuoyeba.AppConfig;
import com.ourslook.zuoyeba.BaseActivity;
import com.ourslook.zuoyeba.Constants;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ServerURL;
import com.ourslook.zuoyeba.ZuoYeBaApplication;
import com.ourslook.zuoyeba.activity.em.VideoCallNewActivity;
import com.ourslook.zuoyeba.activity.em.VoiceCallNewActivity;
import com.ourslook.zuoyeba.adapter.GridViewAdapter;
import com.ourslook.zuoyeba.event.RefreshOrderListEvent;
import com.ourslook.zuoyeba.model.OrderCostModel;
import com.ourslook.zuoyeba.model.OrderDetailModel;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.DisplayUtils;
import com.ourslook.zuoyeba.utils.ToastUtil;
import com.ourslook.zuoyeba.utils.ZuoYeBaoUtils;
import com.ourslook.zuoyeba.view.LookStudentLocationDialog;
import com.ourslook.zuoyeba.view.NoScrollGridView;
import com.ourslook.zuoyeba.view.dialog.LoadingDialog;
import com.ourslook.zuoyeba.view.dialog.LookMapDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by huangyi on 16/5/18.
 * //老师订单详情---未完成订单
 */
public class TeacherOrderDetailActivity extends BaseActivity implements View.OnClickListener {


    long orderModelId;
    @Bind(R.id.iv_listDetailAty_t_grabing_head)
    ImageView ivListDetailAtyTGrabingHead;//头像
    @Bind(R.id.tv_listDetailAty_t_grabing_name)
    TextView tvListDetailAtyTGrabingName;//姓名
    @Bind(R.id.tv_listDetailAty_t_grabing_subject)
    TextView tvListDetailAtyTGrabingSubject;//学科
    @Bind(R.id.tv_listDetailAty_t_grabing_grade)
    TextView tvListDetailAtyTGrabingGrade;//年级
    @Bind(R.id.tv_listDetailAty_t_grabing_status)
    TextView tvListDetailAtyTGrabingStatus;//状态
    @Bind(R.id.tv_listDetailAty_t_grabing_teachType)
    TextView tvListDetailAtyTGrabingTeachType;//type授课方式
    @Bind(R.id.tv_listDetailAty_t_grabing_price)
    TextView tvListDetailAtyTGrabingPrice;//单价
    @Bind(R.id.ll_listDetailAty_price)
    LinearLayout llListDetailAtyPrice;
    @Bind(R.id.tv_listDetailAty_t_grabing_date)
    TextView tvListDetailAtyTGrabingDate;//授课时间
    @Bind(R.id.tv_listDetailAty_t_grabing_time)
    TextView tvListDetailAtyTGrabingTime;
    @Bind(R.id.tv_listDetailAty_t_grabing_address)
    TextView tvListDetailAtyTGrabingAddress;//地址
    @Bind(R.id.ll_listDetailAty_t_grabing_address)
    LinearLayout llListDetailAtyTGrabingAddress;
    @Bind(R.id.ll_listDetailAty_t_grabing_pics)
    LinearLayout llListDetailAtyTGrabingPics;
    @Bind(R.id.grid_listDetailAty_t_grabing_pic)
    NoScrollGridView gridListDetailAtyTGrabingPic;//图片展示

    @Bind(R.id.ll_listDetailAty_stuNum)
    LinearLayout mLlStudentPNumber;//学生电话布局
    @Bind(R.id.tv_listDetailAty_stuNum)
    TextView mTvStudentPNumber;//学生电话

    OrderDetailModel mOrderDetailModel;
    @Bind(R.id.tv_listDetailAty_cancel)
    TextView mTvCancelOrder;//取消订单
    @Bind(R.id.tv_listDetailAty_t_doing_stop)
    TextView tvListDetailAtyTDoingStop;

    //TODO 2016年8月19日根据客户修改要求添加订单编号项及查看位置
    @Bind(R.id.tv_listDetailAty_t_location)
    TextView mTvLocation;//查看位置
    @Bind(R.id.tv_listDetailAty_t_grabing_orderNum)
    TextView mTvOrderNumber;//订单编号
    String mFromWhere;//来自哪里

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderModelId = getIntent().getLongExtra(Constants.PASS_ORDER, -1);
        mFromWhere = getIntent().getStringExtra("fromWhere");
        if (orderModelId == -1) {
            ToastUtil.showToastDefault(mContext, "参数异常");
            finish();
        }
        setContentViewWithDefaultTitle(R.layout.activity_teacher_order_detail, "订单详情");
    }

    @Override
    protected void initView() {
        setOnClickListeners(this, mTvCancelOrder, tvListDetailAtyTDoingStop, mTvLocation);
        getHttp();
    }


    /**
     * 展示用户信息
     */
    private void setInfo() {
        int cornerRadius = DisplayUtils.dp2px(31, mContext);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(cornerRadius))
                .build();
        //加载用户头像
        if (mOrderDetailModel.studentphotourl != null) {
            ZuoYeBaApplication.imageLoader.displayImage(mOrderDetailModel.studentphotourl, ivListDetailAtyTGrabingHead, options);
        }
        tvListDetailAtyTGrabingName.setText(mOrderDetailModel.studentname);
        tvListDetailAtyTGrabingSubject.setText("科目：" + mOrderDetailModel.course);
        tvListDetailAtyTGrabingGrade.setText("年级：" + mOrderDetailModel.grade);
        if (mOrderDetailModel.status == 1) {
            tvListDetailAtyTGrabingStatus.setVisibility(View.INVISIBLE);
        }
        tvListDetailAtyTGrabingStatus.setTextColor(Color.rgb(143, 195, 238));
        switch (mOrderDetailModel.status) {
            case 2:
                tvListDetailAtyTGrabingStatus.setText("已承接");
                break;
            case 3:
                tvListDetailAtyTGrabingStatus.setText("进行中");
                break;
        }
        mTvOrderNumber.setText("" + mOrderDetailModel.id + mOrderDetailModel.time);// 订单编号
//        tvListDetailAtyTGrabingStatus.setText(mOrderDetailModel.status == 1 ? "抢单中" : mOrderDetailModel.status == 2 ? "已承接" : mOrderDetailModel.status == 3 ? "进行中" : mOrderDetailModel.status == 4 ? "已结束" : "已作废");
        tvListDetailAtyTGrabingTeachType.setText(mOrderDetailModel.type == 1 ? "电话" : mOrderDetailModel.type == 2 ? "视频" : "上门");
        tvListDetailAtyTGrabingPrice.setText(mOrderDetailModel.price + "");
        tvListDetailAtyTGrabingDate.setText(ZuoYeBaoUtils.getStringDataCorrectToMin(mOrderDetailModel.teachingtime));

        // 展示问题图片
        gridListDetailAtyTGrabingPic.setNumColumns(3);
        gridListDetailAtyTGrabingPic.setAdapter(new GridViewAdapter(mContext, mOrderDetailModel.imgList, R.layout.item_imageview));
        // 处理点击大图功能
        gridListDetailAtyTGrabingPic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, PhotoViewActivity.class);
                intent.putStringArrayListExtra("PHOTOS", (ArrayList<String>) mOrderDetailModel.imgList);
                intent.putExtra("CLICKED_POSITION", position);
                startActivity(intent);
            }
        });

        //授课地址
        if (mOrderDetailModel.type == 3) {
            llListDetailAtyTGrabingAddress.setVisibility(View.VISIBLE);
            tvListDetailAtyTGrabingAddress.setText(mOrderDetailModel.address);
        } else {
            llListDetailAtyTGrabingAddress.setVisibility(View.GONE);
        }
        //开始授课按钮
        if (mOrderDetailModel.status == 2) {
            tvListDetailAtyTDoingStop.setVisibility(View.VISIBLE);
            if (mOrderDetailModel.type == 1 || mOrderDetailModel.type == 2) {
                tvListDetailAtyTDoingStop.setText("开始授课");
            } else if (mOrderDetailModel.type == 3) {
                if (mOrderDetailModel.isleave) {
                    tvListDetailAtyTDoingStop.setText("开始授课");
                } else {
                    tvListDetailAtyTDoingStop.setText("我已出发");
                }
            }
        } else if (mOrderDetailModel.status == 3) {
            tvListDetailAtyTDoingStop.setVisibility(View.VISIBLE);
            tvListDetailAtyTDoingStop.setText("结束授课");
        } else if (mOrderDetailModel.status == 1) {
            tvListDetailAtyTDoingStop.setVisibility(View.VISIBLE);
            tvListDetailAtyTDoingStop.setText("立即抢单");
        } else {

            tvListDetailAtyTDoingStop.setVisibility(View.GONE);
        }

        //取消订单
        if (mOrderDetailModel.status == 3) {
            mTvCancelOrder.setVisibility(View.GONE);
        } else if (mOrderDetailModel.status == 2) {
            mTvCancelOrder.setVisibility(View.VISIBLE);
        }

        //学生电话
        if (mOrderDetailModel.status == 3) {
            mLlStudentPNumber.setVisibility(View.GONE);
        } else if (mOrderDetailModel.status == 2) {
            if (mOrderDetailModel.type == 3) {
                mLlStudentPNumber.setVisibility(View.VISIBLE);
                mTvStudentPNumber.setText(mOrderDetailModel.studenttel);
            } else {
                mLlStudentPNumber.setVisibility(View.GONE);
            }
        }


        //非正常结束的订单
        if (mOrderDetailModel.status == 3 && mOrderDetailModel.type != 3) {
            tvListDetailAtyTDoingStop.setVisibility(View.VISIBLE);
            tvListDetailAtyTDoingStop.setText("结束授课");

        }
        if (!TextUtils.isEmpty(mFromWhere) && "TeacherMainFragment".equals(mFromWhere)) {
            if (mOrderDetailModel.grabStatus == 1) {
                tvListDetailAtyTDoingStop.setVisibility(View.VISIBLE);
                tvListDetailAtyTDoingStop.setText("确认中");
                tvListDetailAtyTDoingStop.setOnClickListener(null);
            }
        }
        if (!TextUtils.isEmpty(mFromWhere)
                && "TeacherMainFragment".equals(mFromWhere)
                && mOrderDetailModel.type == 3) {
            mTvLocation.setVisibility(View.VISIBLE);
        } else {
            mTvLocation.setVisibility(View.GONE);
        }

    }

    /**
     * 订单详情
     */
    private void getHttp() {
        LoadingDialog.showLoadingDialog(mContext);

        Map<String, String> params = new HashMap<>();
        params.put("token", AppConfig.token);
        params.put("workid", orderModelId + "");

        EasyHttp.doPost(mContext, ServerURL.GETORDERDETAIL, params, null, OrderDetailModel.class, false, new EasyHttp.OnEasyHttpDoneListener<OrderDetailModel>() {
                    @Override
                    public void onEasyHttpSuccess(ResultBean<OrderDetailModel> resultBean) {
                        LoadingDialog.dismissLoadingDialog();
                        mOrderDetailModel = resultBean.data;
                        setInfo();
                    }

                    @Override
                    public void onEasyHttpFailure(String code, String message) {
                        LoadingDialog.dismissLoadingDialog();
                        ToastUtil.showToastDefault(mContext, message);
                    }
                }
        );
    }

    /**
     * 取消订单
     */
    private void cancelOrder() {
        LoadingDialog.showLoadingDialog(mContext);
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", AppConfig.token);
        params.put("workid", orderModelId + "");
        EasyHttp.doPost(mContext, ServerURL.CANCELORDERBYID, params, null, null, false, new EasyHttp.OnEasyHttpDoneListener<Object>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<Object> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastOnce(mContext, "订单已取消");
                //刷新订单列表
                EventBus.getDefault().post(new RefreshOrderListEvent(2));
                finish();
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastOnce(mContext, message);
            }
        });
    }

    /**
     * 结束授课(仅上门)
     */
    private void endOrder() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", AppConfig.token);
        params.put("workid", orderModelId + "");
        EasyHttp.doPost(mContext, ServerURL.ENDORDERBYID, params, null, OrderCostModel.class, false, new EasyHttp.OnEasyHttpDoneListener<OrderCostModel>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<OrderCostModel> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                //ToastUtil.showToastOnce(mContext, "下课");
                //刷新订单列表
                EventBus.getDefault().post(new RefreshOrderListEvent(2));
                if (resultBean.data != null) {
                    Intent intent = new Intent(mContext, TeacherOrderEndDetailActivity.class);
                    intent.putExtra("orderCostModel", resultBean.data);
                    intent.putExtra(Constants.PASS_ORDER, Long.parseLong(orderModelId + ""));
                    startActivity(intent);
                }
                finish();
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastOnce(mContext, message);
            }
        });
    }

    /**
     * 开始上门（仅上门）
     */
    private void doorOrder() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", AppConfig.token);
        params.put("workid", orderModelId + "");
        EasyHttp.doPost(mContext, ServerURL.DOORORDERBYID, params, null, null, false, new EasyHttp.OnEasyHttpDoneListener<Object>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<Object> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                //ToastUtil.showToastOnce(mContext, "出发");
                tvListDetailAtyTDoingStop.setText("开始授课");
                mTvCancelOrder.setVisibility(View.GONE);
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastOnce(mContext, message);
            }
        });
    }

    /**
     * 开始授课（仅上门）
     */
    private void startOrder() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", AppConfig.token);
        params.put("workid", orderModelId + "");
        EasyHttp.doPost(mContext, ServerURL.STARTORDERBYID, params, null, null, false, new EasyHttp.OnEasyHttpDoneListener<Object>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<Object> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                //ToastUtil.showToastOnce(mContext, "上课");
                tvListDetailAtyTDoingStop.setText("结束授课");
                //刷新订单列表订单状态
                EventBus.getDefault().post(new RefreshOrderListEvent(0));
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastOnce(mContext, message);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_listDetailAty_cancel://取消订单
                AlertDialog dialog = new AlertDialog.Builder(mContext)
                        .setMessage("确定取消该订单？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cancelOrder();
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
                break;
            case R.id.tv_listDetailAty_t_doing_stop: //结束授课
                if (mOrderDetailModel.status == 3 && mOrderDetailModel.type != 3) {//点击结束授课
                    endWork();
                    return;
                }
                String str_ok = tvListDetailAtyTDoingStop.getText().toString().trim();
                if (str_ok.equals("我已出发")) {
                    doorOrder();
                } else if (str_ok.equals("开始授课")) {
                    if (mOrderDetailModel.type == 1 || mOrderDetailModel.type == 2) {
                        //TODO 此处需要检查权限
                        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, Constants.CODE_201);
                        } else {
                            if (mOrderDetailModel.type == 1) {//电话
                                Intent i = new Intent(mContext, VoiceCallNewActivity.class);
                                //给谁打
                                i.putExtra("to", mOrderDetailModel.studentimacc);
                                //订单id   用于 开始作业 结束id
                                i.putExtra("workid", mOrderDetailModel.id + "");
                                mContext.startActivity(i);
                                finish();
                            } else if (mOrderDetailModel.type == 2) {//视频
                                Intent i = new Intent(mContext, VideoCallNewActivity.class);
                                //给谁打
                                i.putExtra("to", mOrderDetailModel.studentimacc);
                                //订单id   用于 开始作业 结束id
                                i.putExtra("workid", mOrderDetailModel.id + "");
                                startActivity(i);
                                finish();
                            }
                        }
                    } else if (mOrderDetailModel.type == 3) {//上门
                        startOrder();
                    }
                } else if (str_ok.equals("结束授课")) {
                    AlertDialog finishDialog = new AlertDialog.Builder(mContext)
                            .setMessage("确定结束授课？")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    endOrder();
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    finishDialog.show();

                } else if (str_ok.equals("立即抢单")) {
                    grabOrder();
                }
                break;
            case R.id.tv_listDetailAty_t_location://TODO 查看位置
                new LookStudentLocationDialog(mContext, Double.parseDouble(mOrderDetailModel.studentCoordinateX), Double.parseDouble(mOrderDetailModel.studentCoordinateY))
                        .showing();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //已经获取拥有权限
            if (mOrderDetailModel.type == 1) {//电话
                Intent i = new Intent(mContext, VoiceCallNewActivity.class);
                //给谁打
                i.putExtra("to", mOrderDetailModel.studentimacc);
                //订单id   用于 开始作业 结束id
                i.putExtra("workid", mOrderDetailModel.id + "");
                mContext.startActivity(i);
                finish();
            } else if (mOrderDetailModel.type == 2) {//视频
                Intent i = new Intent(mContext, VideoCallNewActivity.class);
                //给谁打
                i.putExtra("to", mOrderDetailModel.studentimacc);
                //订单id   用于 开始作业 结束id
                i.putExtra("workid", mOrderDetailModel.id + "");
                startActivity(i);
                finish();
            }
        } else {
            ToastUtil.showToastDefault(mContext, "录音通话权限未授权---->应用----->权限管理");
        }
    }


    /**
     * 结束作业
     */
    private void endWork() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", AppConfig.token);
        params.put("workid", mOrderDetailModel.id + "");


        EasyHttp.doPost(mContext, ServerURL.ENDWORKBYID, params, null, OrderCostModel.class, false, new EasyHttp.OnEasyHttpDoneListener<OrderCostModel>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<OrderCostModel> resultBean) {
                //ToastUtil.showToastDefault(mContext, "操作成功!");
                finish();
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                ToastUtil.showToastDefault(mContext, message);
            }
        });
    }

    private void grabOrder() {
        Map<String, String> params = new HashMap<>();
        LoadingDialog.showLoadingDialog(mContext);
        params.put("token", AppConfig.token);
        params.put("workid", orderModelId + "");
        EasyHttp.doPost(mContext, ServerURL.GRABORDERBYID, params, null, null, false, new EasyHttp.OnEasyHttpDoneListener<Object>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<Object> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastDefault(mContext, "抢单成功");

                getHttp();
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastDefault(mContext, message);
            }
        });
    }

}