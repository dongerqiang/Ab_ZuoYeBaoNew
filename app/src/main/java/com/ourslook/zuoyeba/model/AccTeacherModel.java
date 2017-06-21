package com.ourslook.zuoyeba.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by SEED on 2015/12/24.
 */
public class AccTeacherModel implements Serializable{
    public long gradeid;// 年级ID
    public String grade;// 年级名称
    public ArrayList<CourseModel> courseList;// 科目列表
    public long levelid;// 老师级别ID
    public String level;// 老师级别名称
    public String graduate;// 毕业院校
    public int schoolage;// 教龄
    public boolean isQC;// 是否资质认证
    public boolean isPC;// 是否平台认证
    public String signature;// 个性签名
    public double money;// 平台余额
    public String star;// 老师星级

}
