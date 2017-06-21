package com.ourslook.zuoyeba.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

/**
 * 老师订单详情---已完成订单
 * Created by DuanLu on 2016/5/20.
 */
public class TeacherOrderEndDetailActivity extends BaseActivity {
    @Bind(R.id.iv_listDetailAty_t_end_head)
    ImageView mIvEndHead;//图像
    @Bind(R.id.tv_listDetailAty_t_end_name)
    TextView mTvEndName;//姓名
    @Bind(R.id.tv_listDetailAty_t_end_subject)
    TextView mTvEndSubject;//科目
    @Bind(R.id.tv_listDetailAty_t_end_grade)
    TextView mTvEndGrade;//年级
    @Bind(R.id.tv_listDetailAty_t_end_status)
    TextView mTvEndStatus;//状态
    @Bind(R.id.tv_listDetailAty_t_end_teachType)
    TextView mTvEndTeachType;//授课方式
    @Bind(R.id.ll_listDetailAty_price)
    LinearLayout mLlPrice;//单价布局
    @Bind(R.id.tv_listDetailAty_t_end_price)
    TextView mTvEndPrice;//单价
    @Bind(R.id.tv_listDetailAty_t_end_date)
    TextView mTvEndData;//结束日期
    @Bind(R.id.tv_listDetailAty_t_end_time)
    TextView mTvEndTime;//结束时间
    @Bind(R.id.ll_listDetailAty_t_end_address)
    LinearLayout mLlEndAddress;//地址布局
    @Bind(R.id.tv_listDetailAty_t_end_address)
    TextView mTvEndAddress;//地址
    @Bind(R.id.ll_listDetailAty_t_end_pics)
    LinearLayout mLlEndPics;//问题照片布局
    @Bind(R.id.grid_listDetailAty_t_end_pic)
    NoScrollGridView mNsgEndPic;//问题照片
    @Bind(R.id.line_listDetailAty_t_end_money)
    View mVENdMoneyLine;//实际收益分割线
    @Bind(R.id.ll_listDetailAty_t_end_money)
    LinearLayout mLlEndMoney;//实际收益布局
    @Bind(R.id.tv_listDetailAty_t_end_money)
    TextView mTvEndMoney;//实际收益

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

    long orderModelId;//订单id
    OrderDetailModel mOrderDetailModel;

    //TODO 2016年8月19日根据客户修改要求添加订单编号项
    @Bind(R.id.tv_listDetailAty_t_end_orderNum)
    TextView mTvOrderNumber;//订单编号
    @Bind(R.id.ll_listDetailAty_t_end_comment1)
    LinearLayout mLlAlreadyComment;//已评价布局
    @Bind(R.id.tv_listDetailAty_t_end_good1)
    TextView mTvAlreadyGood;//已评价——好评
    @Bind(R.id.tv_listDetailAty_t_end_normal1)
    TextView mTvAlreadyNormal;//已评价——中评
    @Bind(R.id.tv_listDetailAty_t_end_bad1)
    TextView mTvAlreadyBad;//已评价——差评
    @Bind(R.id.tv_listDetailAty_t_end_comment)
    TextView mTvAlreadyComment;//已评价——评价内容
    OrderCostModel orderCostModel;// 结束授课弹框内容实体

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
        setContentViewWithDefaultTitle(R.layout.activity_teacher_order_end_detail, "订单详情");
//        setContentViewWithDefaultTitle(R.layout.activity_teacher_order_end_detail, "订单详情");
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
            } catch (Exception e) {
                Log.e("--", "异常=" + e.toString());
                e.printStackTrace();
            }
            new TeacherEndDialog(this, orderCostModel).show();
        }
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
            ZuoYeBaApplication.imageLoader.displayImage(mOrderDetailModel.studentphotourl, mIvEndHead, options);
        } else {
            mIvEndHead.setImageResource(R.drawable.default_head);
        }
        mTvOrderNumber.setText("" + mOrderDetailModel.id + mOrderDetailModel.time);//TODO 订单编号
        mTvEndName.setText(mOrderDetailModel.studentname);
        mTvEndSubject.setText("科目：" + mOrderDetailModel.course);
        mTvEndGrade.setText("年级：" + mOrderDetailModel.grade);

        //订单状态
        if (mOrderDetailModel.status == 4) {
            mTvEndStatus.setText("已结束");
            mLlEndMoney.setVisibility(View.VISIBLE);
            mVENdMoneyLine.setVisibility(View.VISIBLE);

            if (mOrderDetailModel.commentid != 0 ||
                    !TextUtils.isEmpty(mOrderDetailModel.commentcontent)) {
                mLlAlreadyComment.setVisibility(View.VISIBLE);
                mTvAlreadyComment.setText(mOrderDetailModel.commentcontent);
                // 评价类型ID1好评;2中评;3差评
                if (mOrderDetailModel.commentid == 1) {
                    mTvAlreadyGood.setSelected(true);
                    mTvAlreadyNormal.setSelected(false);
                    mTvAlreadyBad.setSelected(false);
                } else if (mOrderDetailModel.commentid == 2) {
                    mTvAlreadyGood.setSelected(false);
                    mTvAlreadyNormal.setSelected(true);
                    mTvAlreadyBad.setSelected(false);
                } else if (mOrderDetailModel.commentid == 3) {
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
        } else if (mOrderDetailModel.status == 5) {
            mTvEndStatus.setText("已作废");
            mLlEndMoney.setVisibility(View.GONE);
            mVENdMoneyLine.setVisibility(View.GONE);

            mLlAlreadyComment.setVisibility(View.GONE);
        }

        if (mOrderDetailModel.type == 1) {
            mTvEndTeachType.setText("电话");
            mTvEndAddress.setText("");
            mLlEndAddress.setVisibility(View.GONE);
        } else if (mOrderDetailModel.type == 2) {
            mTvEndTeachType.setText("视频");
            mTvEndAddress.setText("");
            mLlEndAddress.setVisibility(View.GONE);
        } else if (mOrderDetailModel.type == 3) {
            mTvEndTeachType.setText("上门");
            mTvEndAddress.setText(mOrderDetailModel.address);
            mLlEndAddress.setVisibility(View.VISIBLE);

            if (mOrderDetailModel.status == 4) {//已结束
                mLlStartDate.setVisibility(View.VISIBLE);
                mLlFinishDate.setVisibility(View.VISIBLE);
                mLlAllDate.setVisibility(View.VISIBLE);
            } else if (mOrderDetailModel.status == 5) {//已作废
                mLlStartDate.setVisibility(View.GONE);
                mLlFinishDate.setVisibility(View.GONE);
                mLlAllDate.setVisibility(View.GONE);
            }
            mTvStartDate.setText(ZuoYeBaoUtils.getStringDataCorrectToMin(mOrderDetailModel.teachingtimestart));
            mTvFinishDate.setText(ZuoYeBaoUtils.getStringDataCorrectToMin(mOrderDetailModel.teachingtimeend));
            mTvAllDate.setText(mOrderDetailModel.duration + "min");
        } else {
            mTvEndTeachType.setText("");
            mTvEndAddress.setText("");
            mLlEndAddress.setVisibility(View.GONE);
        }

        mTvEndData.setText(ZuoYeBaoUtils.getStringDataCorrectToMin(mOrderDetailModel.teachingtime));
        mTvEndPrice.setText(String.format("%.2f", mOrderDetailModel.price));
        mTvEndMoney.setText(String.format("￥%.2f", mOrderDetailModel.money));
        //展示问题图片
        mNsgEndPic.setNumColumns(3);
        mNsgEndPic.setAdapter(new GridViewAdapter(mContext, mOrderDetailModel.imgList, R.layout.item_imageview));
        // 处理点击大图功能
        mNsgEndPic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, PhotoViewActivity.class);
                intent.putStringArrayListExtra("PHOTOS", (ArrayList<String>) mOrderDetailModel.imgList);
                intent.putExtra("CLICKED_POSITION", position);
                startActivity(intent);
            }
        });

//        long teachingtime = mOrderDetailModel.teachingtime;
//        String d = DateUtils.StrDateToSortStrDate(teachingtime);
//        String t = DateUtils.formateDateLongToStringOnlyTime2(teachingtime);

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
}
