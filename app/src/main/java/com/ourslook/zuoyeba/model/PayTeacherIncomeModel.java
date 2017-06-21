package com.ourslook.zuoyeba.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by SEED on 2016/1/14.
 */
public class PayTeacherIncomeModel implements Serializable {
    public double balancemoney ;// 平台余额
    public double monthmoney;// 本月当前实际总收益
    public PageModel page;// 分页信息
    public List<PayTeacherIncomeDetailModel> incomeList;// 本月收益列表

}
