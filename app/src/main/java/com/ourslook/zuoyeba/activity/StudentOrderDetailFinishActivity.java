package com.ourslook.zuoyeba.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.ourslook.zuoyeba.adapter.GridViewAdapter;
import com.ourslook.zuoyeba.model.OrderCostModel;
import com.ourslook.zuoyeba.model.OrderDetailModel;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.DisplayUtils;
import com.ourslook.zuoyeba.utils.ToastUtil;
import com.ourslook.zuoyeba.utils.ZuoYeBaoUtils;
import com.ourslook.zuoyeba.view.NoScrollGridView;
import com.ourslook.zuoyeba.view.dialog.EndDialog;
import com.ourslook.zuoyeba.view.dialog.LoadingDialog;
import com.ourslook.zuoyeba.view.dialog.TeacherEndDialog;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by huangyi on 16/5/18.
 * 学生订单详情  已完成 +已作废
 */
public class StudentOrderDetailFinishActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.iv_listDetailAty_s_end_head)
    ImageView ivListDetailAtySEndHead;//头像
    @Bind(R.id.tv_listDetailAty_s_end_name)
    TextView tvListDetailAtySEndName;//姓名
    @Bind(R.id.tv_listDetailAty_s_end_subject)
    TextView tvListDetailAtySEndSubject;//学科
    @Bind(R.id.tv_listDetailAty_s_end_grade)
    TextView tvListDetailAtySEndGrade;//年级
    @Bind(R.id.tv_listDetailAty_s_end_status)
    TextView tvListDetailAtySEndStatus;//状态
    @Bind(R.id.ll_listDetailAty_s_end_teacher)
    LinearLayout llListDetailAtySEndTeacher;
    @Bind(R.id.tv_listDetailAty_s_end_teacher)
    TextView tvListDetailAtySEndTeacher;//教师姓名
    @Bind(R.id.tv_listDetailAty_s_end_teachType)
    TextView tvListDetailAtySEndTeachType;//类型
    @Bind(R.id.tv_listDetailAty_s_end_buyTime)
    TextView tvListDetailAtySEndBuyTime;//下单时间
    @Bind(R.id.tv_listDetailAty_s_end_date)
    TextView tvListDetailAtySEndDate;//授课时间
    @Bind(R.id.tv_listDetailAty_s_end_time)
    TextView tvListDetailAtySEndTime;
    @Bind(R.id.tv_listDetailAty_s_end_address)
    TextView tvListDetailAtySEndAddress;//地址
    @Bind(R.id.ll_listDetailAty_s_end_address)
    LinearLayout llListDetailAtySEndAddress;
    @Bind(R.id.ll_listDetailAty_s_grabing_pics)
    LinearLayout llListDetailAtySGrabingPics;
    @Bind(R.id.grid_listDetailAty_s_grabing_pic)
    NoScrollGridView gridListDetailAtySGrabingPic;
    @Bind(R.id.ll_listDetailAty_s_grabing_money)
    LinearLayout llListDetailAtySEndMoney;
    @Bind(R.id.tv_listDetailAty_s_end_money)
    TextView tvListDetailAtySEndMoney;//订单金额
    @Bind(R.id.tv_listDetailAty_s_end_good)
    TextView tvListDetailAtySEndGood;//好评按钮
    @Bind(R.id.tv_listDetailAty_s_end_normal)
    TextView tvListDetailAtySEndNormal;//中评按钮
    @Bind(R.id.tv_listDetailAty_s_end_bad)
    TextView tvListDetailAtySEndBad;//差评按钮
    @Bind(R.id.et_listDetailAty_s_end_comment)
    EditText etListDetailAtySEndComment;//输入评价
    @Bind(R.id.tv_listDetailAty_s_end_ok)
    TextView tvListDetailAtySEndOk;//提交评价
    @Bind(R.id.ll_listDetailAty_s_end_comment)
    LinearLayout llListDetailAtySEndComment;//是否展示评价界面

    @Bind(R.id.ll_listDetailAty_startDate)
    LinearLayout mLlStartDate;//授课开始时间布局
    @Bind(R.id.tv_listDetailAty_startDate)
    TextView mTvStartDate;//授课开始时间
    @Bind(R.id.ll_listDetailAty_endDate)
    LinearLayout mLlFinishDate;//授课结束时间布局
    @Bind(R.id.tv_listDetailAty_endDate)
    TextView mTvFinishDate;//授课结束时间
    @Bind(R.id.ll_listDetailAty_allDate)
    LinearLayout mLlAllDate;//授课总时长布局
    @Bind(R.id.tv_listDetailAty_allDate)
    TextView mTvAllDate;//授课总时长

    @Bind(R.id.ll_listDetailAty_s_end_comment1)
    LinearLayout mLlAlreadyComment;//已评价布局
    @Bind(R.id.tv_listDetailAty_s_end_good1)
    TextView mTvAlreadyGood;//已评价——好评
    @Bind(R.id.tv_listDetailAty_s_end_normal1)
    TextView mTvAlreadyNormal;//已评价——中评
    @Bind(R.id.tv_listDetailAty_s_end_bad1)
    TextView mTvAlreadyBad;//已评价——差评
    @Bind(R.id.tv_listDetailAty_s_end_comment)
    TextView mTvAlreadyComment;//已评价——评价内容

    long orderModelId = -1;

    OrderDetailModel oorderDetailModel;

    OrderCostModel orderCostModel;// 订单价格
    EndDialog endDialog;// 授课价格对话框


    int index = -1;

    //TODO 2016年8月19日根据客户修改要求添加订单编号项
    @Bind(R.id.tv_listDetailAty_s_end_orderNum)
    TextView mTvOrderNumber;//订单编号
    //音频播放器
    private MediaPlayer mMediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderModelId = getIntent().getLongExtra(Constants.PASS_ORDER, -1);
        orderCostModel = (OrderCostModel) getIntent().getSerializableExtra("orderCostModel");

        if (orderModelId == -1) {
            ToastUtil.showToastDefault(mContext, "参数异常");
            finish();
        }
        setContentViewWithDefaultTitle(R.layout.activity_student_finish_order_detail, "订单详情");
    }

    @Override
    protected void initView() {

        if (orderCostModel != null) {
            mMediaPlayer = MediaPlayer.create(this, R.raw.end_dialog);
            try {
                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                }
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                showCostDialog(orderCostModel.duration, orderCostModel.costmoney, orderCostModel.redmoney, orderCostModel.money, orderCostModel.servicetel);
                //new TeacherEndDialog(this, orderCostModel).show();
            } catch (Exception e) {
                Log.e("--", "异常=" + e.toString());
                e.printStackTrace();
            }

        }
        setOnClickListeners(this, tvListDetailAtySEndGood, tvListDetailAtySEndNormal, tvListDetailAtySEndBad, tvListDetailAtySEndOk);

        getHttp();
    }


    /**
     * 授课价格对话框
     *
     * @param time
     * @param price
     * @param red
     * @param realPrice
     * @param stel
     */
    private void showCostDialog(int time, double price, double red, double realPrice, String stel) {
        endDialog = new EndDialog(this, R.style.FullHeightDialog, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endDialog.dismiss();
            }
        });
        endDialog.setText(time, price, red, realPrice, stel);
        if (!endDialog.isShowing())
            endDialog.show();
    }

    /**
     * 展示数据
     */
    private void setInfo() {
        int cornerRadius = DisplayUtils.dp2px(31, mContext);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(cornerRadius))
                .build();
        //加载用户头像
        if (oorderDetailModel.studentphotourl != null) {
            ZuoYeBaApplication.imageLoader.displayImage(oorderDetailModel.studentphotourl, ivListDetailAtySEndHead, options);
        } else {
            ivListDetailAtySEndHead.setImageResource(R.drawable.default_head);
        }
        mTvOrderNumber.setText("" + oorderDetailModel.id + oorderDetailModel.time);//TODO 订单编号
        tvListDetailAtySEndName.setText(oorderDetailModel.studentname);
        tvListDetailAtySEndSubject.setText("科目：" + oorderDetailModel.course);
        tvListDetailAtySEndGrade.setText("年级：" + oorderDetailModel.grade);

        if (oorderDetailModel.status == 4) {//已结束
            tvListDetailAtySEndMoney.setText(String.format("￥%.2f", oorderDetailModel.money));
            llListDetailAtySEndMoney.setVisibility(View.VISIBLE);
            tvListDetailAtySEndTeacher.setText(oorderDetailModel.teachername);
            llListDetailAtySEndTeacher.setVisibility(View.VISIBLE);
            llListDetailAtySEndComment.setVisibility(oorderDetailModel.iscomment ? View.GONE : View.VISIBLE);
            if (oorderDetailModel.iscomment) {
                mLlAlreadyComment.setVisibility(View.VISIBLE);
                mTvAlreadyComment.setText(oorderDetailModel.commentcontent);
                // 评价类型ID1好评;2中评;3差评
                if (oorderDetailModel.commentid == 1) {
                    mTvAlreadyGood.setSelected(true);
                    mTvAlreadyNormal.setSelected(false);
                    mTvAlreadyBad.setSelected(false);
                } else if (oorderDetailModel.commentid == 2) {
                    mTvAlreadyGood.setSelected(false);
                    mTvAlreadyNormal.setSelected(true);
                    mTvAlreadyBad.setSelected(false);
                } else if (oorderDetailModel.commentid == 3) {
                    mTvAlreadyGood.setSelected(false);
                    mTvAlreadyNormal.setSelected(false);
                    mTvAlreadyBad.setSelected(true);
                } else {
                    mTvAlreadyGood.setSelected(false);
                    mTvAlreadyNormal.setSelected(false);
                    mTvAlreadyBad.setSelected(false);
                }
            } else {
                mLlAlreadyComment.setVisibility(View.GONE);
            }
        } else if (oorderDetailModel.status == 5) {//已作废
            tvListDetailAtySEndTeacher.setText("");
            llListDetailAtySEndTeacher.setVisibility(View.GONE);
            tvListDetailAtySEndMoney.setText("");
            llListDetailAtySEndMoney.setVisibility(View.GONE);
            llListDetailAtySEndComment.setVisibility(View.GONE);

            mLlAlreadyComment.setVisibility(View.GONE);
        }

        if (oorderDetailModel.type == 1) {
            tvListDetailAtySEndTeachType.setText("电话");
            tvListDetailAtySEndAddress.setText("");
            llListDetailAtySEndAddress.setVisibility(View.GONE);

        } else if (oorderDetailModel.type == 2) {
            tvListDetailAtySEndTeachType.setText("视频");
            tvListDetailAtySEndAddress.setText("");
            llListDetailAtySEndAddress.setVisibility(View.GONE);
        } else if (oorderDetailModel.type == 3) {
            tvListDetailAtySEndTeachType.setText("上门");
            tvListDetailAtySEndAddress.setText(oorderDetailModel.address);
            llListDetailAtySEndAddress.setVisibility(View.VISIBLE);

            if (oorderDetailModel.status == 4) {//已结束
                mLlStartDate.setVisibility(View.VISIBLE);
                mLlFinishDate.setVisibility(View.VISIBLE);
                mLlAllDate.setVisibility(View.VISIBLE);
            } else if (oorderDetailModel.status == 5) {//已作废
                mLlStartDate.setVisibility(View.GONE);
                mLlFinishDate.setVisibility(View.GONE);
                mLlAllDate.setVisibility(View.GONE);
            }
            mTvStartDate.setText(ZuoYeBaoUtils.getStringDataCorrectToMin(oorderDetailModel.teachingtimestart));
            mTvFinishDate.setText(ZuoYeBaoUtils.getStringDataCorrectToMin(oorderDetailModel.teachingtimeend));
            mTvAllDate.setText(oorderDetailModel.duration + "min");
        } else {
            tvListDetailAtySEndTeachType.setText("");
            tvListDetailAtySEndAddress.setText("");
            llListDetailAtySEndAddress.setVisibility(View.GONE);
        }
        tvListDetailAtySEndBuyTime.setText(ZuoYeBaoUtils.getStringDataCorrectToDay(oorderDetailModel.time));
        tvListDetailAtySEndDate.setText(ZuoYeBaoUtils.getStringDataCorrectToMin(oorderDetailModel.teachingtime));

        //此处展示问题图片
        gridListDetailAtySGrabingPic.setNumColumns(3);
        gridListDetailAtySGrabingPic.setAdapter(new GridViewAdapter(mContext, oorderDetailModel.imgList, R.layout.item_imageview));

        tvListDetailAtySEndStatus.setText(oorderDetailModel.status == 2 ? "已确认教师" : oorderDetailModel.status == 3 ? "进行中" : oorderDetailModel.status == 4 ? "已结束" : "已作废");


    }

    /**
     * 查询订单详情
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
                oorderDetailModel = resultBean.data;
                setInfo();
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                ToastUtil.showToastDefault(mContext, message);
                LoadingDialog.dismissLoadingDialog();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_listDetailAty_s_end_good:
                index = 0;
                changeClickStatus();
                break;
            case R.id.tv_listDetailAty_s_end_normal:
                index = 1;
                changeClickStatus();
                break;
            case R.id.tv_listDetailAty_s_end_bad:
                index = 2;
                changeClickStatus();
                break;
            case R.id.tv_listDetailAty_s_end_ok://提交评价
                commitComment();
                break;
        }
    }

    /**
     * 改变选择评价状态
     */
    private void changeClickStatus() {
        switch (index) {
            case 0:
                tvListDetailAtySEndGood.setSelected(true);
                tvListDetailAtySEndNormal.setSelected(false);
                tvListDetailAtySEndBad.setSelected(false);
                break;
            case 1:
                tvListDetailAtySEndGood.setSelected(false);
                tvListDetailAtySEndNormal.setSelected(true);
                tvListDetailAtySEndBad.setSelected(false);
                break;
            case 2:
                tvListDetailAtySEndGood.setSelected(false);
                tvListDetailAtySEndNormal.setSelected(false);
                tvListDetailAtySEndBad.setSelected(true);
                break;
        }

    }

    /**
     * 提交评价
     */
    private void commitComment() {
        if (index == -1) {
            ToastUtil.showToastDefault(mContext, "请选择评价");
            return;
        } else {
            LoadingDialog.showLoadingDialog(mContext);
            Map<String, String> params = new HashMap<>();
            params.put("token", AppConfig.token);
            params.put("workid", "" + oorderDetailModel.id);
            params.put("commentid", "" + (index + 1));
            if (etListDetailAtySEndComment.getText().toString().trim().length() != 0) {
                params.put("commentcontent", "" + etListDetailAtySEndComment.getText().toString().trim());
            }
            EasyHttp.doPost(mContext, ServerURL.COMMENTWORKBYID, params, null, null, false, new EasyHttp.OnEasyHttpDoneListener<Object>() {
                @Override
                public void onEasyHttpSuccess(ResultBean<Object> resultBean) {
                    LoadingDialog.dismissLoadingDialog();
                    ToastUtil.showToastDefault(mContext, "评价成功!");
                    getHttp();
                    //llListDetailAtySEndComment.setVisibility(View.GONE);

                }

                @Override
                public void onEasyHttpFailure(String code, String message) {
                    LoadingDialog.dismissLoadingDialog();
                    ToastUtil.showToastDefault(mContext, message);
                }
            });
        }
    }
}
