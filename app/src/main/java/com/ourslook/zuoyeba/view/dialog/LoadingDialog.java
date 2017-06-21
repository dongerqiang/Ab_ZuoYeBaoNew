package com.ourslook.zuoyeba.view.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.ourslook.zuoyeba.R;


/**
 * Created by Administrator on 2015/11/1.
 * 用于加载的Dialog
 */
public class LoadingDialog extends BaseDialog {

    private static LoadingDialog mDialog;
    TextView mText;

    public LoadingDialog(Context context) {
        super(context, R.layout.dialog_loading);
        init();

    }

    public LoadingDialog setText(String str) {
        mText.setText(str);
        return this;
    }

    /**
     * 初始化控件绑定事件
     */
    private void init() {
        mText = getViewById(R.id.tv_dialog_loading);
    }

    public static void showLoadingDialog(Context context) {
        showLoadingDialog(context, null);
    }

    public static void showLoadingDialog(Context context, String message) {
        if (mDialog == null) {
            mDialog = new LoadingDialog(context);
            if (!TextUtils.isEmpty(message)) {
                mDialog.setText(message);
            }
            mDialog.show();
        } else {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            mDialog = null;
            showLoadingDialog(context, message);
        }
    }

    public static void dismissLoadingDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }

    }
}
