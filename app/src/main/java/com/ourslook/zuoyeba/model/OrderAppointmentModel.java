package com.ourslook.zuoyeba.model;

import java.io.Serializable;

/**
 * Created by SEED on 2016/1/18.
 */
public class OrderAppointmentModel implements Serializable {
    public boolean isappointment;// 是否有预约
    public long id ;// 订单ID(备用)
    public int type;// 方式：1:电话 2:视频 3:上门
    public long time ;// 预约时间

    public long getId() {
        return id;
    }

    public boolean isappointment() {
        return isappointment;
    }

    public long getTime() {
        return time;
    }

    public int getType() {
        return type;
    }
}
