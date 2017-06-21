package com.ourslook.zuoyeba.view.dialog;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.model.OrderCostModel;
import com.ourslook.zuoyeba.utils.StringUtils;

import butterknife.Bind;

/**
 * Created by DuanLu on 2016/9/1.
 */
public class TeacherEndDialog extends BaseDialog implements CompoundButton.OnClickListener {
    @Bind(R.id.tv_dialogEnd_time)
    TextView mTvTime;//时间
    @Bind(R.id.tv_dialogEnd_price)
    TextView mTvPrice;//收入
    @Bind(R.id.tv_dialogEnd_stel)
    TextView mTvPhone;//客服
    @Bind(R.id.tv_dialogEnd_ok)
    TextView mTvKnow;//我知道了
    OrderCostModel orderCostModel;// 结束授课弹框内容实体

    //音频播放器
    private MediaPlayer mMediaPlayer;

    public TeacherEndDialog(Context context, OrderCostModel orderCostModel) {
        super(context, R.layout.dialog_teacher_end);
        this.orderCostModel = orderCostModel;

        initView();
    }

    private void initView() {
        mTvTime.setText(orderCostModel.duration + "分钟");
        mTvPrice.setText("￥" + StringUtils.formatCurrency2String(orderCostModel.money));
        mTvPhone.setText(orderCostModel.servicetel);

        setOnClickListeners(this, mTvKnow);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_dialogEnd_ok:
                dismiss();
                break;
        }
    }

}
