package com.ourslook.zuoyeba.model;

import java.io.Serializable;

/**
 * Created by SEED on 2016/3/25.
 */
public class PayBindAlipayModel implements Serializable{
    private String sign;//	String	签名
    private String _input_charset;//	String	参数编码字符集
    private String product_code;//	String	产品码
    private String sign_type;//	String	签名方式
    private String external_sign_no	;//String	商户签约号
    private String service;//	String	接口名称
    private String access_info;//	String	接入信息
    private String scene	;//String	签约场景
    private String partner;//	String	商户id
    private String notify_url;//	String	服务器异步通知页面路径

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String get_input_charset() {
        return _input_charset;
    }

    public void set_input_charset(String _input_charset) {
        this._input_charset = _input_charset;
    }

    public String getProduct_code() {
        return product_code;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public String getExternal_sign_no() {
        return external_sign_no;
    }

    public void setExternal_sign_no(String external_sign_no) {
        this.external_sign_no = external_sign_no;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getAccess_info() {
        return access_info;
    }

    public void setAccess_info(String access_info) {
        this.access_info = access_info;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }
}
