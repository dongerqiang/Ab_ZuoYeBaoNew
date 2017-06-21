package com.ourslook.zuoyeba.model;

import java.io.Serializable;

/**
 * Created by SEED on 2016/1/5.
 */
public class OrderModel implements Serializable {

    public long id;// 订单ID
    public String studentname;// 学生姓名
    public String studentphotourl;// 学生头像
    public String teachername;// 老师姓名
    public String teacherphotourl;// 老师头像
    public String grade;// 年级名称
    public String course;// 课程名称
    public int type;// 授课方式：1:电话 2:视频 3:上门
    public int status;// 状态(1:抢单中 2:已确认教师/承接中 3:进行中 4:已结束 5:已作废)
    public int grabStatus;//抢单状态(0:立即抢单，1:确认中，2：已确认)
    public long time;// 下单时间
    public String studentimacc;// 学生环信账号
    public String teacherimacc;// 老师环信账号

    public boolean iscomment;

    public boolean isleave;// 是否已经出发


}
