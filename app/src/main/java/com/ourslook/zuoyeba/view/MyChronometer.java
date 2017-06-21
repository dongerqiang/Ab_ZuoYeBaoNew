package com.ourslook.zuoyeba.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Chronometer;

public class MyChronometer extends Chronometer {
    int miss = 0;

    public MyChronometer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //this.setFormat("HH:mm:ss");
        this.setOnChronometerTickListener(new OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                miss++;
                MyChronometer.this.setText(FormatMiss(miss));
            }
        });
    }

    public MyChronometer(Context context, AttributeSet attrs) {
        super(context, attrs);
        // this.setFormat("HH:mm:ss");
        this.setOnChronometerTickListener(new OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                miss++;
                MyChronometer.this.setText(FormatMiss(miss));
            }
        });
    }

    public MyChronometer(Context context) {
        super(context);
        // this.setFormat("HH:mm:ss");
        this.setOnChronometerTickListener(new OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                miss++;
                MyChronometer.this.setText(FormatMiss(miss));
            }
        });
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        //屏幕隐藏时继续计时
        visibility = View.VISIBLE;
        super.onWindowVisibilityChanged(visibility);
    }

    @Override
    public void setOnChronometerTickListener(OnChronometerTickListener listener) {
        super.setOnChronometerTickListener(listener);
    }

//    @Override
//    public OnChronometerTickListener getOnChronometerTickListener() {
//        miss++;
//        this.setText(FormatMiss(miss));
//        return super.getOnChronometerTickListener();
//    }

    public static String FormatMiss(int miss) {
        String hh = miss / 3600 > 9 ? miss / 3600 + "" : "0" + miss / 3600;
        String mm = (miss % 3600) / 60 > 9 ? (miss % 3600) / 60 + "" : "0" + (miss % 3600) / 60;
        String ss = (miss % 3600) % 60 > 9 ? (miss % 3600) % 60 + "" : "0" + (miss % 3600) % 60;
        return hh + ":" + mm + ":" + ss;
    }

}
