package com.ourslook.zuoyeba.model;

import java.io.Serializable;

/**
 * Created by SEED on 2015/12/24.
 */
public class AccStudentModel implements Serializable{
    public long gradeid;// 年级ID
    public String grade;// 年级名称
    public int integration;// 消费积分
    public boolean iscreditvalid;// 是否已绑定支付宝

}
