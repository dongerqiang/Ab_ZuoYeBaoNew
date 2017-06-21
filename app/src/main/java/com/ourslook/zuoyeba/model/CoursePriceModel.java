package com.ourslook.zuoyeba.model;

import java.io.Serializable;

/**
 * Created by SEED on 2016/1/5.
 */
public class CoursePriceModel implements Serializable{
    public int type;// 授课方式：1:电话 2:视频 3:上门
    public String name;// 授课方式名称
    public String price;// 单位：元/分

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }
}
