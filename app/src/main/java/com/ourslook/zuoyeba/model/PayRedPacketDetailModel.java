package com.ourslook.zuoyeba.model;

import java.io.Serializable;

/**
 * Created by SEED on 2016/1/14.
 */
public class PayRedPacketDetailModel implements Serializable{
    public long id;// 红包ID
    public String name;// 红包名称
    public long money;// 红包金额
    public long time ;// 有效期
    public int status;// 状态(1:未使用 2:使用中 3:已使用 4:已过期)

}
