package com.ourslook.zuoyeba.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ourslook.zuoyeba.R;


/**
 * Created by SEED on 2015/12/18.
 * 选择照片
 */
public class PickDialogPic extends Dialog {
    private TextView tv_photo;
    private TextView tv_album;
    private View.OnClickListener listener;

    public PickDialogPic(Context context, int themeResId , View.OnClickListener listener ) {
        super(context, themeResId);
        this.listener = listener;
        setCustomDialog();
    }

    private void setCustomDialog() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_pick_picture, null);
        tv_photo = (TextView) v.findViewById(R.id.tv_dialogPic_photo);
        tv_album = (TextView) v.findViewById(R.id.tv_dialogPic_album);

        tv_photo.setOnClickListener(listener);
        tv_album.setOnClickListener(listener);

        super.setContentView(v);
    }

}
