package com.ourslook.zuoyeba.model;

import java.io.Serializable;

/**
 * Created by SEED on 2015/12/24.
 */
public class CourseModel implements Serializable{

    public long courseid;// 科目ID
    public String coursename;// 科目名称

    public long getCourseid() {
        return courseid;
    }

    public String getCoursename() {
        return coursename;
    }
}
