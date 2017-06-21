package com.ourslook.zuoyeba.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by DuanLu on 2016/7/6.
 * 引导页的ViewPager适配器
 */
public class VPOfLeadAdapter extends PagerAdapter implements View.OnClickListener {
    ImageView[] mImageView;
    int index;

    public VPOfLeadAdapter(ImageView[] mImageView) {
        this.mImageView = mImageView;
    }

    @Override
    public int getCount() {
        return mImageView.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mImageView[position]);
        index = position;
        mImageView[position].setOnClickListener(this);
        return mImageView[position];
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
        container.removeView(mImageView[position]);
    }

    /**
     * ViewPager的点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        mOnPagerClick.getPagerIndex(index);
    }

    public void setOnPagerClick(OnPagerClick mOnPagerClick) {
        this.mOnPagerClick = mOnPagerClick;
    }

    OnPagerClick mOnPagerClick;

    /**
     * 回调接口   处理ViewPager的点击事件
     */
    public interface OnPagerClick {
        public void getPagerIndex(int index);

    }
}
