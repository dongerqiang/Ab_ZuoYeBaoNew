package com.ourslook.zuoyeba.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ourslook.zuoyeba.R;

/**
 * 提示弹框（登录/绑定支付宝）
 * Created by SEED on 2015/12/18.
 *
 */
public class WarningDailog extends Dialog {
    private TextView tv_warn;
    private View ll_warn;
    private View.OnClickListener listener;

    public WarningDailog(Context context, int themeResId ,View.OnClickListener listener ) {
        super(context, themeResId);
        this.listener = listener;
        setCustomDialog();
    }

    private void setCustomDialog() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_warn, null);
        tv_warn = (TextView) v.findViewById(R.id.tv_dialogWarn_warn);
        ll_warn = v.findViewById(R.id.ll_dialogWarn);

        ll_warn.setOnClickListener(listener);

        super.setContentView(v);
    }

    public void setText(String t){
        tv_warn.setText(t);
    }
}
