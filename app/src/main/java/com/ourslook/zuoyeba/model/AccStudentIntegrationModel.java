package com.ourslook.zuoyeba.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by SEED on 2016/1/18.
 */
public class AccStudentIntegrationModel implements Serializable {
    public PageModel page;// 分页信息
    public long totalintegration;// 总积分
    public List<AccStudentIntegrationDetailModel> integrationList;// 积分记录列表

}
