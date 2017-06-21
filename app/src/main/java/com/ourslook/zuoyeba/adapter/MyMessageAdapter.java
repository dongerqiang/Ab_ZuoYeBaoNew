package com.ourslook.zuoyeba.adapter;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.adapter.baseadapter.WyCommonAdapter;
import com.ourslook.zuoyeba.adapter.baseadapter.WyViewHolder;
import com.ourslook.zuoyeba.model.InfMessageDetailModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by huangyi on 16/5/16.
 * 消息列表适配器
 */
public class MyMessageAdapter extends WyCommonAdapter<InfMessageDetailModel> {
    public MyMessageAdapter(Context context, List data, int layoutId) {
        super(context, data, layoutId);
    }

    @Override
    public void convert(WyViewHolder holder, InfMessageDetailModel mInfMessageDetailModel) {
        Date date=new Date(mInfMessageDetailModel.time);
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str=format.format(date);
        holder.setTextViewText(R.id.tv_li_myMsgAty_msg,mInfMessageDetailModel.content);
        holder.setTextViewText(R.id.tv_li_myMsgAty_date,str);
        TextView tvContent=holder.getView(R.id.tv_li_myMsgAty_msg);
        TextView tvTime=holder.getView(R.id.tv_li_myMsgAty_date);
        if(!mInfMessageDetailModel.isread){
            tvContent.setTextColor(Color.BLACK);
            tvTime.setTextColor(Color.BLACK);
        }else{
            tvContent.setTextColor(Color.GRAY);
            tvTime.setTextColor(Color.GRAY);
        }
    }
}
