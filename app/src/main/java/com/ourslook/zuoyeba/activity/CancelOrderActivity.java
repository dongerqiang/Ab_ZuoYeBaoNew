package com.ourslook.zuoyeba.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ourslook.zuoyeba.AppConfig;
import com.ourslook.zuoyeba.AppManager;
import com.ourslook.zuoyeba.BaseActivity;
import com.ourslook.zuoyeba.Constants;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ServerURL;
import com.ourslook.zuoyeba.adapter.CancelOrderAdapter;
import com.ourslook.zuoyeba.event.CancelOrderListEvent;
import com.ourslook.zuoyeba.model.CourseCancelReasonModel;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.ToastUtil;
import com.ourslook.zuoyeba.view.dialog.LoadingDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by huangyi on 16/5/19.
 * 取消订单界面
 */
public class CancelOrderActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    @Bind(R.id.list_cancelListAty_reasonList)
    ListView listCancelListAtyReasonList;

    CancelOrderAdapter mAdapter;

  public static   boolean[] chooseIndexArray;

    int chooseIndex=-1;


    long orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderId=getIntent().getLongExtra(Constants.PASS_ORDER,-1);
        setContentViewWithDefaultTitle(R.layout.activity_cancel_order, "取消订单");
    }

    @Override
    protected void initView() {
        mTvTitleRight.setText("提交");
        mTvTitleRight.setVisibility(View.VISIBLE);
        setOnClickListeners(this,mTvTitleRight);
        mAdapter=new CancelOrderAdapter(mContext,null,R.layout.item_cancel_order_list);
        listCancelListAtyReasonList.setAdapter(mAdapter);
        listCancelListAtyReasonList.setOnItemClickListener(this);
        getCancelList();
    }


    /**
     * 取消原因列表
     */
    private void getCancelList(){
        LoadingDialog.showLoadingDialog(mContext);
        EasyHttp.doPost(mContext, ServerURL.GETCOURSECANCELREASONLIST, null, null, CourseCancelReasonModel.class, true, new EasyHttp.OnEasyHttpDoneListener<ArrayList<CourseCancelReasonModel>>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<ArrayList<CourseCancelReasonModel>> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                if(resultBean.data!=null){
                    if(resultBean.data.size()>0){
                        mAdapter.mData.addAll(resultBean.data);
                        chooseIndexArray=new boolean[resultBean.data.size()];
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastDefault(mContext,message);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_title_right:
                cancelOrder();
                break;
        }
    }

    /**
     * 取消订单
     */
    private void cancelOrder(){
        if(chooseIndex==-1){
            ToastUtil.showToastDefault(mContext,"请选择取消原因!");
            return;
        }
        LoadingDialog.showLoadingDialog(mContext);
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", AppConfig.token);
        params.put("workid", orderId+"");
        params.put("reasonid", mAdapter.mData.get(chooseIndex).reasonid+"");
        EasyHttp.doPost(mContext, ServerURL.CANCELWORKBYID, params, null, null, false, new EasyHttp.OnEasyHttpDoneListener<Object>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<Object> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastDefault(mContext,"操作成功!");
                AppManager.getInstance().finishActivity(StudentOrderDetailNoFinishActivity.class);
                finish();
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
               LoadingDialog.dismissLoadingDialog();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        chooseIndex= (int) id;
        for(int i=0;i<chooseIndexArray.length;i++){
            chooseIndexArray[i]=false;
        }
        chooseIndexArray[chooseIndex]=true;
        mAdapter.notifyDataSetChanged();
    }
}
