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
import android.util.Log;
import android.view.View;
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
import com.ourslook.zuoyeba.event.NotificationRefreshStudentNoFinishListOrDetail;
import com.ourslook.zuoyeba.model.OrderCostModel;
import com.ourslook.zuoyeba.model.OrderDetailModel;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.DisplayUtils;
import com.ourslook.zuoyeba.utils.ToastUtil;
import com.ourslook.zuoyeba.utils.ZuoYeBaoUtils;
import com.ourslook.zuoyeba.view.NoScrollGridView;
import com.ourslook.zuoyeba.view.dialog.LoadingDialog;
import com.ourslook.zuoyeba.view.dialog.LookMapDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by huangyi on 16/5/16.
 * 学生订单详情   进行中(已确认教师)
 */
public class StudentOrderDetailNoFinishActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.iv_listDetailAty_s_doing_head)
    ImageView ivListDetailAtySDoingHead;//头像
    @Bind(R.id.tv_listDetailAty_s_doing_name)
    TextView tvListDetailAtySDoingName;//姓名
    @Bind(R.id.tv_listDetailAty_s_doing_subject)
    TextView tvListDetailAtySDoingSubject;//科目
    @Bind(R.id.tv_listDetailAty_s_doing_grade)
    TextView tvListDetailAtySDoingGrade;//年级
    @Bind(R.id.tv_listDetailAty_s_doing_status)
    TextView tvListDetailAtySDoingStatus;//状态
    @Bind(R.id.tv_listDetailAty_s_doing_teacher)
    TextView tvListDetailAtySDoingTeacher;//教师
    @Bind(R.id.tv_listDetailAty_s_doing_location)
    TextView tvListDetailAtySDoingLocation;//查看位置
    @Bind(R.id.tv_listDetailAty_s_doing_teachType)
    TextView tvListDetailAtySDoingTeachType;//授课方式
    @Bind(R.id.tv_listDetailAty_s_doing_buyTime)
    TextView tvListDetailAtySDoingBuyTime;//下单时间
    @Bind(R.id.tv_listDetailAty_s_doing_date)
    TextView tvListDetailAtySDoingDate;//授课日期
    @Bind(R.id.tv_listDetailAty_s_doing_time)
    TextView tvListDetailAtySDoingTime;//授课时间
    @Bind(R.id.tv_listDetailAty_s_doing_address)
    TextView tvListDetailAtySDoingAddress;//授课地址
    @Bind(R.id.ll_listDetailAty_s_doing_address)
    LinearLayout llListDetailAtySDoingAddress;//授课地址条目
    @Bind(R.id.ll_listDetailAty_s_grabing_pics)
    LinearLayout llListDetailAtySGrabingPics;//问题照片条目
    @Bind(R.id.grid_listDetailAty_s_grabing_pic)
    NoScrollGridView gridListDetailAtySGrabingPic;//问题照片展示
    @Bind(R.id.tv_listDetailAty_s_doing_cancel)
    TextView tvListDetailAtySDoingCancel;//取消订单
    @Bind(R.id.tv_listDetailAty_s_doing_ok)
    TextView tvListDetailAtySDoingOk;//开始作业


    private long orderModelId;

    private OrderDetailModel orderDetailModel;

    //TODO 2016年8月19日根据客户修改要求添加订单编号项
    @Bind(R.id.tv_listDetailAty_s_doing_orderNum)
    TextView mTvOrderNumber;//订单编号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderModelId = getIntent().getLongExtra(Constants.PASS_ORDER, -1);
        if (orderModelId == -1) {
            ToastUtil.showToastDefault(mContext, "参数异常");
            finish();
        }
        setContentViewWithDefaultTitle(R.layout.activity_student_doing_order_detail, "订单详情");
        ButterKnife.bind(this);

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initView() {
        getHttp();
    }


    private void setInfo() {
        int cornerRadius = DisplayUtils.dp2px(31, mContext);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(cornerRadius))
                .build();
        //加载用户头像
        if (orderDetailModel.studentphotourl != null) {
            ZuoYeBaApplication.imageLoader.displayImage(orderDetailModel.studentphotourl, ivListDetailAtySDoingHead, options);
        }
        mTvOrderNumber.setText("" + orderDetailModel.id + orderDetailModel.time);//TODO 订单编号
        tvListDetailAtySDoingName.setText(orderDetailModel.studentname);
        tvListDetailAtySDoingSubject.setText("科目：" + orderDetailModel.course);
        tvListDetailAtySDoingGrade.setText("年级：" + orderDetailModel.grade);
        tvListDetailAtySDoingTeacher.setText(orderDetailModel.teachername);
//        状态(1:抢单中 2:已确认教师/承接中 3:进行中 4:已结束 5:已作废)
        tvListDetailAtySDoingStatus.setTextColor(Color.rgb(143, 195, 238));
        switch (orderDetailModel.status) {
            case 2:
                tvListDetailAtySDoingStatus.setText("已确认教师");
                break;
            case 3:
                tvListDetailAtySDoingStatus.setText("进行中");
                break;
        }
//        tvListDetailAtySDoingStatus.setText(orderDetailModel.status == 2 ? "已确认教师" : orderDetailModel.status == 3 ? "进行中" : orderDetailModel.status == 4 ? "已结束" : "已作废");
        if (orderDetailModel.type == 1) {
            tvListDetailAtySDoingTeachType.setText("电话");
            tvListDetailAtySDoingAddress.setText("");
            llListDetailAtySDoingAddress.setVisibility(View.GONE);
        } else if (orderDetailModel.type == 2) {
            tvListDetailAtySDoingTeachType.setText("视频");
            tvListDetailAtySDoingAddress.setText("");
            llListDetailAtySDoingAddress.setVisibility(View.GONE);
        } else if (orderDetailModel.type == 3) {
            tvListDetailAtySDoingTeachType.setText("上门");
            tvListDetailAtySDoingAddress.setText(orderDetailModel.address);
            llListDetailAtySDoingAddress.setVisibility(View.VISIBLE);
        }
        if (orderDetailModel.status == 2) {
            tvListDetailAtySDoingStatus.setText("已确认教师");
            tvListDetailAtySDoingCancel.setVisibility(View.VISIBLE);
            tvListDetailAtySDoingLocation.setVisibility(View.VISIBLE);
            if (orderDetailModel.type == 1) {
                tvListDetailAtySDoingOk.setText("开始作业");
                tvListDetailAtySDoingOk.setVisibility(View.VISIBLE);
            } else if (orderDetailModel.type == 2) {
                tvListDetailAtySDoingOk.setText("开始作业");
                tvListDetailAtySDoingOk.setVisibility(View.VISIBLE);
            } else if (orderDetailModel.type == 3) {
                tvListDetailAtySDoingOk.setText("取消订单");
                tvListDetailAtySDoingCancel.setVisibility(View.INVISIBLE);
                tvListDetailAtySDoingOk.setVisibility(View.VISIBLE);
                //已出发 (有定位)
                tvListDetailAtySDoingLocation.setVisibility(orderDetailModel.isleave && orderDetailModel.type == 3 ? View.VISIBLE : View.GONE);
            }
        } else if (orderDetailModel.status == 3 && orderDetailModel.type != 3) {
            tvListDetailAtySDoingStatus.setText("进行中");
            tvListDetailAtySDoingOk.setText("结束授课");
            tvListDetailAtySDoingCancel.setVisibility(View.INVISIBLE);
            tvListDetailAtySDoingOk.setVisibility(View.VISIBLE);

        }
        if (orderDetailModel.type != 3) {
            tvListDetailAtySDoingLocation.setVisibility(View.GONE);
        }

        tvListDetailAtySDoingBuyTime.setText(ZuoYeBaoUtils.getStringDataCorrectToDay(orderDetailModel.time));
        tvListDetailAtySDoingDate.setText(ZuoYeBaoUtils.getStringDataCorrectToMin(orderDetailModel.teachingtime));

        //TODO 此处需要展示问题图片
        gridListDetailAtySGrabingPic.setNumColumns(3);
        gridListDetailAtySGrabingPic.setAdapter(new GridViewAdapter(mContext, orderDetailModel.imgList, R.layout.item_imageview));
        setOnClickListeners(this, tvListDetailAtySDoingLocation, tvListDetailAtySDoingCancel, tvListDetailAtySDoingOk);

        if (orderDetailModel.isleave && orderDetailModel.status == 2) {
            tvListDetailAtySDoingStatus.setText("老师已出发");
        }
    }

    /**
     * 获取订单详情
     */
    private void getHttp() {
        LoadingDialog.showLoadingDialog(mContext);
        Map<String, String> params = new HashMap();
        params.put("token", AppConfig.token);
        params.put("workid", orderModelId + "");
        EasyHttp.doPost(mContext, ServerURL.GETWORKDETAIL, params, null, OrderDetailModel.class, false, new EasyHttp.OnEasyHttpDoneListener<OrderDetailModel>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<OrderDetailModel> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                orderDetailModel = resultBean.data;
                Log.d("--", "orderDetailModel.teachername=" + orderDetailModel.teachername);
                setInfo();
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                ToastUtil.showToastDefault(mContext, message);
                LoadingDialog.dismissLoadingDialog();
            }
        });
    }

    @Subscribe
    public void onEvent(NotificationRefreshStudentNoFinishListOrDetail event) {
        if (event.type.equals(Constants.TYPE)) {
            getHttp();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_listDetailAty_s_doing_location://TODO 定位 展示学生和老师的位置    老师的位置图标分男女
                new LookMapDialog(mContext, orderDetailModel).showing();
                break;
            case R.id.tv_listDetailAty_s_doing_cancel://取消订单
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
            case R.id.tv_listDetailAty_s_doing_ok://开始作业
                if (orderDetailModel.status == 3 && orderDetailModel.type != 3) {//进行中的订单
                    endWork();
                    return;
                }
                startWork();
//                finish(); //如果从结束的订单中返回会有BUG
                break;
        }
    }

    /**
     * 结束作业
     */
    private void endWork() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", AppConfig.token);
        params.put("workid", orderDetailModel.id + "");


        EasyHttp.doPost(mContext, ServerURL.ENDWORKBYID, params, null, OrderCostModel.class, false, new EasyHttp.OnEasyHttpDoneListener<OrderCostModel>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<OrderCostModel> resultBean) {
                ToastUtil.showToastDefault(mContext, "操作成功!");
                finish();
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                ToastUtil.showToastDefault(mContext, message);
            }
        });
    }

    /**
     * 取消订单
     */
    private void cancelOrder() {
        //TODO 跳转取消订单界面   需要传递id
        Intent intent = new Intent(this, CancelOrderActivity.class);
        intent.putExtra(Constants.PASS_ORDER, orderDetailModel.id);
        startActivity(intent);
    }

    /**
     * 开始工作
     */
    private void startWork() {
        String s = tvListDetailAtySDoingOk.getText().toString().trim();
        if (s.equals("开始作业")) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, Constants.CODE_201);
            } else {
                //deq add start
                /*if (!EMClient.getInstance().isConnected()){
                    Toast.makeText(mContext, R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
                    return;
                }*/
                //deq add end
                if (orderDetailModel.type == 1) {

                    //已经拥有权限
                    Intent i = new Intent(mContext, VoiceCallNewActivity.class);
                    //给谁打
                    i.putExtra("to", orderDetailModel.teacherimacc);
                    //订单id   用于 开始作业 结束id
                    i.putExtra("workid", orderDetailModel.id + "");
                    startActivity(i);

                } else if (orderDetailModel.type == 2) {
                    Intent i = new Intent(mContext, VideoCallNewActivity.class);
                    //给谁打
                    i.putExtra("to", orderDetailModel.teacherimacc);
                    //订单id   用于 开始作业 结束id
                    i.putExtra("workid", orderDetailModel.id + "");
                    mContext.startActivity(i);
                }
                finish();
            }
        } else if (s.equals("取消订单"))

        {
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
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //已经获取拥有权限
            if (orderDetailModel.type == 1) {
                //已经拥有权限
                Intent i = new Intent(mContext, VoiceCallNewActivity.class);
                //给谁打
                i.putExtra("to", orderDetailModel.teacherimacc);
                //订单id   用于 开始作业 结束id
                i.putExtra("workid", orderDetailModel.id + "");
                startActivity(i);

            } else if (orderDetailModel.type == 2) {
                Intent i = new Intent(mContext, VideoCallNewActivity.class);
                //给谁打
                i.putExtra("to", orderDetailModel.teacherimacc);
                //订单id   用于 开始作业 结束id
                i.putExtra("workid", orderDetailModel.id + "");
                mContext.startActivity(i);
            }
            finish();
        } else {
            ToastUtil.showToastDefault(mContext, "录音通话权限未授权---->应用----->权限管理");
        }
    }


}
