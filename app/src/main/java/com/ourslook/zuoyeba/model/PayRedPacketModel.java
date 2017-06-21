package com.ourslook.zuoyeba.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by SEED on 2016/1/14.
 */
public class PayRedPacketModel implements Serializable{
    public PageModel page;// 分页信息
    public List<PayRedPacketDetailModel> redpacketList;// 红包列表

}
