package com.ourslook.zuoyeba.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.ourslook.zuoyeba.AppConfig;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ZuoYeBaApplication;
import com.ourslook.zuoyeba.adapter.baseadapter.ListenerWithPosition;
import com.ourslook.zuoyeba.adapter.baseadapter.WyCommonAdapter;
import com.ourslook.zuoyeba.adapter.baseadapter.WyViewHolder;
import com.ourslook.zuoyeba.event.GetOrderEvent;
import com.ourslook.zuoyeba.model.OrderModel;
import com.ourslook.zuoyeba.utils.DisplayUtils;
import com.ourslook.zuoyeba.utils.ZuoYeBaoUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by huangyi on 16/5/19.
 * 抢单适配器
 */
public class GetOrderAdapter extends WyCommonAdapter<OrderModel> implements ListenerWithPosition.OnClickWithPositionListener<WyViewHolder> {
    public GetOrderAdapter(Context context, List data, int layoutId) {
        super(context, data, layoutId);
    }

    @Override
    public void convert(WyViewHolder holder, OrderModel mOrderModel) {
        int cornerRadius = DisplayUtils.dp2px(31
                , mContext);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(cornerRadius))
                .build();

        holder.setImageViewImage(R.id.iv_lv_homeFgmT_head, R.drawable.default_head);
        //加载用户头像
        if (mOrderModel.studentphotourl != null) {
            if (mOrderModel.studentphotourl.length() > 0) {
                ZuoYeBaApplication.imageLoader.displayImage(mOrderModel.studentphotourl, (ImageView) holder.getView(R.id.iv_lv_homeFgmT_head), options);
            }
        }

        holder.setTextViewText(R.id.tv_lv_homeFgmT_name, mOrderModel.studentname);
        holder.setTextViewText(R.id.tv_lv_homeFgmT_subject, "科目：" + mOrderModel.course);
        holder.setTextViewText(R.id.tv_lv_homeFgmT_grade, "年级：" + mOrderModel.grade);
        TextView grab = holder.getView(R.id.tv_lv_homeFgmT_grab);
        if (mOrderModel.grabStatus == 1) {
            holder.setTextViewText(R.id.tv_lv_homeFgmT_grab, "确认中");
            grab.setOnClickListener(null);
        } else {
            holder.setTextViewText(R.id.tv_lv_homeFgmT_grab, "立即抢单");
            holder.setOnClickListener(this, R.id.tv_lv_homeFgmT_grab);
        }
        holder.setTextViewText(R.id.tv_lv_homeFgmT_teachType, mOrderModel.type == 1 ? "电话" : mOrderModel.type == 2 ? "视频" : "上门");
        holder.setTextViewText(R.id.tv_lv_homeFgmT_date, ZuoYeBaoUtils.getStringData(mOrderModel.time).substring(0, ZuoYeBaoUtils.getStringData(mOrderModel.time).length() - 3));
    }

    @Override
    public void onClick(View v, int position, WyViewHolder holder) {
        EventBus.getDefault().post(new GetOrderEvent(position, holder));

    }
}
