package com.ourslook.zuoyeba.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.radar.RadarNearbyResult;
import com.baidu.mapapi.radar.RadarNearbySearchOption;
import com.baidu.mapapi.radar.RadarSearchError;
import com.baidu.mapapi.radar.RadarSearchListener;
import com.baidu.mapapi.radar.RadarSearchManager;
import com.baidu.mapapi.radar.RadarUploadInfo;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.ourslook.zuoyeba.AppConfig;
import com.ourslook.zuoyeba.BaseActivity;
import com.ourslook.zuoyeba.Constants;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ServerURL;
import com.ourslook.zuoyeba.adapter.PickUpTeacherListAdapter;
import com.ourslook.zuoyeba.model.OrderPageTeacherModel;
import com.ourslook.zuoyeba.model.OrderTeacherModel;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.StringUtils;
import com.ourslook.zuoyeba.utils.ToastUtil;
import com.ourslook.zuoyeba.view.ScaleListView;
import com.ourslook.zuoyeba.view.WaveView;
import com.ourslook.zuoyeba.view.dialog.WarningDailog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by zhaotianlong on 2016/5/18.
 */
public class PickUpTeacherActivity extends BaseActivity implements View.OnClickListener, RadarSearchListener, AdapterView.OnItemClickListener {

    /* 百度地图 */
    private BaiduMap mBaiduMap;
    private MapView mMapView;
    /* 定位相关 */
    private LocationClient mLocationClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    private BitmapDescriptor mCurrentMarker;
    boolean isFirstLoc = true;// 是否首次定位
    private WarningDailog mCanceOrderDialog;
    private boolean needMap = false;// 是否显示地图
    /* 百度雷达 */
    private RadarSearchManager mManager;

    private View ll_have;// 有老师
    private View ll_no;// 无老师
    private View ll_leftTime;// 剩余时间

    int leftTime = 180;
    private TextView tv_leftTime;// 剩余时间
    private TextView tv_teacherCount;// 老师数量
    private ScaleListView list_teacher;// 老师列表
    private ArrayList<OrderTeacherModel> list = new ArrayList<>();
    private PickUpTeacherListAdapter adapter;

    private TextView tv_resend;// 重新下单
    private TextView tv_continue;// 继续等待
    private String OrderNo;
    private Intent intent;
    private int type;
    public static final String  TAG = "PickUpTeacherActivity";
    public static    boolean flag=false;//用于在取消订单以后关闭轮询
    //音频播放器
    private MediaPlayer mediaPlayer;
    private int teacherNum = 0;//老师数量
    private boolean startNew = false; //用于type =3 打开新的界面来确认老师

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    leftTime--;
                    tv_leftTime.setText(leftTime + "s");
                    if (leftTime > 0 && leftTime % 5 == 0) {
                        refreshWork();
                    }
                    if (leftTime <= 0) {
                        mBaiduMap.clear();
                        handler.removeMessages(1);
                        ll_leftTime.setVisibility(View.GONE);
                        ll_have.setVisibility(View.GONE);
                        ll_no.setVisibility(View.VISIBLE);
                    } else {
                        handler.sendEmptyMessageDelayed(1, 1000);
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private FrameLayout map_container;
    private WaveView waveView;
    private String address;
    private LatLng mcurrentLatitude;
    private InfoWindow mInfoWindow;
    private OverlayOptions teacherOption;
    private boolean clearOverlay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithDefaultTitle(R.layout.activity_pickup_teacher, "选择老师");
        OrderNo = getIntent().getStringExtra(Constants.PASS_ORDER);
        address = getIntent().getStringExtra(Constants.TEACHE_ADDRESS);
        type = getIntent().getIntExtra("type", -1);
        Log.d(TAG,"OrderNo = "+OrderNo+"\naddress = "+address+"\n type = "+type+"\ntoke = "+AppConfig.token);
        setListener();
        if (StringUtils.isNotEmpty(AppConfig.userVo)) {
            initMap();
            initLocation();
            initRadar();
//            mLocationClient.start(); //不需要定位
        }
        refreshWork();
        handler.sendEmptyMessageDelayed(1, 1000);
        initView();
    }

    private void decodeAddress(final String address) {
        if(address == null){
            return;
        }
        //新建编码查询对象
        GeoCoder geocode = GeoCoder.newInstance();
        //新建查询对象要查询的条件
        GeoCodeOption GeoOption = new GeoCodeOption().city(AppConfig.city).address(address);
        //发起地理编码请求
        geocode.geocode(GeoOption);
        //设置查询结果监听者
        geocode.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {

                  /**
             * 反地理编码查询结果回调函数
             * @param result  反地理编码查询结果
             */
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {

            }


                /**
                 * 地理编码查询结果回调函数
                 * @param result  地理编码查询结果
                 */
        @Override
        public void onGetGeoCodeResult(GeoCodeResult result) {

            mcurrentLatitude =result.getLocation();
            Log.d(TAG, "onGetGeoCodeResult  mcurrentLatitude = "+mcurrentLatitude.latitude+","+mcurrentLatitude.longitude);
            if(mcurrentLatitude==null) return;
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(mcurrentLatitude,18);
            mBaiduMap.animateMapStatus(u,1000);
            //window info
            Button button = new Button(PickUpTeacherActivity.this);
            button.setText(address);
            button.setPadding(20,0,20,0);
            //noinspection deprecation
            button.setTextColor(getResources().getColor(R.color.white));
            button.setBackgroundResource(R.drawable.shap_button);
            button.setLines(1);
            mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), mcurrentLatitude, -120, null);
            mBaiduMap.showInfoWindow(mInfoWindow);

            //
            BitmapDescriptor teacherLoaction = BitmapDescriptorFactory
                    .fromResource(R.drawable.mylocation);

            //构建MarkerOption，用于在地图上添加Marker
            teacherOption = new MarkerOptions()
                    .animateType(MarkerOptions.MarkerAnimateType.grow)
                    .position(mcurrentLatitude)
                    .icon(teacherLoaction);
            //在地图上添加Marker，并显示
            mBaiduMap.addOverlay(teacherOption);

        }
    });
    }

    @Override
    protected void initView() {
        flag=false;
        mIvTitleBack.setVisibility(View.GONE);
        mTvTitleLeft.setVisibility(View.VISIBLE);
        mTvTitleLeft.setText("取消订单");
        map_container = (FrameLayout)findViewById(R.id.map_container);
        mMapView = (MapView) findViewById(R.id.bMapView_pickUpTAty);
        waveView = (WaveView) findViewById(R.id.waveView);

        ll_have = findViewById(R.id.ll_pickUpTAty_have);
        ll_no = findViewById(R.id.ll_pickUpTAty_no);
        ll_leftTime = findViewById(R.id.view_pickUpTAty_leftTime);

        tv_leftTime = (TextView) findViewById(R.id.tv_pickUpTAty_leftTime);
        tv_teacherCount = (TextView) findViewById(R.id.tv_pickUpTAty_teacherCount);
        list_teacher = (ScaleListView) findViewById(R.id.list_pickUpTAty_teacher);
        list_teacher.setScaleView(mMapView);
        tv_resend = (TextView) findViewById(R.id.tv_pickUpTAty_resend);
        tv_continue = (TextView) findViewById(R.id.tv_pickUpTAty_continue);
        if (type != -1) {
            if (type != 3) {
                map_container.setVisibility(View.GONE);
                mMapView.setVisibility(View.GONE);
                waveView.setVisibility(View.GONE);
            } else {
                map_container.setVisibility(View.VISIBLE);
                mMapView.setVisibility(View.VISIBLE);
                waveView.setVisibility(View.VISIBLE);
                initWaveView();
                startMediaPlayer(R.raw.location);
                decodeAddress(address);
            }
        }
        mTvTitleLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消订单
                cancel();
            }
        });
    }

    private void initWaveView() {
        waveView.setDuration(5000);
        waveView.setStyle(Paint.Style.FILL);
        waveView.setColor(Color.parseColor("#FF0000"));
        waveView.setInterpolator(new LinearOutSlowInInterpolator());
        waveView.start();
    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        //声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 10000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);

    }

    /**
     * 初始化地图
     */
    private void initMap() {
        mBaiduMap = mMapView.getMap();
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 设置定位层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        mCurrentMarker = BitmapDescriptorFactory
                    .fromResource(R.drawable.mylocation);


        MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
        config.accuracyCircleFillColor = 0x00000000;
        config.accuracyCircleStrokeColor = 0x00000000;
        mBaiduMap.setMyLocationConfigeration(config);
        /*//// 定位初始化
        LocationClient mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();*/

    }

    /**
     * 地图标注(老师位置)
     *
     * @param sex
     * @param y   经度
     * @param x   纬度
     */
    private void addOverlayMarker(String sex, double y, double x) {
        //定义Maker坐标点
        LatLng point = new LatLng(y, x);
        //构建Marker图标
        BitmapDescriptor bitmap;
        if (sex.equals("1")) {
            bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_nan);
        } else {
            bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_nv);
        }
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .animateType(MarkerOptions.MarkerAnimateType.grow)
                .position(point)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);

    }

    /**
     * 初始化百度周边雷达
     */
    private void initRadar() {
        //周边雷达管理器
        mManager = RadarSearchManager.getInstance();

        //坐标
        LatLng pt = new LatLng(AppConfig.latitude, AppConfig.longitude);

        //周边雷达设置用户身份标识，id为空默认是设备标识
        mManager.setUserID(AppConfig.userVo.imacc);
        //上传位置
        RadarUploadInfo info = new RadarUploadInfo();
        info.comments = AppConfig.userVo.sex + "";
        info.pt = pt;
        mManager.uploadInfoRequest(info);

        //构造请求参数，其中centerPt是自己的位置坐标
        RadarNearbySearchOption option = new RadarNearbySearchOption()
                .centerPt(pt).pageNum(1).radius(3000);
        //发起查询请求
        mManager.nearbyInfoRequest(option);

        //周边雷达设置监听
        mManager.addNearbyInfoListener(this);
    }

    private void setListener() {
        tv_resend.setOnClickListener(this);
        tv_continue.setOnClickListener(this);
        adapter = new PickUpTeacherListAdapter(list, this, OrderNo);
        list_teacher.setAdapter(adapter);
        list_teacher.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_pickUpTAty_resend:
                finish();
                break;
            case R.id.tv_pickUpTAty_continue:
                restartPickTeacher();
                break;
            default:
                break;
        }
    }

    /**
     * 继续等待
     */
    private void restartPickTeacher() {
        mBaiduMap.clear();
        ll_leftTime.setVisibility(View.VISIBLE);
        ll_have.setVisibility(View.GONE);
        ll_no.setVisibility(View.GONE);
        refreshWork();
        leftTime = 180;
        tv_leftTime.setText(leftTime + "s");
        tv_teacherCount.setText("共有" + 0 + "位老师接单");
        if (!handler.hasMessages(1)) {
            handler.sendEmptyMessageDelayed(1, 1000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出时销毁定位
        mLocationClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        mMapView = null;
        delWork();
        if (handler.hasMessages(1)) {
            handler.removeMessages(1);
        }
        //移除监听
        mManager.removeNearbyInfoListener(this);
        //清除用户信息
        mManager.clearUserInfo();
        //释放资源
        mManager.destroy();
        mManager = null;
        if(mLocationClient !=null){
            mLocationClient.unRegisterLocationListener(myListener);
        }
        if(waveView !=null){
            waveView.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        if (!handler.hasMessages(1)) {
            handler.sendEmptyMessageDelayed(1, 1000);
        }
        if(type == 3 && clearOverlay){
            if(mInfoWindow !=null){
                mBaiduMap.showInfoWindow(mInfoWindow);
            }
            if(teacherOption !=null){
                mBaiduMap.addOverlay(teacherOption);
            }
            if(waveView !=null){
                waveView.start();
            }
            clearOverlay = false;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
        if (handler.hasMessages(1)) {
            handler.removeMessages(1);
        }
    }



    /**
     * 搜索老师
     */
    private void refreshWork() {
        if(flag){
            return;
        }
        Map<String, String> params = new HashMap();
        params.put("token", AppConfig.token);
        EasyHttp.doPost(mContext, ServerURL.REFRESHWORK, params, null, OrderPageTeacherModel.class, false, new EasyHttp.OnEasyHttpDoneListener<OrderPageTeacherModel>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<OrderPageTeacherModel> resultBean) {
                list = resultBean.data.teacherList;
                tv_teacherCount.setText("共有" + list.size() + "位老师接单");
                mBaiduMap.clear();
                for (int i = 0; i < list.size(); i++) {
                    OrderTeacherModel orderTeacherModel = list.get(i);
                    int sex = orderTeacherModel.getSex();
                    double coordinatex = orderTeacherModel.coordinatex;
                    double coordinatey = orderTeacherModel.coordinatey;
                    addOverlayMarker("" + sex, coordinatey/*+0.02*/, coordinatex/*-0.02*/);
                }
//                map_container.setVisibility(View.GONE);
                if(type == 3){
                    clearOverlay = true;
                    Intent intent = new Intent(PickUpTeacherActivity.this, DetailTeacherActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.TEACHER_LIST,list);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    if(waveView !=null){
                        waveView.stop();
                    }
                    list.clear();
                    tv_teacherCount.setText("共有" + list.size() + "位老师接单");
                }
                ll_have.setVisibility(View.VISIBLE);
                ll_no.setVisibility(View.GONE);
                adapter.list.clear();
                adapter.setList(list);
                adapter.notifyDataSetChanged();
                if (list.size() > teacherNum) {
                    //播放音频文件
                    startMediaPlayer(R.raw.teacher);
                    teacherNum = list.size();
                }
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                if(teacherNum>0){
                    ToastUtil.showToastDefault(mContext,message);
                }
            }
        });
    }

    /**
     * 播放音频文件
     */
    private void startMediaPlayer(int voice) {

        mediaPlayer = MediaPlayer.create(this, voice);
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消订单 点击（选择老师和 物理返回键调用）
     */
    private void cancel() {
        if (mCanceOrderDialog == null)
            mCanceOrderDialog = new WarningDailog(mContext, R.style.FullHeightDialog, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delWork();
                }
            });
        mCanceOrderDialog.setText("返回将会删除此订单， 确认？");
        mCanceOrderDialog.show();
    }

    /**
     * 放弃订单
     */
    private void delWork() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", AppConfig.token);

        EasyHttp.doPost(mContext, ServerURL.DELWORK, params, null, Object.class, false, new EasyHttp.OnEasyHttpDoneListener<Object>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<Object> resultBean) {
                ToastUtil.showToastDefault(mContext, "已删除");
                if (mCanceOrderDialog != null && mCanceOrderDialog.isShowing())
                    mCanceOrderDialog.dismiss();
                //TODO 此处关闭循环
                flag=true;
                    finish();
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                if(flag){
                    return;
                }
                ToastUtil.showToastDefault(mContext, message);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        OrderTeacherModel orderTeacherModel = list.get(position);
        long teacherid = orderTeacherModel.getId();
        intent = new Intent(this, TeacherInfoActivity.class);
        intent.putExtra("teacherid", teacherid);
        startActivity(intent);

    }

    /**
     * 百度周边雷达回调(周边位置检索)
     * 利用周边雷达功能，可实现周边（处于同一个周边雷达关系内）用户位置信息检索的能力。
     * 检索过程支持距离、时间等约束条件；返回结果支持按照距离、时间远近的排序。
     *
     * @param radarNearbyResult
     * @param radarSearchError
     */
    @Override
    public void onGetNearbyInfoList(RadarNearbyResult radarNearbyResult, RadarSearchError radarSearchError) {

    }

    /**
     * 百度周边雷达回调(位置信息上传)
     * 目前支持单次位置信息上传和位置信息连续自动上传两种模式。
     *
     * @param radarSearchError
     */
    @Override
    public void onGetUploadState(RadarSearchError radarSearchError) {

    }

    /**
     * 百度周边雷达回调(清除用户信息)
     * 用户信息清除后，将不会再被其他人检索到。
     *
     * @param radarSearchError
     */
    @Override
    public void onGetClearInfoState(RadarSearchError radarSearchError) {

    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
//            mLocationClient.stop();
            if (location == null || mMapView == null) {
                return;
            }
            AppConfig.longitude = location.getLongitude();
            AppConfig.latitude = location.getLatitude();
            String a = location.getLongitude() + "";
            String b = location.getLatitude() + "";
//            Toast.makeText(PickUpTeacherAty.this, a, Toast.LENGTH_LONG).show();
//            Toast.makeText(PickUpTeacherAty.this, b, Toast.LENGTH_LONG).show();
            Log.d("BaiduLocationApiDem","======================== \n latititude : "+a+"\nLatitude :" +b+"\nmcurrentLatitude:"+mcurrentLatitude);
            LatLng ll = new LatLng(location.getLatitude(),
                    location.getLongitude());
            // 构造定位数据
            if(mcurrentLatitude != null){
                ll = mcurrentLatitude;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-36·0
                    .direction(0).latitude(ll.latitude)
                    .longitude(ll.longitude).build();
            // 设置定位数据
            mBaiduMap.setMyLocationData(locData);

            if (isFirstLoc) {
                isFirstLoc = false;
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll,18);
                mBaiduMap.animateMapStatus(u,1000);

                Button button = new Button(PickUpTeacherActivity.this);
                button.setText(address);
                button.setPadding(20,0,20,0);
                //noinspection deprecation
                button.setTextColor(getResources().getColor(R.color.white));
                button.setBackgroundResource(R.drawable.shap_button);
                button.setLines(1);
                InfoWindow mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), ll, -80, null);
                mBaiduMap.showInfoWindow(mInfoWindow);
            }else{
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u,500);
            }

        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    /**
     * 监听Back键按下事件,方法2:
     * 注意:
     * 返回值表示:是否能完全处理该事件
     * 在此处返回false,所以会继续传播该事件.
     * 在具体项目中此处的返回值视情况而定.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            System.out.println("按下了back键   onKeyDown()");
            cancel();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }
}
