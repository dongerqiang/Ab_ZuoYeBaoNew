package com.ourslook.zuoyeba.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ourslook.zuoyeba.R;


/**
 * Created by SEED on 2015/12/23.
 * 删除对话框
 */
public class DelDialog extends Dialog {
    private TextView tv_del;
    private View.OnClickListener listener;

    public DelDialog(Context context, int themeResId , View.OnClickListener listener ) {
        super(context, themeResId);
        this.listener = listener;
        setCustomDialog();
    }

    private void setCustomDialog() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_del, null);
        tv_del = (TextView) v.findViewById(R.id.tv_dialog_del);

        tv_del.setOnClickListener(listener);

        super.setContentView(v);
    }

    public void setText(String t){
        tv_del.setText(t);
    }
}