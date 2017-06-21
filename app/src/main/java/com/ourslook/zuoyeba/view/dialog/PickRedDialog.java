package com.ourslook.zuoyeba.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ourslook.zuoyeba.R;

/**
 * Created by SEED on 2015/12/31.
 * 选择红包dialog
 */
public class PickRedDialog extends Dialog {
    private View view_ok;
    private TextView tv_money;
    private TextView tv_no;
    private View.OnClickListener listener;

    public PickRedDialog(Context context, int themeResId, View.OnClickListener listener) {
        super(context, themeResId);
        this.listener = listener;
        setCustomDialog();
    }

    private void setCustomDialog() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_pickred, null);
        view_ok = v.findViewById(R.id.view_dialogPickRed_ok);
        tv_money = (TextView) v.findViewById(R.id.tv_dialogPickRed_money);
        tv_no = (TextView) v.findViewById(R.id.tv_dialogPickRed_no);

        view_ok.setOnClickListener(listener);
        tv_no.setOnClickListener(listener);

        super.setContentView(v);
    }

    public void setMoney(String t) {
        tv_money.setText(t);
    }
}
