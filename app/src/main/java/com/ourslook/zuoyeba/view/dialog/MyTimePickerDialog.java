package com.ourslook.zuoyeba.view.dialog;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.utils.DateUtils;
import com.ourslook.zuoyeba.utils.ToastUtil;
import com.ourslook.zuoyeba.view.dialog.BaseDialog;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.Bind;

/**
 * Created by huangyi on 16/5/20.
 * 用于时间选择   立即...
 */
public class MyTimePickerDialog extends BaseDialog implements NumberPicker.OnValueChangeListener,View.OnClickListener {

    @Bind(R.id.btn_sure_choose_Time_dialog)
    Button btnSureChooseTimeDialog;
    @Bind(R.id.nmb_choose_Time_province)
    NumberPicker nmbChooseTimeDate;
    @Bind(R.id.nmb_choose_Time_city)
    NumberPicker nmbChooseTimeHour;
    @Bind(R.id.nmb_choose_Time_area)
    NumberPicker nmbChooseTimeMinute;
    @Bind(R.id.ll_chooseTime_back)
    public
    LinearLayout llChooseTimeBack;

    private String[] hourData = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
    private String[] minuteData = {"00", "10", "20", "30", "40", "50"};
    private String[] dateData;
    public MyTimePickerDialog(Context context,OnChooseTimeListener mOnChooseTimeListener ) {
        super(context, R.layout.dialog_my_choose_time);
        this.mOnChooseTimeListener=mOnChooseTimeListener;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        long l = System.currentTimeMillis();
        dateData=new String[30];
        dateData[0]="立即";
        for(int i=1;i<dateData.length;i++){
            dateData[i]=DateUtils.formateDateLongToStringOnlyDate2(l);
            l += 86400 * 1000;
        }
        nmbChooseTimeDate.setDisplayedValues(dateData);
        nmbChooseTimeDate.setMaxValue(dateData.length-1);
        nmbChooseTimeDate.setWrapSelectorWheel(false);
        nmbChooseTimeDate.setOnValueChangedListener(this);
        changePickLine(nmbChooseTimeDate,R.color.colorPrimary);

        nmbChooseTimeHour.setDisplayedValues(hourData);
        nmbChooseTimeHour.setMaxValue(hourData.length-1);
        changePickLine(nmbChooseTimeHour,R.color.colorPrimary);

        nmbChooseTimeMinute.setDisplayedValues(minuteData);
        nmbChooseTimeMinute.setMaxValue(minuteData.length-1);
        changePickLine(nmbChooseTimeMinute,R.color.colorPrimary);

        setOnClickListeners(this,btnSureChooseTimeDialog,llChooseTimeBack);

        //不弹出输入框
        nmbChooseTimeDate.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        nmbChooseTimeHour.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        nmbChooseTimeMinute.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        //默认当天时间12:30
//        nmbChooseTimeDate.setValue(1);
//        nmbChooseTimeHour.setValue(12);
//        nmbChooseTimeMinute.setValue(3);
        nmbChooseTimeHour.setVisibility(View.GONE);
        nmbChooseTimeMinute.setVisibility(View.GONE);
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        if(picker.getValue()==0){
            nmbChooseTimeHour.setVisibility(View.GONE);
            nmbChooseTimeMinute.setVisibility(View.GONE);
        }else{
            nmbChooseTimeHour.setVisibility(View.VISIBLE);
            nmbChooseTimeMinute.setVisibility(View.VISIBLE);
        }
    }
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
        switch (v.getId()){
            case R.id.btn_sure_choose_Time_dialog://sure
                if(nmbChooseTimeDate.getValue()==0){
                    Calendar calendar=Calendar.getInstance();
                    calendar.setTimeInMillis(calendar.getTimeInMillis()+1000*600);
                    mOnChooseTimeListener.onChoose(String.format("%1$d年%2$02d月%3$02d日\t%4$02d:%5$02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1,
                            calendar.get(Calendar.DATE),calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE)));
                }else {
                    mOnChooseTimeListener.onChoose(dateData[nmbChooseTimeDate.getValue()]+"\t"+hourData[nmbChooseTimeHour.getValue()]+":"+minuteData[nmbChooseTimeMinute.getValue()]);
                }
                dismiss();
                break;
            case R.id.ll_chooseTime_back:
//                dismiss();
                break;
        }
    }
    OnChooseTimeListener mOnChooseTimeListener;
    public interface OnChooseTimeListener{
        void onChoose(String timeString);
    }
}
