package com.ourslook.zuoyeba.adapter;

import android.content.Context;
import android.widget.TextView;

import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.adapter.baseadapter.WyCommonAdapter;
import com.ourslook.zuoyeba.adapter.baseadapter.WyViewHolder;
import com.ourslook.zuoyeba.model.PayRedPacketDetailModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by huangyi on 16/5/16.
 * 红包列表适配器
 */
public class MyRedBagAdapter extends WyCommonAdapter<PayRedPacketDetailModel> {
    public MyRedBagAdapter(Context context, List data, int layoutId) {
        super(context, data, layoutId);
    }

    @Override
    public void convert(WyViewHolder holder, PayRedPacketDetailModel mPayRedPacketDetailModel) {

        Date date=new Date(mPayRedPacketDetailModel.time);
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        String str=format.format(date);
        holder.setTextViewText(R.id.tv_li_myRedAty_name,mPayRedPacketDetailModel.name);
        holder.setTextViewText(R.id.tv_li_myRedAty_date,str);
        holder.setTextViewText(R.id.tv_li_myRedAty_money,mPayRedPacketDetailModel.money+"");
        TextView tvStatus=holder.getView(R.id.tv_li_myRedAty_status);
      if(mPayRedPacketDetailModel.status==1){//未使用 使用中
          tvStatus.setTextColor(mContext.getResources().getColor(R.color.mine_tc_s));
          holder.getView(R.id.ll_li_myRedAty_money).setBackgroundResource(R.drawable.red_use);
      }else {
          tvStatus.setTextColor(mContext.getResources().getColor(R.color.tc9));
          holder.getView(R.id.ll_li_myRedAty_money).setBackgroundResource(R.drawable.red_used);
      }
        holder.setTextViewText(R.id.tv_li_myRedAty_status,mPayRedPacketDetailModel.status==1?"未使用":mPayRedPacketDetailModel.status==2?"使用中":mPayRedPacketDetailModel.status==3?
        "已使用":"已过期");

    }
}
