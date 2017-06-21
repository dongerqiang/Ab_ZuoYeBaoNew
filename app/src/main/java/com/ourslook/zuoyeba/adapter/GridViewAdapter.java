package com.ourslook.zuoyeba.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.adapter.baseadapter.WyCommonAdapter;
import com.ourslook.zuoyeba.adapter.baseadapter.WyViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangyi on 16/5/18.
 */
public class GridViewAdapter extends WyCommonAdapter<String> {
    public GridViewAdapter(Context context, List<String> data, int layoutId) {
        super(context, data, layoutId);
    }

    @Override
    public void convert(WyViewHolder holder, String s) {
        holder.setImageViewImage(R.id.iv_grid_item,s);

    }
}
