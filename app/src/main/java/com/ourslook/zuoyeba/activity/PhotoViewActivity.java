package com.ourslook.zuoyeba.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.ourslook.zuoyeba.BaseActivity;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ZuoYeBaApplication;
import com.ourslook.zuoyeba.view.PhotoViewPager;

import java.util.List;

import butterknife.Bind;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by wangyu on 16/5/20.
 */
public class PhotoViewActivity extends BaseActivity {

    @Bind(R.id.vp_photo)
    PhotoViewPager viewPager;

    List<String> urls;
    int clickedPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithDefaultTitle(R.layout.activity_photo_view, "查看图片");
    }

    @Override
    protected void initView() {
        urls = getIntent().getStringArrayListExtra("PHOTOS");
        clickedPosition = getIntent().getIntExtra("CLICKED_POSITION", 0);

        //for test
//        urls = new ArrayList<>();
//        urls.add("http://img.nnjiankang.com/allimg/130117/12-13011F92059.jpg");
//        urls.add("http://pigimg.zhongso.com/space/gallery/infoimgs/yy/yyqmh/20100901/2010090117425880993.jpg");
//        urls.add("http://www.haopic.me/wp-content/uploads/2015/01/2015011523215553.jpg");
//        urls.add("http://www.mypcera.com/star/2013/201303/59/4.jpg");

        viewPager.setOffscreenPageLimit(urls.size());

        viewPager.setAdapter(new PhotoAdapter());

        viewPager.setCurrentItem(clickedPosition, false);
    }


    class PhotoAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return urls.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(mContext);

//            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            imageView.setLayoutParams(layoutParams);

            ZuoYeBaApplication.imageLoader.displayImage(urls.get(position), photoView);

            new PhotoViewAttacher(photoView);

            container.addView(photoView);
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

        }
    }


}
