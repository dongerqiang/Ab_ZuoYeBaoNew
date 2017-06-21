package com.ourslook.zuoyeba.view.dialog;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.ourslook.zuoyeba.Constants;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.activity.TeacherOrderDetailActivity;

import butterknife.Bind;

/**
 * 学生端确认老师后老师端首页弹出的对话框
 * Created by DuanLu on 2016/9/18.
 */
public class TeacherMainAffirmOrderDialog extends BaseDialog implements View.OnClickListener {
    @Bind(R.id.btn_stay_put)
    Button mBtnStayPut;//留在该页面
    @Bind(R.id.btn_view_detail)
    Button mBtnViewDetail;//查看订单详情
    private long mOrderId;

    public TeacherMainAffirmOrderDialog(Context context, long orderId) {
        super(context, R.layout.dialog_teacher_main_affirm_order);
        mOrderId = orderId;
        initView();
    }

    private void initView() {
        setOnClickListeners(this, mBtnStayPut, mBtnViewDetail);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_stay_put://留在该页面
                dismiss();
                break;
            case R.id.btn_view_detail://查看订单详情
                Intent intent = new Intent(mContext, TeacherOrderDetailActivity.class);
                intent.putExtra(Constants.PASS_ORDER, mOrderId);
                mContext.startActivity(intent);
                dismiss();
                break;
        }
    }

}
