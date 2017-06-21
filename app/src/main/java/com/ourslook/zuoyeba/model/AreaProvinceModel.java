package com.ourslook.zuoyeba.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by SEED on 2015/12/24.
 */
public class AreaProvinceModel implements Serializable{
    public int id;// 省id
    public String name;// 省名称
    public ArrayList<AreaCityModel> cityList;// 市列表


}
