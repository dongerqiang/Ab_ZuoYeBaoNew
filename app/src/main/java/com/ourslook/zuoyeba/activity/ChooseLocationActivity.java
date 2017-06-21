package com.ourslook.zuoyeba.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.ourslook.zuoyeba.AppConfig;
import com.ourslook.zuoyeba.BaseActivity;
import com.ourslook.zuoyeba.Constants;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.adapter.ChooseLocationAdapter;
import com.ourslook.zuoyeba.model.ChooseLocation;
import com.ourslook.zuoyeba.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 选择上门地址
 * Created by wangyu on 16/5/17.
 */
public class ChooseLocationActivity extends BaseActivity implements View.OnClickListener, BaiduMap.OnMapTouchListener, OnGetGeoCoderResultListener, OnGetPoiSearchResultListener, AdapterView.OnItemClickListener, View.OnKeyListener {

    @Bind(R.id.tv_choose_location_city)
    TextView tvChooseLocationCity;
    @Bind(R.id.et_choose_location_search)
    EditText etChooseLocationSearch;
    @Bind(R.id.iv_choose_location_search)
    ImageView ivChooseLocationSearch;
    @Bind(R.id.map_choose_location)
    MapView mapChooseLocation;
    @Bind(R.id.lv_choose_location)
    ListView lvChooseLocation;
    @Bind(R.id.location_im)
    ImageView imageView_sex;
    ArrayList<ChooseLocation> locations = new ArrayList<>();

    BaiduMap mBaiduMap;

    LatLng myLocation;

    ChooseLocationAdapter adapter;
    PoiSearch search;

    ChooseLocation myLocationChoose;
    int currentChecked;

    String address;
    ChooseLocation historyLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        address = getIntent().getStringExtra(Constants.PASS_ADDRESS);
        setContentViewWithDefaultTitle(R.layout.activity_choose_location, "授课地址");

    }


    @Override
    protected void initView() {
        if(AppConfig.userVo !=null && AppConfig.userVo.sex == 1){
            imageView_sex.setImageResource(R.drawable.icon_nan);
        }else {
            imageView_sex.setImageResource(R.drawable.icon_nv);
        }

        mTvTitleRight.setText("确认");
        mTvTitleRight.setVisibility(View.VISIBLE);

        tvChooseLocationCity.setText(AppConfig.city);

        setOnClickListeners(this, ivChooseLocationSearch, mTvTitleRight);

        mBaiduMap = mapChooseLocation.getMap();

        mBaiduMap.setOnMapTouchListener(this);

        myLocation = new LatLng(AppConfig.latitude, AppConfig.longitude);
        MapStatusUpdate statusUpdate = MapStatusUpdateFactory.newLatLng(myLocation);
        mBaiduMap.setMapStatus(statusUpdate);

        GeoCoder coder = GeoCoder.newInstance();
        coder.setOnGetGeoCodeResultListener(this);
        coder.reverseGeoCode(new ReverseGeoCodeOption().location(myLocation));

        adapter = new ChooseLocationAdapter(mContext, locations, R.layout.item_choose_location);
        lvChooseLocation.setAdapter(adapter);

        search = PoiSearch.newInstance();
        search.setOnGetPoiSearchResultListener(this);


        lvChooseLocation.setOnItemClickListener(this);

        etChooseLocationSearch.setOnKeyListener(this);

        if (!TextUtils.isEmpty(address)) {
            historyLocation = new ChooseLocation(address, new LatLng(AppConfig.latitude, AppConfig.longitude), null, false, false, true);
        }
    }


    private void searchBuilding(LatLng latLng) {
        search.searchNearby(new PoiNearbySearchOption()
                .keyword("小区")
                .location(latLng)
                .radius(10000)
        );
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
        if (geoCodeResult.getLocation() != null) {
            myLocation = geoCodeResult.getLocation();
        } else {
            myLocation = new LatLng(AppConfig.latitude, AppConfig.longitude);
        }

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        myLocationChoose = new ChooseLocation(reverseGeoCodeResult.getAddress(), reverseGeoCodeResult.getLocation(),
                null, true, true, false);

        locations.add(myLocationChoose);
        if (historyLocation != null) {
            locations.add(historyLocation);
        }
        adapter.notifyDataSetChanged();

        searchBuilding(myLocation);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        search.destroy();
        mapChooseLocation.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mapChooseLocation.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mapChooseLocation.onPause();
    }


    @Override
    public void onTouch(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_UP:
                LatLng currentCenter = mBaiduMap.getMapStatus().target;
                logForDebug(currentCenter.toString());
                searchBuilding(currentCenter);
                break;
        }
    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        List<PoiInfo> poiInfos = poiResult.getAllPoi();
        if (poiInfos != null && poiInfos.size() > 0) {
            locations.clear();
            locations.add(myLocationChoose);

            for (int i = 0; i < poiInfos.size(); i++) {
                PoiInfo info = poiInfos.get(i);
                locations.add(new ChooseLocation(info.name, info.location, info.address, false, i + 1 == currentChecked, false));
            }
            //add history location and notice it's state
            if (historyLocation != null) {
                locations.add(historyLocation);
            }
            adapter.notifyDataSetChanged();
        } else {
            ToastUtil.showToastDefault(mContext, "暂无搜索结果");
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        currentChecked = position;
        for (int i = 0; i < locations.size(); i++) {
            locations.get(i).isChecked = (i == position);
            myLocationChoose.isChecked = (0 == position);
            //set the history
            if (historyLocation != null && i == locations.size() - 1) {
                historyLocation.isChecked = (i == position);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_choose_location_search:
                //此处需要点击收起软键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),
                            0);
                }
                search.searchNearby(new PoiNearbySearchOption()
                        .keyword(etChooseLocationSearch.getText().toString())
                        .location(myLocation)
                        .radius(10000)
                );
                break;
            case R.id.tv_title_right:
                ChooseLocation checkedLocation = null;
                for (ChooseLocation location : locations) {
                    if (location.isChecked) {
                        checkedLocation = location;
                    }
                }
                if (checkedLocation != null) {
                    Intent intent = getIntent();
                    intent.putExtra("chooseAddress", checkedLocation.name);
                    intent.putExtra("latlng", checkedLocation.latLng);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    ToastUtil.showToastOnce(mContext, "请选择地址");
                }
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {//修改回车键功能
            //此处需要点击收起软键盘
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),
                        0);
            }
            search.searchNearby(new PoiNearbySearchOption()
                    .keyword(etChooseLocationSearch.getText().toString())
                    .location(myLocation)
                    .radius(10000)
            );
            return true;
        }
        return false;
    }
}
