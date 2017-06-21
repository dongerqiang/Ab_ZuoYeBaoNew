package com.ourslook.zuoyeba.adapter;

import android.content.Context;
import android.view.View;

import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.activity.CancelOrderActivity;
import com.ourslook.zuoyeba.adapter.baseadapter.ListenerWithPosition;
import com.ourslook.zuoyeba.adapter.baseadapter.WyCommonAdapter;
import com.ourslook.zuoyeba.adapter.baseadapter.WyViewHolder;
import com.ourslook.zuoyeba.event.CancelOrderListEvent;
import com.ourslook.zuoyeba.model.CourseCancelReasonModel;

import org.greenrobot.eventbus.EventBus;

import java.util.List;


/**
 * Created by huangyi on 16/5/19.
 * 取消订单列表适配器
 */
public class CancelOrderAdapter extends WyCommonAdapter<CourseCancelReasonModel>  {
    public CancelOrderAdapter(Context context, List<CourseCancelReasonModel> data, int layoutId) {
        super(context, data, layoutId);
    }

    @Override
    public void convert(WyViewHolder holder, CourseCancelReasonModel courseCancelReasonModel) {
        holder.setTextViewText(R.id.tv_li_cancelListAty_reason,courseCancelReasonModel.reasonname);
        holder.getView(R.id.iv_li_cancelListAty_check).setSelected(CancelOrderActivity.chooseIndexArray[holder.getmPosition()]);
    }
}
