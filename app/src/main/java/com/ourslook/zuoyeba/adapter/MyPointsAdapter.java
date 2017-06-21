package com.ourslook.zuoyeba.adapter;

import android.content.Context;

import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.adapter.baseadapter.WyCommonAdapter;
import com.ourslook.zuoyeba.adapter.baseadapter.WyViewHolder;
import com.ourslook.zuoyeba.model.AccStudentIntegrationDetailModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by huangyi on 16/5/16.
 * 我的积分适配器
 */
public class MyPointsAdapter extends WyCommonAdapter<AccStudentIntegrationDetailModel> {
    public MyPointsAdapter(Context context, List data, int layoutId) {
        super(context, data, layoutId);
    }

    @Override
    public void convert(WyViewHolder holder, AccStudentIntegrationDetailModel mAccStudentIntegrationDetailModel) {
        Date date=new Date(mAccStudentIntegrationDetailModel.time);
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str=format.format(date);
      holder.setTextViewText(R.id.tv_li_myCountAty_type,mAccStudentIntegrationDetailModel.content);
        holder.setTextViewText(R.id.tv_li_myCountAty_date,str);
        holder.setTextViewText(R.id.tv_li_myCountAty_count,"+"+mAccStudentIntegrationDetailModel.integration);

    }
}
