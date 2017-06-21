package com.ourslook.zuoyeba.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by SEED on 2016/1/5.
 */
public class OrderTeacherDetailModel implements Serializable{
    public long id ;// 老师ID
    public String name;// 老师名字
    public int sex;// 老师性别：1:男 2:女
    public String photourl;// 老师头像
    public String grade ;// 年级名称
    public List<CourseModel> courseList;// 科目列表
    public String level;// 老师级别
    public String graduate;// 毕业院校
    public int schoolage;// 教龄
    public boolean isQC;// 是否资质认证
    public boolean isPC;// 是否平台认证
    public String signature;// 个性签名

    public double getStar() {
        return star;
    }

    public double star;// 老师星级

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSex() {
        return sex;
    }

    public String getPhotourl() {
        return photourl;
    }

    public String getGrade() {
        return grade;
    }

    public List<CourseModel> getCourseList() {
        return courseList;
    }

    public String getLevel() {
        return level;
    }

    public String getGraduate() {
        return graduate;
    }

    public int getSchoolage() {
        return schoolage;
    }

    public boolean isQC() {
        return isQC;
    }

    public boolean isPC() {
        return isPC;
    }

    public String getSignature() {
        return signature;
    }


}
