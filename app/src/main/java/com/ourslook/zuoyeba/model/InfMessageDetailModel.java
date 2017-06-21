package com.ourslook.zuoyeba.model;

import java.io.Serializable;

/**
 * Created by SEED on 2016/1/18.
 */
public class InfMessageDetailModel implements Serializable {
    public long id;// 订单ID(备用)
    public long workid;// 订单ID(备用)
    public String content;// 内容
    public long time;// 时间
    public boolean isread;// 是否已读

}
