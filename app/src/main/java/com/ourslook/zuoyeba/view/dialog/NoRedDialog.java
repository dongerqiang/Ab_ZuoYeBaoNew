package com.ourslook.zuoyeba.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.ourslook.zuoyeba.R;

/**
 * Created by SEED on 2015/12/31.
 * 没有红包
 */
public class NoRedDialog extends Dialog {
    private View view_noRed;
    private View.OnClickListener listener;

    public NoRedDialog(Context context, int themeResId, View.OnClickListener listener) {
        super(context, themeResId);
        this.listener = listener;
        setCustomDialog();
    }

    private void setCustomDialog() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_nored, null);
        view_noRed = v.findViewById(R.id.view_dialogNoRed);
        view_noRed.setOnClickListener(listener);

        super.setContentView(v);
    }

}
