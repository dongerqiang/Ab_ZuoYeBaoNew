package com.ourslook.zuoyeba.event;

/**
 * 刷新教师未完成订单
 * Created by DuanLu on 2016/5/20.
 */
public class RefreshOrderListEvent {
    public int index;//0刷新未完成订单列表，1刷新已完成订单列表，2刷新所有订单列表

    public RefreshOrderListEvent(int index) {
        this.index = index;
    }
}
