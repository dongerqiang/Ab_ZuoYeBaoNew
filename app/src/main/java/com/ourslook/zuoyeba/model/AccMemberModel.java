package com.ourslook.zuoyeba.model;

import java.io.Serializable;

/**
 * Created by SEED on 2015/12/24.
 */
public class AccMemberModel implements Serializable{
    public String token;// 登录token
    public String name;// 会员名字
    public int sex;// 1:男 2:女
    public String mobile;// 手机号
    public int provinceid;// 省ID
    public int cityid;// 市ID
    public int regionid;// 区ID
    public String province;// 省名称
    public String city;// 市名称
    public String region;// 区名称
    public String address;// 地址
    public String photourl;// 会员头像
    public int type;// 1:学生 2:老师
    public String imacc;// 环信账号
    public String impwd;// 环信密码
    public AccStudentModel student;// 学生model
    public AccTeacherModel teacher;// 老师model


}
