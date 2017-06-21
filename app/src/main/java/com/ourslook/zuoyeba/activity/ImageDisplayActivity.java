package com.ourslook.zuoyeba.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.utils.StringUtils;

import java.util.ArrayList;

/**
 * 大图显示界面
 * Created by zhaotianlong on 2016/5/16.
 */
public class ImageDisplayActivity  extends FragmentActivity implements ViewPager.OnPageChangeListener{
    private ViewPager vp;
    private LinearLayout ll_point;
    private int p;
    //    private int size = Integer.MAX_VALUE;
    private int length;

    private ArrayList<ImageView> imageList = new ArrayList<>();
    private ArrayList<View> points = new ArrayList<>();
    private ArrayList<Uri> uriList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);
        vp = (ViewPager) findViewById(R.id.vp_imageDisplayAty);
        ll_point = (LinearLayout) findViewById(R.id.ll_imageDisplayAty_points);

        ArrayList<Uri> uri = getIntent().getParcelableArrayListExtra("uri");
        p = getIntent().getIntExtra("p",-1);

        System.out.println(uri.size());
        if (StringUtils.isNotEmpty(uri)){
            uriList = uri;
            length = uriList.size();
        }

        initImageSource();// 加载图片
        initPoints();// 加载小圆点

        vp.setAdapter(new MyPagerAdapter());
        if (length==1){
            vp.setCurrentItem(0);
        }else{
            vp.setCurrentItem(p);
        }
        vp.setOnPageChangeListener(this);

    }

    public void initPoints() {
        for (int i = 0; i < length; i++) {
            View v = new View(this);
            v.setBackgroundResource(R.drawable.qiehuan2);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(15, 15);
            params.setMargins(5,0,5,0);
            v.setLayoutParams(params);
            ll_point.addView(v);
            points.add(v);
        }
        if (p>-1){
            points.get(p).setBackgroundResource(R.drawable.qiehuan1);
        }
    }

    /**
     * 给ImageView添加Drawable图片
     * @param uri
     */
    private void setImageViewWithDrawable(Uri uri){
        ImageView iv = new ImageView(this);
        try {
            ImageLoader.getInstance().displayImage(uri.toString(),iv);
            imageList.add(iv);
        } catch (Exception e) {
            Log.e("Exception", e.getMessage(), e);
        }
    }

    public void initImageSource() {
        if (length == 1) {
            for (int i = 0; i < length; i++) {
                setImageViewWithDrawable(uriList.get(i));
            }
            ll_point.setVisibility(View.GONE);
            vp.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View arg0, MotionEvent arg1) {
                    return true;
                }
            });
        } else {
            for (int i = 0; i < length; i++) {
                setImageViewWithDrawable(uriList.get(i));
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < length; i++) {

            points.get(i).setBackgroundResource(R.drawable.qiehuan2);
        }
        points.get(position).setBackgroundResource(R.drawable.qiehuan1);
        p = position;

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    /**
     * viewPager适配器
     */
    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            try {
                container.addView(imageList.get(position), 0);
            }catch(Exception e){
                //handler something
            }
            return imageList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {


            container.removeView(imageList.get(position));

        }
    }
}
