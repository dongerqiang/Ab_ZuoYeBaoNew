package com.ourslook.zuoyeba.event;

/**
 * Created by huangyi on 16/5/25.
 * //用于在 点击学生未完成订单的时候检查权限回调传递下标
 */
public class StudentNoFinishListCheckPermissEvent {
    public  int position;
    public StudentNoFinishListCheckPermissEvent( int position){
        this.position=position;
    }
}
