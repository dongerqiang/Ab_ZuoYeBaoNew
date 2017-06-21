package com.ourslook.zuoyeba.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by SEED on 2016/1/18.
 */
public class InfMessageModel implements Serializable {
    public PageModel page;// 分页信息
    public List<InfMessageDetailModel> messageList;// 积分记录列表

}
