package com.ourslook.zuoyeba.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ourslook.zuoyeba.R;

/**
 * Created by jpj on 2017/2/18.
 */

public class RegisteDialog extends Dialog {

    private TextView dialogregisteconfirm;
    private View.OnClickListener listener;
    private TextView message;

    public RegisteDialog(Context context, int themeResId , View.OnClickListener listener ) {
        super(context, themeResId);
        this.listener = listener;
        setCustomDialog();
    }

    private void setCustomDialog() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_regist_success, null);
        dialogregisteconfirm = (TextView) v.findViewById(R.id.dialog_registe_confirm);
        message = (TextView) v.findViewById(R.id.dialog_register_message);
        dialogregisteconfirm.setOnClickListener(listener);
        super.setContentView(v);
    }

    public void setText(String t){
        if(message !=null){
            message.setText(t);
        }
    }
}
