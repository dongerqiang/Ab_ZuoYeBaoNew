package com.ourslook.zuoyeba.model;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by wangyu on 16/5/17.
 */
public class ChooseLocation {

    public String name;
    public String address;
    public boolean isCurrent;// 是否是当前地址
    public boolean isChecked;
    public boolean isHistory;
    public LatLng latLng;//用于学生下单经纬度

    public ChooseLocation(String name, LatLng latLng, String address, boolean isCurrent, boolean isChecked, boolean isHistory) {
        this.name = name;
        this.address = address;
        this.isCurrent = isCurrent;
        this.isChecked = isChecked;
        this.isHistory = isHistory;
        this.latLng = latLng;
    }
}
