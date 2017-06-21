package com.ourslook.zuoyeba.activity.fragement.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.ourslook.zuoyeba.AppConfig;
import com.ourslook.zuoyeba.Constants;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ServerURL;
import com.ourslook.zuoyeba.activity.TeacherOrderDetailActivity;
import com.ourslook.zuoyeba.activity.fragement.BaseFragment;
import com.ourslook.zuoyeba.adapter.GetOrderAdapter;
import com.ourslook.zuoyeba.event.GetOrderEvent;
import com.ourslook.zuoyeba.model.OrderPageModel;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.ToastUtil;
import com.ourslook.zuoyeba.view.dialog.LoadingDialog;
import com.ourslook.zuoyeba.view.dialog.TeacherMainAffirmOrderDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;


/**
 * 老师端首页
 * Created by wangyu on 16/5/16.
 */
public class TeacherMainFragment extends BaseFragment implements AdapterView.OnItemClickListener, PtrHandler {


    @Bind(R.id.lv_homeFgmT)
    ListView lvHomeFgmT;
    @Bind(R.id.view_homeFgmT_noList)
    RelativeLayout viewHomeFgmTNoList;

    private final int DELAY_TIME_TO_REFRESH = 5000;

    GetOrderAdapter mGetOrderAdapter;// 抢单适配器

    boolean isLook = false;

    boolean isRefreshing;
    boolean isHasAffirm;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isLook) {
                getHttpData();
            }
            handler.sendEmptyMessageDelayed(0, DELAY_TIME_TO_REFRESH);
        }
    };
    @Bind(R.id.ptr_student_unfinish_order)
    PtrClassicFrameLayout ptrStudentUnfinishOrder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_teacher_main);
        EventBus.getDefault().register(this);
    }


    @Override
    protected void initView() {
        mGetOrderAdapter = new GetOrderAdapter(mContext, null, R.layout.item_get_order);
        lvHomeFgmT.setAdapter(mGetOrderAdapter);
        lvHomeFgmT.setOnItemClickListener(this);
        handler.sendEmptyMessageDelayed(0, DELAY_TIME_TO_REFRESH);
        //下拉刷新初始化
        ptrStudentUnfinishOrder.setPtrHandler(this);
        ptrStudentUnfinishOrder.setLastUpdateTimeRelateObject(this);
    }

    @Subscribe
    public void onEvent(GetOrderEvent event) {
        Map<String, String> params = new HashMap<>();
        LoadingDialog.showLoadingDialog(mContext);
        params.put("token", AppConfig.token);
        params.put("workid", mGetOrderAdapter.mData.get(event.position).id + "");
        EasyHttp.doPost(mContext, ServerURL.GRABORDERBYID, params, null, null, false, new EasyHttp.OnEasyHttpDoneListener<Object>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<Object> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastDefault(mContext, "抢单成功");
                getHttpData();//刷新数据
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastDefault(mContext, message);
            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        isLook = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isLook = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        isLook = false;
    }


    /**
     * 展示抢单列表数据
     */
    @Override
    public void getHttpData() {
        if (AppConfig.isLogin && !isRefreshing) {
            if (AppConfig.userVo.teacher != null) {
                Map<String, String> params = new HashMap<>();
                params.put("token", AppConfig.token);
                isRefreshing = true;
                EasyHttp.doPost(mContext, ServerURL.GRABORDERLIST, params, null, OrderPageModel.class, false, new EasyHttp.OnEasyHttpDoneListener<OrderPageModel>() {
                    @Override
                    public void onEasyHttpSuccess(ResultBean<OrderPageModel> resultBean) {
                        if (resultBean.data.page.total == 0) {
                            ptrStudentUnfinishOrder.setVisibility(View.GONE);
                            viewHomeFgmTNoList.setVisibility(View.VISIBLE);
                        } else {
                            ptrStudentUnfinishOrder.setVisibility(View.VISIBLE);
                            viewHomeFgmTNoList.setVisibility(View.GONE);
                        }
                        mGetOrderAdapter.mData.clear();
                        mGetOrderAdapter.mData.addAll(resultBean.data.orderList);
                        mGetOrderAdapter.notifyDataSetChanged();
                        isRefreshing = false;
                        //下拉刷新结束
                        ptrStudentUnfinishOrder.refreshComplete();

                    }

                    @Override
                    public void onEasyHttpFailure(String code, String message) {
                        ToastUtil.showToastDefault(mContext, message);
                        isRefreshing = false;
                        //下拉刷新结束
                        mGetOrderAdapter.mData.clear();
                        mGetOrderAdapter.notifyDataSetChanged();
                        ptrStudentUnfinishOrder.refreshComplete();
                    }
                });
            }
        }
        /**
         * 查询是否有已确认订单
         */
        if (AppConfig.isLogin && !isHasAffirm) {
            if (AppConfig.userVo.teacher != null) {
                HashMap<String, String> orderParams = new HashMap<>();
                orderParams.put("token", AppConfig.userVo.token);
                isHasAffirm = true;
                EasyHttp.doPost(mContext, ServerURL.GET_TEACHER_NOTIFY_BY_TEACHER_ID, orderParams, null, null, false, new EasyHttp.OnEasyHttpDoneListener<Object>() {
                    @Override
                    public void onEasyHttpSuccess(ResultBean<Object> resultBean) {
                        if (resultBean.data != null) {
                            isHasAffirm = false;
                            Integer i = (Integer) resultBean.data;
                            Log.d("--", "i=" + i);
                            new TeacherMainAffirmOrderDialog(mContext, i.longValue()).show();
                        }
                    }

                    @Override
                    public void onEasyHttpFailure(String code, String message) {
                        //ToastUtil.showToastDefault(mContext, message);
                        isHasAffirm = false;
                    }
                });
            }
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //点击跳转到订单详情
        Intent intent = new Intent(mContext, TeacherOrderDetailActivity.class);
        intent.putExtra(Constants.PASS_ORDER, mGetOrderAdapter.mData.get((int) id).id);
        intent.putExtra("fromWhere", "TeacherMainFragment");
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(frame, lvHomeFgmT, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        getHttpData();
    }
}
