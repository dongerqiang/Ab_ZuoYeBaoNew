package com.ourslook.zuoyeba.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/5/3.
 */
public class MyOrderListFragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> list;

    public MyOrderListFragmentAdapter(FragmentManager fragmentManager, List<Fragment> list) {
        super(fragmentManager);
        this.list=list;
    }

    @Override
    public Fragment getItem(int position) {

        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
