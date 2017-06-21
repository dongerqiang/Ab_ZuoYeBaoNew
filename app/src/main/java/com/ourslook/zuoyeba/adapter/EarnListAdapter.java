package com.ourslook.zuoyeba.adapter;

import android.content.Context;

import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.adapter.baseadapter.WyCommonAdapter;
import com.ourslook.zuoyeba.adapter.baseadapter.WyViewHolder;
import com.ourslook.zuoyeba.model.PayTeacherIncomeDetailModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by huangyi on 16/5/19.
 * 收益列表数据展示
 */
public class EarnListAdapter extends WyCommonAdapter<PayTeacherIncomeDetailModel> {
    public EarnListAdapter(Context context, List<PayTeacherIncomeDetailModel> data, int layoutId) {
        super(context, data, layoutId);
    }

    @Override
    public void convert(WyViewHolder holder, PayTeacherIncomeDetailModel payTeacherIncomeDetailModel) {
        Date date = new Date(payTeacherIncomeDetailModel.time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        holder.setTextViewText(R.id.tv_li_myMoneyAty_date, format.format(date));
        holder.setTextViewText(R.id.tv_li_myMoneyAty_money, String.format("￥%.2f", payTeacherIncomeDetailModel.money));

    }
}
