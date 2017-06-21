package com.ourslook.zuoyeba.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.view.ScrollerNumberPicker;

import java.util.ArrayList;

/**
 * Created by SEED on 2015/12/18.
 */
public class GradePickDialog extends Dialog{

    private TextView tv_ok;
    private ScrollerNumberPicker picker;
    private ScrollerNumberPicker.OnSelectListener onSelectListener;
    private View.OnClickListener listener;
    private ArrayList<String> data;

    public GradePickDialog(Context context, int themeResId, ArrayList<String> data, View.OnClickListener listener, ScrollerNumberPicker.OnSelectListener onSelectListener ) {
        super(context, themeResId);
        this.onSelectListener = onSelectListener;
        this.listener = listener;
        this.data = data;
        setCustomDialog();
    }

    private void setCustomDialog() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_pick_grade, null);
        tv_ok = (TextView) v.findViewById(R.id.tv_dialogGrade_ok);
        tv_ok.setOnClickListener(listener);
        picker = (ScrollerNumberPicker) v.findViewById(R.id.pick_dialogGarde_grade);
        picker.setOnSelectListener(onSelectListener);

        if (data.size()>0){

            picker.setData(data);
            picker.setDefault(0);
        }

        super.setContentView(v);
    }

    public String getText(){
        return picker.getSelectedText();
    }
    public int getId(){
        return picker.getSelected();
    }

    public void setFirst(int index){
        picker.setDefault(index);
    }
}
