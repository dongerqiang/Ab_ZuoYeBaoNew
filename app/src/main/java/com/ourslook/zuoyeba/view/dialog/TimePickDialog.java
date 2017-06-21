package com.ourslook.zuoyeba.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.utils.DateUtils;
import com.ourslook.zuoyeba.utils.DisplayUtils;
import com.ourslook.zuoyeba.view.ScrollerNumberPicker;

import java.util.ArrayList;

/**
 * Created by SEED on 2015/12/18.
 * 时间选择Dialog
 */
public class TimePickDialog extends Dialog {

    private TextView tv_ok;
    public ScrollerNumberPicker pickDate;
    public ScrollerNumberPicker pickHour;
    public ScrollerNumberPicker pickMinute;
    public TextView tv_maohao;
    private ScrollerNumberPicker.OnSelectListener onSelectListener1;
    private ScrollerNumberPicker.OnSelectListener onSelectListener2;
    private ScrollerNumberPicker.OnSelectListener onSelectListener3;
    private View.OnClickListener listener;

    private ArrayList<String> days = new ArrayList<>();
    private ArrayList<String> hours = new ArrayList<>();
    private ArrayList<String> mins = new ArrayList<>();
    private String[] hs = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
    private String[] ms = {"00", "10", "20", "30", "40", "50"};
    private Context context;

    public TimePickDialog(Context context, int themeResId, View.OnClickListener listener, ScrollerNumberPicker.OnSelectListener onSelectListener1, ScrollerNumberPicker.OnSelectListener onSelectListener2, ScrollerNumberPicker.OnSelectListener onSelectListener3) {
        super(context, themeResId);
        this.onSelectListener1 = onSelectListener1;
        this.onSelectListener2 = onSelectListener2;
        this.onSelectListener3 = onSelectListener3;
        this.listener = listener;
        this.context=context;


        long l = System.currentTimeMillis();
        days.add(0, "立即");
        for (int i = 0; i < 30; i++) {
            days.add(DateUtils.formateDateLongToStringOnlyDate2(l));
            l += 86400 * 1000;
        }
        for (int i = 0; i < hs.length; i++) {
            hours.add(hs[i]);
        }
        for (int i = 0; i < ms.length; i++) {
            mins.add(ms[i]);
        }
        setCustomDialog();
    }

    private void setCustomDialog() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_pick_time, null);
        tv_ok = (TextView) v.findViewById(R.id.tv_dialogTime_ok);
        tv_ok.setOnClickListener(listener);
        pickDate = (ScrollerNumberPicker) v.findViewById(R.id.pick_dialogTime_date);
        pickDate.setOnSelectListener(onSelectListener1);
        pickDate.setData(days);
        pickDate.setDefault(0);

        tv_maohao = (TextView) v.findViewById(R.id.tv_maohao);
        pickHour = (ScrollerNumberPicker) v.findViewById(R.id.pick_dialogTime_hour);
        pickHour.setOnSelectListener(onSelectListener2);
        pickHour.setData(hours);
        pickHour.setDefault(hs.length / 2);

        pickMinute = (ScrollerNumberPicker) v.findViewById(R.id.pick_dialogTime_minute);
        pickMinute.setOnSelectListener(onSelectListener3);
        pickMinute.setData(mins);
        pickMinute.setDefault(ms.length / 2);

        pickHour.setVisibility(View.GONE);
        pickMinute.setVisibility(View.GONE);
        pickDate.setVisibility(View.VISIBLE);
        tv_maohao.setVisibility(View.INVISIBLE);
        super.setContentView(v);
    }

    public String getText() {

        return pickDate.getSelectedText() + "\t" + pickHour.getSelectedText() + ":" + pickMinute.getSelectedText();
    }

    public String getTime() {
        return pickHour.getSelectedText() + pickMinute.getSelectedText();
    }
}
