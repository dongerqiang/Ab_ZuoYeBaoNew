package com.ourslook.zuoyeba.model;

import java.io.Serializable;

/**
 * Created by SEED on 2016/1/14.
 */
public class PayBankModel implements Serializable {
    private int bankid;// 银行ID
    private String bankname;// 银行名称

    public int getBankid() {
        return bankid;
    }

    public String getBankname() {
        return bankname;
    }
}
