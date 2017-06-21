package com.ourslook.zuoyeba.event;

/**
 * Created by huangyi on 16/5/19.
 * 取消订单条目状态选择
 */
public class CancelOrderListEvent {
    public int chooseIndex;
    public CancelOrderListEvent(int chooseIndex){
        this.chooseIndex=chooseIndex;
    }
}
