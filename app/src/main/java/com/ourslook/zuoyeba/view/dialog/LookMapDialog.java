package com.ourslook.zuoyeba.view.dialog;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
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
import com.ourslook.zuoyeba.AppConfig;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ServerURL;
import com.ourslook.zuoyeba.model.AccCoordinate;
import com.ourslook.zuoyeba.model.OrderDetailModel;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by huangyi on 16/5/20.
 * 查看 老师和学生的位置
 */

public class LookMapDialog extends BaseDialog implements View.OnClickListener, ValueAnimator.AnimatorUpdateListener {
    @Bind(R.id.map_view_look_map)
    MapView mapViewLookMap;
    @Bind(R.id.iv_look_map_close)
    ImageView ivLookMapClose;
    BaiduMap mBaiduMap;

    ObjectAnimator showAnim;
    ObjectAnimator dismissAnim;

    OrderDetailModel orderDetailModel;

    LatLng teacherLocation;//老师位置
    public LookMapDialog(Context context, OrderDetailModel orderDetailModel) {
        super(context, R.layout.dialog_look_map);
        this.orderDetailModel=orderDetailModel;
        init();
    }
    private void init(){
        setOnClickListeners(this,ivLookMapClose);
        showAnim=ObjectAnimator.ofFloat(mView,"scaleY",0f,1f);
        showAnim.setDuration(1000);
        showAnim.addUpdateListener(this);

        dismissAnim=ObjectAnimator.ofFloat(mView,"scaleX",1f,0f);
        dismissAnim.setDuration(1000);
        dismissAnim.addUpdateListener(this);
        mBaiduMap=mapViewLookMap.getMap();
        getTeacherLocal();
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            getTeacherLocal();
            handler.sendEmptyMessageDelayed(0,10000);
        }
    };


    /**
     * 寻找老师位置
     */
    private void  getTeacherLocal(){
        if(orderDetailModel==null){
            return;
        }
     Map<String,String> params=new HashMap<>();
        params.put("token",""+AppConfig.token);
        params.put("workid",""+orderDetailModel.id);
        EasyHttp.doPost(mContext, ServerURL.GETCOORDINATE, params, null, AccCoordinate.class, false, new EasyHttp.OnEasyHttpDoneListener<AccCoordinate>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<AccCoordinate> resultBean) {
                if(resultBean.data!=null){
                    teacherLocation=new LatLng(resultBean.data.coordinatey, resultBean.data.coordinatex);
                    MapStatusUpdate statusUpdate = MapStatusUpdateFactory.newLatLng(teacherLocation);
                    mBaiduMap.setMapStatus(statusUpdate);
                    BitmapDescriptor bitmap = BitmapDescriptorFactory
                            .fromResource(resultBean.data.sex==1?R.drawable.icon_nan:R.drawable.icon_nv);
                    //构建MarkerOption，用于在地图上添加Marker
                    OverlayOptions option = new MarkerOptions()
                            .position(teacherLocation)
                            .icon(bitmap);
                    //在地图上添加Marker，并显示
                    mBaiduMap.addOverlay(option);
                    handler.sendEmptyMessageDelayed(0,10000);
                }
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                ToastUtil.showToastDefault(mContext,message);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_look_map_close://关闭
                dismissing();
                break;
        }
    }


    public void showing(){
        show();
        showAnim.start();
    }

    public void dismissing(){
        dismissAnim.start();
    }



    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        if(animation==showAnim){//展示动画
            if(animation.getAnimatedFraction()==1){//消失

            }
        }else{
            if(animation.getAnimatedFraction()==1){//出现
                dismiss();
                handler.removeMessages(0);
            }
        }
    }
}
