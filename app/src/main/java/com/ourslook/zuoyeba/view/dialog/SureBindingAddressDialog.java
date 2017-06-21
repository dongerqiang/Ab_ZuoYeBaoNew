package com.ourslook.zuoyeba.view.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.activity.DetailInfoActivity;

import butterknife.Bind;

/**
 * Created by DuanLu on 2016/6/24.
 */
public class SureBindingAddressDialog extends BaseDialog implements View.OnClickListener {
    @Bind(R.id.btn_sure)
    Button mBtnSure;
    @Bind(R.id.btn_cancel)
    Button mBtnCancel;

    public SureBindingAddressDialog(Context context) {
        super(context, R.layout.dialog_sure_has_address);

        setOnClickListeners(this, mBtnSure, mBtnCancel);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sure:
                openActivity(DetailInfoActivity.class);
                dismiss();
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
        }
    }
}
