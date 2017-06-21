package com.ourslook.zuoyeba.view.dialog;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.model.CommonAreaModel;

import java.lang.reflect.Field;

import butterknife.Bind;

/**
 * Created by huangyi on 16/5/17.
 * 选择地区 Dialog
 */
public class ChooseLocalDialog extends BaseDialog implements View.OnClickListener, NumberPicker.OnValueChangeListener {
    @Bind(R.id.btn_sure_choose_local_dialog)
    Button btnSureChooseLocalDialog;
    @Bind(R.id.nmb_choose_local_province)
    NumberPicker nmbChooseLocalProvince;
    @Bind(R.id.nmb_choose_local_city)
    NumberPicker nmbChooseLocalCity;
    @Bind(R.id.nmb_choose_local_area)
    NumberPicker nmbChooseLocalArea;

    CommonAreaModel data;

    String[] chooseProvince;
    String[] chooseCity;
    String[] chooseArea;

    int provinceIndex;
    int cityIndex;
    int areaIndex;

    OnChooseLocalListener mOnChooseLocalListener;
    @Bind(R.id.ll_chooseLocal_back)
    LinearLayout llChooseLocalBack;

    public ChooseLocalDialog(Context context, CommonAreaModel data, OnChooseLocalListener mOnChooseLocalListener) {
        super(context, R.layout.dailog_choose_local);
        this.data = data;
        this.mOnChooseLocalListener = mOnChooseLocalListener;
        init();
    }

    /**
     * 进行初始化
     */
    private void init() {
        //不弹出输入框
        nmbChooseLocalProvince.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        nmbChooseLocalCity.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        nmbChooseLocalArea.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        changePickLine(nmbChooseLocalProvince, mContext.getResources().getColor(R.color.colorPrimary));
        changePickLine(nmbChooseLocalCity, mContext.getResources().getColor(R.color.colorPrimary));
        changePickLine(nmbChooseLocalArea, mContext.getResources().getColor(R.color.colorPrimary));
        chooseProvince = new String[data.provinceList.size()];
        for (int i = 0; i < data.provinceList.size(); i++) {
            chooseProvince[i] = data.provinceList.get(i).name;
        }
        nmbChooseLocalProvince.setDisplayedValues(chooseProvince);
        nmbChooseLocalProvince.setMaxValue(chooseProvince.length - 1);

        nmbChooseLocalCity.setOnValueChangedListener(this);
        nmbChooseLocalProvince.setOnValueChangedListener(this);
        setOnClickListeners(this, btnSureChooseLocalDialog,llChooseLocalBack);
        handleData();

    }

    /**
     * 处理数据
     */
    private void handleData() {
        chooseCity = new String[data.provinceList.get(provinceIndex).cityList.size()];
        for (int i = 0; i < data.provinceList.get(provinceIndex).cityList.size(); i++) {
            chooseCity[i] = data.provinceList.get(provinceIndex).cityList.get(i).name;
        }
        if (data.provinceList.get(provinceIndex).cityList.size() == 0) {//是否存在市
            chooseArea = new String[0];
        } else {
            if (data.provinceList.get(provinceIndex).cityList.get(cityIndex).regionList.size() == 0) {//是否存在区
                chooseArea = new String[0];
            } else {
                chooseArea = new String[data.provinceList.get(provinceIndex).cityList.get(cityIndex).regionList.size()];
            }
        }


        for (int i = 0; i < chooseArea.length; i++) {
            chooseArea[i] = data.provinceList.get(provinceIndex).cityList.get(cityIndex).regionList.get(i).name;
        }

        if (chooseCity.length == 0) {
            nmbChooseLocalCity.setDisplayedValues(new String[]{" "});
        } else {
            nmbChooseLocalCity.setDisplayedValues(chooseCity);
        }

        if (chooseArea.length == 0) {
            nmbChooseLocalArea.setDisplayedValues(new String[]{" "});
        } else {
            nmbChooseLocalArea.setDisplayedValues(chooseArea);
        }

        if (chooseCity.length > 0) {
            nmbChooseLocalCity.setMaxValue(chooseCity.length - 1);
        }

        if (chooseArea.length > 0) {
            nmbChooseLocalArea.setMaxValue(chooseArea.length - 1);
        }

    }


    /**
     * 设置分割线颜色
     *
     * @param myPicker
     * @param color
     */
    private void changePickLine(NumberPicker myPicker, int color) {
        NumberPicker picker = myPicker;
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    //设置分割线的颜色值
                    pf.set(picker, new ColorDrawable(color));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sure_choose_local_dialog:
                int areaid;
                String province = data.provinceList.get(provinceIndex).name;
                String city = "";
                String area = "";
                if (data.provinceList.get(provinceIndex).cityList.size() > 0) {//有市
                    city = data.provinceList.get(provinceIndex).cityList.get(cityIndex).name;
                    if (data.provinceList.get(provinceIndex).cityList.get(cityIndex).regionList.size() > 0) {//有区
                        areaid = data.provinceList.get(provinceIndex).cityList.get(cityIndex).regionList.get(nmbChooseLocalArea.getValue()).id;
                        area = data.provinceList.get(provinceIndex).cityList.get(cityIndex).regionList.get(nmbChooseLocalArea.getValue()).name;
                    } else {//没有区
                        areaid = (int) data.provinceList.get(provinceIndex).cityList.get(cityIndex).id;

                    }

                } else {//没有市
                    areaid = data.provinceList.get(provinceIndex).id;
                }
                mOnChooseLocalListener.getChooseInfo(province, city, area, areaid);
                dismiss();
                break;
            case R.id.ll_chooseLocal_back:
                dismiss();
                break;
        }
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        if (picker == nmbChooseLocalProvince) {
            provinceIndex = picker.getValue();
            cityIndex = 0;
            nmbChooseLocalCity.setMaxValue(0);
            nmbChooseLocalArea.setMaxValue(0);

        } else if (picker == nmbChooseLocalCity) {
            cityIndex = picker.getValue();
            areaIndex = 0;
            nmbChooseLocalArea.setMaxValue(0);
        }
        handleData();
    }

    public interface OnChooseLocalListener {
        void getChooseInfo(String province, String city, String area, int areaId);
    }
}
