package com.ourslook.zuoyeba.event;

import com.ourslook.zuoyeba.adapter.baseadapter.WyViewHolder;

/**
 * Created by huangyi on 16/5/19.
 * 抢单    教师主页-----适配器
 */
public class GetOrderEvent {
   public int position;
    public WyViewHolder holder;
    public GetOrderEvent(int position,WyViewHolder holder){
        this.position=position;
        this.holder=holder;
    }
}
