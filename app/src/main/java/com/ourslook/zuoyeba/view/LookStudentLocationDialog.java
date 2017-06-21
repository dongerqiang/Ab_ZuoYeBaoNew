package com.ourslook.zuoyeba.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.model.OrderDetailModel;
import com.ourslook.zuoyeba.view.dialog.BaseDialog;

import butterknife.Bind;

/**
 * Created by huangyi on 16/5/20.
 * 抢单列表进去后订单详情页中点击查看学生的位置
 */

public class LookStudentLocationDialog extends BaseDialog implements View.OnClickListener, ValueAnimator.AnimatorUpdateListener {
    @Bind(R.id.map_view_look_map)
    MapView mapViewLookMap;
    @Bind(R.id.iv_look_map_close)
    ImageView ivLookMapClose;
    BaiduMap mBaiduMap;

    ObjectAnimator showAnim;
    ObjectAnimator dismissAnim;

    OrderDetailModel orderDetailModel;

    LatLng studentLocation;//学生位置
    private double mLat;//纬度
    private double mLng;//经度

    public LookStudentLocationDialog(Context context, double lng, double lat) {
        super(context, R.layout.dialog_look_map);
        mLat = lat;
        mLng = lng;
        init();
    }

    private void init() {
        setOnClickListeners(this, ivLookMapClose);
        showAnim = ObjectAnimator.ofFloat(mView, "scaleY", 0f, 1f);
        showAnim.setDuration(1000);
        showAnim.addUpdateListener(this);

        dismissAnim = ObjectAnimator.ofFloat(mView, "scaleX", 1f, 0f);
        dismissAnim.setDuration(1000);
        dismissAnim.addUpdateListener(this);
        mBaiduMap = mapViewLookMap.getMap();
        getStudentLocal();
    }

    private void getStudentLocal() {
        studentLocation = new LatLng(mLat, mLng);
        MapStatusUpdate statusUpdate = MapStatusUpdateFactory.newLatLng(studentLocation);
        mBaiduMap.setMapStatus(statusUpdate);
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.mylocation);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(studentLocation)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_look_map_close://关闭
                dismissing();
                break;
        }
    }


    public void showing() {
        show();
        showAnim.start();
    }

    public void dismissing() {
        dismissAnim.start();
    }


    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        if (animation == showAnim) {//展示动画
            if (animation.getAnimatedFraction() == 1) {//消失
            }
        } else {
            if (animation.getAnimatedFraction() == 1) {//出现
                dismiss();
            }
        }
    }

}
