package com.ourslook.zuoyeba.adapter;

import android.content.Context;
import android.view.View;

import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.adapter.baseadapter.WyCommonAdapter;
import com.ourslook.zuoyeba.adapter.baseadapter.WyViewHolder;
import com.ourslook.zuoyeba.model.ChooseLocation;

import java.util.List;

/**
 * Created by wangyu on 16/5/17.
 */
public class ChooseLocationAdapter extends WyCommonAdapter<ChooseLocation> {


    public ChooseLocationAdapter(Context context, List<ChooseLocation> data, int layoutId) {
        super(context, data, layoutId);
    }

    @Override
    public void convert(WyViewHolder holder, ChooseLocation chooseLocation) {
        if (chooseLocation != null) {
            holder.setViewVisibility(R.id.iv_item_choose_location_check, chooseLocation.isChecked ? View.VISIBLE : View.INVISIBLE)
                    .setViewVisibility(R.id.tv_item_choose_location_history, chooseLocation.isHistory ? View.VISIBLE : View.GONE)
                    .setTextViewText(R.id.tv_item_choose_location_address, chooseLocation.address);
            if (chooseLocation.isCurrent) {
                holder.setTextViewText(R.id.tv_item_choose_location_name, "[当前]" + chooseLocation.name);
            } else if (chooseLocation.isHistory) {
                holder.setTextViewText(R.id.tv_item_choose_location_name, "[历史]" + chooseLocation.name);
            } else {
                holder.setTextViewText(R.id.tv_item_choose_location_name, chooseLocation.name);
            }
        }
    }

}



