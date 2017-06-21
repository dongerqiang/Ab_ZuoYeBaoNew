package com.ourslook.zuoyeba.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SEED on 2016/1/14.
 */
public class OrderPageTeacherModel implements Serializable{
    public PageModel page;// 分页信息
    public ArrayList<OrderTeacherModel> teacherList;// 订单列表

    public PageModel getPage() {
        return page;
    }

    public List<OrderTeacherModel> getTeacherList() {
        return teacherList;
    }
}
