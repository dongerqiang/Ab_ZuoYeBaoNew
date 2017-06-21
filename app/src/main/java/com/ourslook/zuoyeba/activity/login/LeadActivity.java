package com.ourslook.zuoyeba.activity.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ourslook.zuoyeba.BaseActivity;
import com.ourslook.zuoyeba.Constants;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.activity.ChoiceWorkActivity;
import com.ourslook.zuoyeba.adapter.VPOfLeadAdapter;
import com.ourslook.zuoyeba.utils.PreferencesManager;

import butterknife.Bind;

/**
 * 引导页
 * Created by DuanLu on 2016/7/6.
 */
public class LeadActivity extends BaseActivity implements VPOfLeadAdapter.OnPagerClick {
    @Bind(R.id.vp_lead)
    ViewPager mVpLead;
    @Bind(R.id.ll_point)
    LinearLayout mLlPoint;//点的布局

    ImageView[] mImageView;
    //int 类型的图片资源
    int mPicture[] = {R.drawable.loading1, R.drawable.loading2, R.drawable.loading3};
    private View[] mPoints;//圆点
    //ViewPager的适配器
    VPOfLeadAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lead);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Translucent navigation bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    @Override
    protected void initView() {
        getImageView();
        mVpLead = getViewById(R.id.vp_lead);
        //加载ViewPager适配器
        mAdapter = new VPOfLeadAdapter(mImageView);
        mAdapter.setOnPagerClick(this);
        mVpLead.setAdapter(mAdapter);
        mVpLead.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setMarkerPosition(-1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 初始化ImageView数组
     */
    private void getImageView() {
        mImageView = new ImageView[mPicture.length];
        for (int i = 0; i < mImageView.length; i++) {
            mImageView[i] = new ImageView(this);
            mImageView[i].setImageResource(mPicture[i]);
            mImageView[i].setScaleType(ImageView.ScaleType.FIT_XY);
        }
        //初始化点
        mPoints = new View[mPicture.length];
        int local = mVpLead.getCurrentItem();
        for (int i = 0; i < mPoints.length; i++) {
            TextView tvPoints = new TextView(mContext);
            tvPoints.setPadding(2, 2, 2, 2);
            tvPoints.setText("●");
            tvPoints.setTextColor(Color.LTGRAY);
            tvPoints.setTextSize(12);
            mLlPoint.addView(tvPoints);
            if (local == i) {
//                tvPoints.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                tvPoints.setTextColor(Color.GRAY);
            }
        }
    }

    /**
     * 设置圆点滑动过程中位置的变化
     */
    private void setMarkerPosition(int pos) {
        int position;
        if (pos == -1) {
            position = mVpLead.getCurrentItem();
        } else {
            position = pos;
        }
        for (int i = 0; i < mLlPoint.getChildCount(); i++) {
            TextView tv = (TextView) mLlPoint.getChildAt(i);
            if (position % mPoints.length == i) {
//                tv.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                tv.setTextColor(Color.GRAY);
            } else {
                tv.setTextColor(Color.LTGRAY);
            }
        }
    }

    /**
     * ViewPager的子条目点击事件
     *
     * @param index
     */
    @Override
    public void getPagerIndex(int index) {
        if (index == mImageView.length - 1) {
            //GO TO CHOICE CAREER
            inChoiceWorkActivity();
            //inHomeActivity();
        }
    }

    private void inChoiceWorkActivity() {
        Intent intent = new Intent();
        intent.setClass(this, ChoiceWorkActivity.class);
        PreferencesManager.getInstance(this).put(Constants.LEAD_KEY, false);
        startActivity(intent);
        finish();
    }



}
