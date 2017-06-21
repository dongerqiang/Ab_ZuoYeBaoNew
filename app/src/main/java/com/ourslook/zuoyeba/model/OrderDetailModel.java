package com.ourslook.zuoyeba.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by SEED on 2016/1/5.
 */
public class OrderDetailModel implements Serializable {

    public long id;// 订单ID
    public String studentname;// 学生姓名
    public String studentphotourl;// 学生头像
    public String studentimacc;// 学生环信账号
    public String teachername;// 老师姓名
    public String teacherphotourl;// 老师头像
    public String teacherimacc;// 老师环信账号
    public String grade;// 年级名称
    public String course;// 课程名称
    public int type;// 授课方式：1:电话 2:视频 3:上门
    public int status;// 状态(1:抢单中 2:已确认教师/承接中 3:进行中 4:已结束 5:已作废)
    public long time;// 下单时间
    public long teachingtime;// 授课时间（下单时填写）
    public long teachingtimestart;// 实际授课开始时间
    public long teachingtimeend;// 实际授课结束时间
    public long duration;// 授课时长
    public String address;// 授课地址
    public double money;// 金额(学生端为订单金额，老师端为实际收益)
    public double price;// 课程单价(只有老师端有)
    public String studenttel;// 学生电话(只有老师端有)
    public List<String> imgList;// 问题照片url列表(只有老师端有)
    public boolean isleave;// 是否已经出发
    public boolean iscomment;// 是否已经评价(仅学生端)
    public int commentid;// 评价类型ID1好评;2中评;3差评
    public String commentcontent;// 评语

    //TODO 2016年8月31日加，用于抢单列表进去的详情页查看学生位置
    public String studentCoordinateX;    //上门订单学生目标坐标 X
    public String studentCoordinateY;    //上门订单学生目标坐标 Y
    public int grabStatus;//抢单状态(0:立即抢单，1:确认中，2：已确认)


}
