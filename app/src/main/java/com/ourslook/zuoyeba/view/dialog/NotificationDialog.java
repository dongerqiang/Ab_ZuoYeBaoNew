package com.ourslook.zuoyeba.view.dialog;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ourslook.zuoyeba.R;

import butterknife.Bind;

/**
 * 老师已出发提示框
 * Created by DuanLu on 2016/9/3.
 */
public class NotificationDialog extends BaseDialog implements CompoundButton.OnClickListener {
    @Bind(R.id.tv_message)
    TextView mTvMeaasge;
    @Bind(R.id.tv_dialogEnd_ok)
    TextView mTvKnow;
    String mMessage;

    public NotificationDialog(Context context, String message) {
        super(context, R.layout.dialog_notification);
        mMessage = message;
        initView();
    }

    private void initView() {
        mTvMeaasge.setText(mMessage);
        setOnClickListeners(this, mTvKnow);
    }

    public void setMessage(String message) {
        mTvMeaasge.setText(message);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

}
