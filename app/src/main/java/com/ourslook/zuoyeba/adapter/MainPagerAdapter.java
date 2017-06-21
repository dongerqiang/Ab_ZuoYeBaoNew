package com.ourslook.zuoyeba.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import com.ourslook.zuoyeba.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wy on 2015/11/11.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

    public  List<Fragment> mFragments = new ArrayList<>();
    public  List<String> mFragmentTitles = new ArrayList<>();

    public FragmentManager fm;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fm=fm;
    }

    public void addFragment(Fragment fragment, String title) {
        mFragments.add(fragment);
        mFragmentTitles.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitles.get(position);
    }

}
