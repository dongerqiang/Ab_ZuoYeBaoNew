package com.ourslook.zuoyeba.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.utils.StringUtils;


/**
 * Created by SEED on 2015/12/18.
 * 授课结束
 */
public class EndDialog extends Dialog {
    private TextView tv_ok;
    private TextView tv_time;
    private TextView tv_price;
    private TextView tv_red;
    private TextView tv_realPrice;
    private TextView tv_stel;

    //音频播放器
    private MediaPlayer mMediaPlayer;
    Context mContext;


    private View.OnClickListener listener;

    public EndDialog(Context context, int themeResId, View.OnClickListener listener) {
        super(context, themeResId);
        mContext = context;
        this.listener = listener;
        setCustomDialog();
    }

    private void setCustomDialog() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_endclass, null);
        tv_ok = (TextView) v.findViewById(R.id.tv_dialogEnd_ok);
        tv_time = (TextView) v.findViewById(R.id.tv_dialogEnd_time);
        tv_price = (TextView) v.findViewById(R.id.tv_dialogEnd_price);
        tv_red = (TextView) v.findViewById(R.id.tv_dialogEnd_red);
        tv_realPrice = (TextView) v.findViewById(R.id.tv_dialogEnd_realPrice);
        tv_stel = (TextView) v.findViewById(R.id.tv_dialogEnd_stel);

        tv_ok.setOnClickListener(listener);

        super.setContentView(v);
    }

    public void setText(int time, double price, double red, double realPrice, String stel) {
        tv_time.setText(time + "分钟");
        tv_price.setText("￥" + StringUtils.formatCurrency2String(price));
        tv_red.setText("￥" + StringUtils.formatCurrency2String(red));
        tv_realPrice.setText("￥" + StringUtils.formatCurrency2String(realPrice));
        tv_stel.setText(stel);
    }

}
