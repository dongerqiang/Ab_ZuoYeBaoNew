package com.ourslook.zuoyeba.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ourslook.zuoyeba.AppConfig;
import com.ourslook.zuoyeba.AppManager;
import com.ourslook.zuoyeba.Constants;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ServerURL;
import com.ourslook.zuoyeba.activity.DetailTeacherActivity;
import com.ourslook.zuoyeba.activity.PickUpTeacherActivity;
import com.ourslook.zuoyeba.activity.RedBagActivity;
import com.ourslook.zuoyeba.activity.StudentOrderDetailNoFinishActivity;
import com.ourslook.zuoyeba.model.OrderTeacherModel;
import com.ourslook.zuoyeba.model.PayRedPacketDetailModel;
import com.ourslook.zuoyeba.model.PayRedPacketModel;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.StringUtils;
import com.ourslook.zuoyeba.utils.ToastUtil;
import com.ourslook.zuoyeba.view.ImageLoaderUtil.ILutil;
import com.ourslook.zuoyeba.view.dialog.LoadingDialog;
import com.ourslook.zuoyeba.view.dialog.NoRedDialog;
import com.ourslook.zuoyeba.view.dialog.PickRedDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SEED on 2015/12/21.
 */
public class PickUpTeacherListAdapter extends BaseAdapter {

    private Context context;
    public ArrayList<OrderTeacherModel> list;

    private PickRedDialog redDialog;// 红包弹框
    private NoRedDialog noRedDialog;// 没有红包弹框
    private String OrderNo;

    public PickUpTeacherListAdapter(ArrayList<OrderTeacherModel> list, Context context, String OrderNo) {
        super();
        this.list = list;
        this.context = context;
        this.OrderNo = OrderNo;
    }

    public void setList(ArrayList<OrderTeacherModel> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_pickup_teacher, null);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        OrderTeacherModel orderTeacherModel = list.get(position);
        String photourl = orderTeacherModel.getPhotourl();
        if (StringUtils.isNotEmpty(photourl)) {
            ImageLoader.getInstance().displayImage(photourl, holder.iv_head, ILutil.getRoundImageOptions());
        } else {
            holder.iv_head.setImageResource(R.drawable.default_head);
        }
        int sex = orderTeacherModel.getSex();
        if (sex == 1) {
            holder.iv_headBg.setImageResource(R.drawable.pipeilaoshi_nan);
        } else if (sex == 2) {
            holder.iv_headBg.setImageResource(R.drawable.pipeilaoshi_nv);
        }
        holder.tv_name.setText(orderTeacherModel.getName());
        holder.tv_lvl.setText(orderTeacherModel.getLevel());
        final long id = orderTeacherModel.getId();

        holder.tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //调红包接口，看有没有红包
                getRedPacketList(id);
            }
        });

        return convertView;
    }

    static class Holder {
        ImageView iv_head;
        ImageView iv_headBg;
        TextView tv_name;
        TextView tv_lvl;
        TextView tv_ok;

        public Holder(View v) {
            iv_head = (ImageView) v.findViewById(R.id.iv_li_pickUpTAty_head);
            iv_headBg = (ImageView) v.findViewById(R.id.iv_li_pickUpTAty_headBg);
            tv_name = (TextView) v.findViewById(R.id.tv_li_pickUpTAty_name);
            tv_lvl = (TextView) v.findViewById(R.id.tv_li_pickUpTAty_teachlvl);
            tv_ok = (TextView) v.findViewById(R.id.tv_li_pickUpTAty_ok);
        }
    }

    /**
     * 获取红包设定
     */
    private void getRedPacketSetting(final long id) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", AppConfig.token);

        EasyHttp.doPost(context, ServerURL.GETREDPACKETSETTING, params, null, Double.class, false, new EasyHttp.OnEasyHttpDoneListener<Double>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<Double> resultBean) {


                showRedDialog(id,resultBean.data+"");
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                ToastUtil.showToastDefault(context, message);
            }
        });
    }


    /**
     * 确认老师
     */
    private void confirmTeacher(long id) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", AppConfig.token);
        params.put("teacherid", id + "");
        EasyHttp.doPost(context, ServerURL.CONFIRMTEACHER, params, null, Object.class, false, new EasyHttp.OnEasyHttpDoneListener<Object>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<Object> resultBean) {

                ToastUtil.showToastDefault(context, "确认老师成功");
                //跳到订单详情带教师id和标示id过去
                Intent intent = new Intent(context, StudentOrderDetailNoFinishActivity.class);
//                intent.putExtra(Constants.PASS_ORDER,Long.parseLong(OrderNo));
                intent.putExtra(Constants.PASS_ORDER,AppConfig.placeOrder);
                context.startActivity(intent);
                AppManager.getInstance().finishActivity(DetailTeacherActivity.class);
                AppManager.getInstance().finishActivity(PickUpTeacherActivity.class);
                PickUpTeacherActivity.flag=true;
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                ToastUtil.showToastDefault(context, message);

            }
        });
    }

    /**
     * 选择红包弹框
     */
    private void showRedDialog(final long id, String s) {
        if (redDialog == null)
            redDialog = new PickRedDialog(context, R.style.FullHeightDialog, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.view_dialogPickRed_ok:
                            //有红包跳到红包列表带教师id和标示id过去
                            Intent i = new Intent(context, RedBagActivity.class);
                            i.putExtra(Constants.PASS_STATUS, 1);
                            i.putExtra(Constants.TEACHER_ID,id);;
                            i.putExtra(Constants.PASS_ORDER,AppConfig.placeOrder);
                            context.startActivity(i);
                            redDialog.dismiss();
                            break;
                        case R.id.tv_dialogPickRed_no:
                            confirmTeacher(id);
                            redDialog.dismiss();
                            break;
                        default:
                            break;
                    }
                }
            });
        redDialog.setMoney(s);
        redDialog.show();

    }

    /**
     * 我的红包
     */
    private void getRedPacketList(final long id) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", AppConfig.token);
        params.put("status", "1");
        LoadingDialog.showLoadingDialog(context);
        EasyHttp.doPost(context, ServerURL.GETREDPACKETLIST, params, null, PayRedPacketModel.class, false, new EasyHttp.OnEasyHttpDoneListener<PayRedPacketModel>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<PayRedPacketModel> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                List<PayRedPacketDetailModel> orderList = resultBean.data.redpacketList;
                if (orderList.size() > 0) {


                     getRedPacketSetting(id);
                } else {
                    showNoRedDialog(id);
                }
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastDefault(context, message);
                redDialog.dismiss();
            }
        });
    }

    /**
     * 没有红包弹框
     */
    private void showNoRedDialog(final long id) {
        if (noRedDialog == null)
            noRedDialog = new NoRedDialog(context, R.style.FullHeightDialog, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noRedDialog.dismiss();
                    confirmTeacher(id);
                }
            });
        noRedDialog.show();
    }
}
