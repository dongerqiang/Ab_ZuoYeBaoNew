package com.ourslook.zuoyeba.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by SEED on 2016/1/5.
 */
public class OrderPageModel implements Serializable{

    public PageModel page;// 分页信息
    public List<OrderModel> orderList;// 订单列表

}
