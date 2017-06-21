package com.ourslook.zuoyeba.model;

import java.io.Serializable;

/**
 * 版本更新实体
 * Created by SEED on 2016/1/19.
 */
public class CommonVersionModel implements Serializable {
    public String version;// 版本名称
    public String url;// 路径
    public boolean must;//是否强制更新
}
