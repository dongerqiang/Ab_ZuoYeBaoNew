package com.ourslook.zuoyeba.adapter.baseadapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 关 on 2016/4/13.
 */
public abstract class MyExpendListViewAdapter<T> extends BaseExpandableListAdapter {
    protected Context mContext;
    public List<List<T>> mData;
    private int layoutGroupId;
    private int layoutChildId;

    public MyExpendListViewAdapter(Context mContext, List<List<T>> data, int layoutGroupId, int layoutChildId) {
        this.mContext = mContext;
        this.mData = data == null ? new ArrayList<List<T>>() : data;
        this.layoutGroupId = layoutGroupId;
        this.layoutChildId = layoutChildId;
    }

    @Override
    public int getGroupCount() {
        return mData.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return mData.get(i).size();
    }

    @Override
    public Object getGroup(int i) {
        return mData.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return mData.get(i).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public abstract View getGroupView(int i, boolean b, View view, ViewGroup viewGroup);

    @Override
    public abstract View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup);


    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

}
