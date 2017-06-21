package com.ourslook.zuoyeba.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by SEED on 2015/12/24.
 */
public class AreaCityModel implements Serializable{
    public long id;// 市id
    public String name;// 市名称
    public String letter;// 市第一个字母
    public ArrayList<AreaRegionModel> regionList;// 地区列表

}
