package com.ourslook.zuoyeba.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.model.CoursePriceModel;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by SEED on 2015/12/18.
 * 选择授课方式
 */
public class TeachTypeDialog extends Dialog {
    //    private TextView tv_ok;
    private TextView tv_price;
    private TextView tv_priceD;
    private TextView tv_priceD2;

    private TextView tv_telephone;
    private TextView tv_video;
    private TextView tv_door;
    private TextView tv_startprice;

    private View ll_tel;
    private View ll_video;
    private View ll_goHome;
    private ImageView iv_tel;
    private ImageView iv_video;
    private ImageView iv_goHome;

    private View.OnClickListener listener;
    private List<CoursePriceModel> coursePriceList;
    private Context context;

    public TeachTypeDialog(Context context, int themeResId, List<CoursePriceModel> coursePriceList, View.OnClickListener listener) {
        super(context, themeResId);
        this.context = context;
        this.listener = listener;
        this.coursePriceList = coursePriceList;
        setCustomDialog();
    }

    private void setCustomDialog() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_pick_teachtype, null);

//        tv_ok = (TextView) v.findViewById(R.id.tv_dialogTeachType_ok);
        tv_price = (TextView) v.findViewById(R.id.tv_dialogTeachType_price);
        tv_priceD = (TextView) v.findViewById(R.id.tv_dialogTeachType_priceDetail);
        tv_priceD2 = (TextView) v.findViewById(R.id.tv_dialogTeachType_priceDetail2);
        ll_tel = v.findViewById(R.id.ll_dialogTeachType_tel);//电话布局
        ll_video = v.findViewById(R.id.ll_dialogTeachType_video);//视频
        ll_goHome = v.findViewById(R.id.ll_dialogTeachType_goHome);//上门
        iv_tel = (ImageView) v.findViewById(R.id.iv_dialogTeachType_tel);
        iv_video = (ImageView) v.findViewById(R.id.iv_dialogTeachType_video);
        iv_goHome = (ImageView) v.findViewById(R.id.iv_dialogTeachType_goHome);

        tv_telephone = (TextView) v.findViewById(R.id.tv_telephone);
        tv_video = (TextView) v.findViewById(R.id.tv_video);
        tv_door = (TextView) v.findViewById(R.id.tv_door);
        tv_startprice = (TextView) v.findViewById(R.id.tv_startprice);

        for (int i = 0; i < coursePriceList.size(); i++) {
            switch (coursePriceList.get(i).type) {//授课方式：1:电话 2:视频 3:上门
                case 1://电话
                    iv_tel.setImageResource(R.drawable.dianhua_s);
                    tv_telephone.setText(coursePriceList.get(i).getPrice());
                    break;
                case 2://视频
                    iv_video.setImageResource(R.drawable.shipin_s);
                    tv_video.setText(coursePriceList.get(i).getPrice());
                    break;
                case 3://上门
                    iv_goHome.setImageResource(R.drawable.shangmen_s);
                    tv_door.setText(coursePriceList.get(i).getPrice());
                    tv_startprice.setVisibility(View.VISIBLE);
                    tv_startprice.setText("上门授课半小时起 (￥" + (new BigDecimal(coursePriceList.get(i).getPrice())
                            .multiply(new BigDecimal(30))).floatValue() + "起），不满半小时按半小时收费");
                    break;
            }
        }

//        tv_ok.setOnClickListener(listener);
        ll_tel.setOnClickListener(listener);
        ll_video.setOnClickListener(listener);
        ll_goHome.setOnClickListener(listener);

        tv_price.setText(0 + "");
        tv_priceD.setVisibility(View.GONE);
        tv_priceD2.setVisibility(View.GONE);

        super.setContentView(v);
    }

    public void setSel(int i) {
        for (int j = 0; j < coursePriceList.size(); j++) {
            if (coursePriceList.get(j).type == i) {
                switch (i) {
                    case 1:
                        tv_telephone.setText(coursePriceList.get(j).getPrice() + "");
                        tv_priceD.setVisibility(View.GONE);
                        tv_priceD2.setVisibility(View.GONE);

                        iv_tel.setImageResource(R.drawable.dianhua_b);
                        iv_video.setImageResource(R.drawable.shipin_s);
                        iv_goHome.setImageResource(R.drawable.shangmen_s);
                        break;
                    case 2:
                        tv_video.setText(coursePriceList.get(j).getPrice() + "");
                        tv_priceD.setVisibility(View.GONE);
                        tv_priceD2.setVisibility(View.GONE);
                        iv_tel.setImageResource(R.drawable.dianhua_s);
                        iv_video.setImageResource(R.drawable.shipin_b);
                        iv_goHome.setImageResource(R.drawable.shangmen_s);
                        break;
                    case 3:
                        tv_door.setText(coursePriceList.get(j).getPrice() + "");
                        tv_priceD.setVisibility(View.VISIBLE);
                        tv_priceD.setText("  (￥" + (new BigDecimal(coursePriceList.get(j).getPrice()).multiply(new BigDecimal(30))).floatValue() + "起)");
                        //  tv_priceD2.setVisibility(View.VISIBLE);
                        tv_startprice.setText("上门授课半小时起 (￥" + (new BigDecimal(coursePriceList.get(j).getPrice()).multiply(new BigDecimal(30))).floatValue() + "起），不满半小时按半小时收费");
                        iv_tel.setImageResource(R.drawable.dianhua_s);
                        iv_video.setImageResource(R.drawable.shipin_s);
                        iv_goHome.setImageResource(R.drawable.shangmen_b);
                        break;

                    default:
                        iv_tel.setImageResource(R.drawable.dianhua_s);
                        iv_video.setImageResource(R.drawable.shipin_s);
                        iv_goHome.setImageResource(R.drawable.shangmen_s);
                        break;
                }
            }
        }

    }

    public String getTeachType(int i) {
        if (i == 1) {
            return "语音";
        } else if (i == 2) {
            return "视频";
        } else if (i == 3) {
            return "上门";
        }
        return "";
    }

}
