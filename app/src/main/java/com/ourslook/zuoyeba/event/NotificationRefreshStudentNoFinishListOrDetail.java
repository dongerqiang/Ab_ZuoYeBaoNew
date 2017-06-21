package com.ourslook.zuoyeba.event;

/**
 * 根据推送如果是老师已出发的推送时刷新学生端未完成订单列表或详情页状态
 * Created by DuanLu on 2016/9/3.
 */
public class NotificationRefreshStudentNoFinishListOrDetail {
    public String type;

    public NotificationRefreshStudentNoFinishListOrDetail(String type) {
        this.type = type;
    }

}
