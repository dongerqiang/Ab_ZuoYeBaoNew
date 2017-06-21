package com.ourslook.zuoyeba.model;

import java.io.Serializable;

/**
 * Created by SEED on 2016/1/14.
 */
public class AccCreditCardModel implements Serializable {
    private boolean iscreditvalid;// 是否已绑定信用卡
    private int creditbankid;// 信用卡银行ID
    private String creditbankname;// 信用卡银行名称
    private String creditbankimgurl;// 信用卡银行图片路径
    private String creditnumber;// 信用卡号码
    private String creditname;// 信用卡持卡人姓名
    private String creditidcard;// 信用卡持卡人身份证
    private String creditmobile;// 信用卡预留手机号

    public String getCreditbankimgurl() {
        return creditbankimgurl;
    }

    public int getCreditbankid() {
        return creditbankid;
    }

    public String getCreditbankname() {
        return creditbankname;
    }

    public String getCreditidcard() {
        return creditidcard;
    }

    public String getCreditmobile() {
        return creditmobile;
    }

    public String getCreditname() {
        return creditname;
    }

    public String getCreditnumber() {
        return creditnumber;
    }

    public boolean iscreditvalid() {
        return iscreditvalid;
    }
}
