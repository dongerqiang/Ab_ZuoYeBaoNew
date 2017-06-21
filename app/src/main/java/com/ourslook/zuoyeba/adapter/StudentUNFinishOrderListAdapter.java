package com.ourslook.zuoyeba.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.ourslook.zuoyeba.AppConfig;
import com.ourslook.zuoyeba.Constants;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ZuoYeBaApplication;
import com.ourslook.zuoyeba.activity.OrderListActivity;
import com.ourslook.zuoyeba.activity.StudentOrderDetailFinishActivity;
import com.ourslook.zuoyeba.activity.em.VideoCallActivity;
import com.ourslook.zuoyeba.activity.em.VideoCallNewActivity;
import com.ourslook.zuoyeba.activity.em.VoiceCallActivity;
import com.ourslook.zuoyeba.activity.em.VoiceCallNewActivity;
import com.ourslook.zuoyeba.adapter.baseadapter.ListenerWithPosition;
import com.ourslook.zuoyeba.adapter.baseadapter.WyCommonAdapter;
import com.ourslook.zuoyeba.adapter.baseadapter.WyViewHolder;
import com.ourslook.zuoyeba.event.StudentNoFinishListCheckPermissEvent;
import com.ourslook.zuoyeba.model.OrderModel;
import com.ourslook.zuoyeba.utils.DisplayUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by huangyi on 16/5/16.  学生未完成订单
 */
public class StudentUNFinishOrderListAdapter extends WyCommonAdapter<OrderModel> implements ListenerWithPosition.OnClickWithPositionListener<WyViewHolder> {
    String type;

    public StudentUNFinishOrderListAdapter(Context context, List<OrderModel> data, int layoutId, String type) {
        super(context, data, layoutId);
        this.type = type;
    }

    @Override
    public void convert(WyViewHolder holder, OrderModel model) {
        holder.setTextViewText(R.id.tv_lv_myListS_name, model.studentname)
                .setTextViewText(R.id.tv_lv_myListS_subject, "科目：" + model.course)
                .setTextViewText(R.id.tv_lv_myListS_grade, "年级：" + model.grade);
        Date date = new Date(model.time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateString = sdf.format(date);
        holder.setTextViewText(R.id.tv_lv_myListS_date, dateString);
        //// 状态(1:抢单中 2:已确认教师/承接中 3:进行中 4:已结束 5:已作废)
        holder.setTextViewText(R.id.tv_lv_myListS_status, model.status == 2 ? type.equals(Constants.TEACHER) ? "已承接" : "已确认教师" : model.status == 3 ? "进行中" : model.status == 4 ? "已结束" : "已作废");
        if (model.status == 4 || model.status == 5) {//已完成订单_灰色
            holder.setTextViewTextColor(R.id.tv_lv_myListS_status, Color.rgb(102, 102, 102));
        } else {//未完成订单_浅绿色
            holder.setTextViewTextColor(R.id.tv_lv_myListS_status, Color.rgb(143, 195, 238));
        }
        TextView status = holder.getView(R.id.tv_lv_myListS_status);
        if (model.status == 4 && AppConfig.userVo.type == 1) {
            holder.setViewVisibility(R.id.tv_lv_myListS_comment, View.VISIBLE);
            TextView tvComment = holder.getView(R.id.tv_lv_myListS_comment);
            status.setGravity(Gravity.END);
            status.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            if (model.iscomment) {
                tvComment.setText("已评价");
                tvComment.setGravity(Gravity.END);
                tvComment.setSelected(false);
                tvComment.setClickable(false);
            } else {
                tvComment.setText("评价");
                tvComment.setGravity(Gravity.CENTER);
                tvComment.setSelected(true);
                tvComment.setClickable(true);

                holder.setOnClickListener(this, R.id.tv_lv_myListS_comment);
            }
        } else {
            status.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            holder.setViewVisibility(R.id.tv_lv_myListS_comment, View.GONE);
            status.setGravity(Gravity.CENTER);
        }
        if (AppConfig.userVo.type == 1 && model.isleave && model.status == 2) {
            holder.setTextViewText(R.id.tv_lv_myListS_status, "老师已出发");
        }
        int cornerRadius = DisplayUtils.dp2px(31.5f, mContext);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(cornerRadius))
                .build();
        holder.setImageViewImage(R.id.iv_lv_myListS_head, R.drawable.default_head);
        ImageView imageView = holder.getView(R.id.iv_lv_myListS_head);
        ZuoYeBaApplication.imageLoader.displayImage(model.studentphotourl, imageView, options);
        switch (model.type) {
            case 1:
                holder.setTextViewText(R.id.tv_lv_myListS_teachType, "语音")
                        .setImageViewImage(R.id.iv_lv_myListS_teachType, R.drawable.dianhua_button);

                break;
            case 2:
                holder.setTextViewText(R.id.tv_lv_myListS_teachType, "视频")
                        .setImageViewImage(R.id.iv_lv_myListS_teachType, R.drawable.shipin_button);
                break;
            case 3:
                holder.setTextViewText(R.id.tv_lv_myListS_teachType, "上门")
                        .setViewVisibility(R.id.iv_lv_myListS_teachType, View.INVISIBLE);
                break;
        }


        if (AppConfig.userVo.type == 1 && model.type != 3 && model.status != 4 && model.status != 5) {
            holder.setViewVisibility(R.id.iv_lv_myListS_teachType, View.VISIBLE);
            if (model.status == 4 || model.status == 5) {
                holder.setViewVisibility(R.id.iv_lv_myListS_teachType, View.GONE);
            } else {
                holder.setOnClickListener(this, R.id.iv_lv_myListS_teachType);
            }
        } else {
            holder.setViewVisibility(R.id.iv_lv_myListS_teachType, View.GONE);
        }
    }


    @Override
    public void onClick(View v, int position, WyViewHolder holder) {
        if (v.getId() == R.id.tv_lv_myListS_comment) {
            Intent intent = new Intent(mContext, StudentOrderDetailFinishActivity.class);
            intent.putExtra(Constants.PASS_ORDER, mData.get(position).id);
            mContext.startActivity(intent);
            return;
        }
        EventBus.getDefault().post(new StudentNoFinishListCheckPermissEvent(position));
        OrderModel model = mData.get(position);
        if (model.type == 1 || model.type == 2) {
            //TODO 此处需要检查权限
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.RECORD_AUDIO}, Constants.CODE_201);
            } else {

                if (model.type == 1) {
                    Intent i = new Intent(mContext, VoiceCallNewActivity.class);
                    //给谁打
                    i.putExtra("to", mData.get(position).teacherimacc);
                    //订单id   用于 开始作业 结束id
                    i.putExtra("workid", mData.get(position).id + "");
                    mContext.startActivity(i);
                } else if (model.type == 2) {
                    Intent i = new Intent(mContext, VideoCallNewActivity.class);
                    //给谁打
                    i.putExtra("to", mData.get(position).teacherimacc);
                    //订单id   用于 开始作业 结束id
                    i.putExtra("workid", mData.get(position).id + "");
                    mContext.startActivity(i);
                }
            }
        }


    }
}
