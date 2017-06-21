package com.ourslook.zuoyeba.model;

import java.io.Serializable;

/**
 * Created by SEED on 2016/1/5.
 */
public class OrderTeacherModel implements Serializable{
    public long id;// 老师ID
    public String name;// 老师名字
    public String photourl;// 老师头像
    public int sex;// 老师性别
    public String level;// 老师级别
    public double coordinatex;// 经度
    public double coordinatey;// 纬度

    public String getPhotourl() {
        return photourl;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSex() {
        return sex;
    }

    public String getLevel() {
        return level;
    }

    public double getCoordinatex() {
        return coordinatex;
    }

    public double getCoordinatey() {
        return coordinatey;
    }
}
