package com.ourslook.zuoyeba.model;

import java.io.Serializable;

/**
 * Created by SEED on 2016/1/8.
 */
public class OrderCostModel implements Serializable{
    public int duration;// 授课时长(分钟)
    public double costmoney;// 费用
    public double redmoney;// 红包
    public double money;// 实际费用
    public String servicetel;// 客服电话
    public boolean isredvalid;// 红包是否过期（未选择红包时也是true）
    public boolean isredused;// 是否使用红包
}
