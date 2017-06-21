package com.ourslook.zuoyeba.activity.fragement.student;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.ourslook.zuoyeba.activity.StudentOrderDetailNoFinishActivity;
import com.ourslook.zuoyeba.activity.em.VideoCallNewActivity;
import com.ourslook.zuoyeba.activity.em.VoiceCallNewActivity;
import com.ourslook.zuoyeba.activity.fragement.BaseFragment;
import com.ourslook.zuoyeba.adapter.StudentUNFinishOrderListAdapter;
import com.ourslook.zuoyeba.event.NotificationRefreshStudentNoFinishListOrDetail;
import com.ourslook.zuoyeba.event.StudentNoFinishListCheckPermissEvent;
import com.ourslook.zuoyeba.model.OrderModel;
import com.ourslook.zuoyeba.model.OrderPageModel;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.ToastUtil;
import com.ourslook.zuoyeba.view.LoadMore;
import com.ourslook.zuoyeba.view.dialog.LoadingDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;


/**
 * Created by huangyi on 16/5/16.  学生未完成订单
 */
public class StudentUNFinishOrderListFragment extends BaseFragment implements AdapterView.OnItemClickListener, PtrHandler, LoadMore.OnLoadMoreListener {
    @Bind(R.id.lv_student_order_left_fragment)
    ListView lvStudentOrderLeftFragment;
    @Bind(R.id.view_myListS_noList)
    RelativeLayout viewMyListSNoList;

    StudentUNFinishOrderListAdapter mStudentUNFinishOrderListAdapter;

    List<OrderModel> list = new ArrayList<>();
    @Bind(R.id.ptr_student_unfinish_order)
    PtrClassicFrameLayout ptrStudentUnfinishOrder;


    private int pageIndex = 1;// 当前页数
    //    private int perPage = 5;// 每页显示笔数
    LoadMore loadMore;
    boolean isRefreshing;


    int position = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_student_unfinish_order);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initView() {
        mStudentUNFinishOrderListAdapter = new StudentUNFinishOrderListAdapter(mContext, list, R.layout.item_student_order_unfinish_list, Constants.STUDENT);
        lvStudentOrderLeftFragment.setAdapter(mStudentUNFinishOrderListAdapter);
        lvStudentOrderLeftFragment.setDividerHeight(0);
        lvStudentOrderLeftFragment.setOnItemClickListener(this);


        //下拉刷新初始化
        ptrStudentUnfinishOrder.setPtrHandler(this);
        ptrStudentUnfinishOrder.setLastUpdateTimeRelateObject(this);

        //上拉加载初始化
        loadMore = new LoadMore(lvStudentOrderLeftFragment);
        loadMore.setOnLoadMoreListener(this);
        //noinspection deprecation
        lvStudentOrderLeftFragment.setOnScrollListener(loadMore);
    }


    @Override
    public void getHttpData() {


    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //TODO 跳转到
        OrderModel orderModel = mStudentUNFinishOrderListAdapter.mData.get((int) id);
        Intent intent = new Intent();
        intent.putExtra(Constants.PASS_ORDER, orderModel.id);
        intent.setClass(mContext, StudentOrderDetailNoFinishActivity.class);
        startActivity(intent);
    }

    @Subscribe
    public void clickPosition(StudentNoFinishListCheckPermissEvent event) {
        position = event.position;
    }

    @Subscribe
    public void onEvent(NotificationRefreshStudentNoFinishListOrDetail event) {
        if (event.type.equals(Constants.TYPE)) {
            Log.d("--","学生列表");
            pageIndex=1;
            getData();
        }
    }

    //此处对权限处理需要判断
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (position == -1) {
                ToastUtil.showToastDefault(mContext, "下标异常");
            }
            if (mStudentUNFinishOrderListAdapter.mData.get(position).type == 1) {
                Intent i = new Intent(mContext, VoiceCallNewActivity.class);
                //给谁打
                i.putExtra("to", mStudentUNFinishOrderListAdapter.mData.get(position).teacherimacc);
                //订单id   用于 开始作业 结束id
                i.putExtra("workid", mStudentUNFinishOrderListAdapter.mData.get(position).id + "");
                startActivity(i);
            } else if (mStudentUNFinishOrderListAdapter.mData.get(position).type == 2) {
                Intent i = new Intent(mContext, VideoCallNewActivity.class);
                //给谁打
                i.putExtra("to", mStudentUNFinishOrderListAdapter.mData.get(position).teacherimacc);
                //订单id   用于 开始作业 结束id
                i.putExtra("workid", mStudentUNFinishOrderListAdapter.mData.get(position).id + "");
                startActivity(i);
            }
        } else {
            ToastUtil.showToastDefault(mContext, "录音通话权限未授权---->应用----->权限管理");
        }

    }

    @Override
    public void onStart() {
        onRefreshBegin(ptrStudentUnfinishOrder);
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(frame, lvStudentOrderLeftFragment, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
//        logForDebug("refresh");
        pageIndex = 1;
        getData();
    }

    private void getData() {
        Map<String, String> params = new HashMap<>();
        params.put("token", AppConfig.token);
        params.put("type", "" + 1);
        params.put("page", "" + pageIndex);
        params.put("rows", "" + Constants.ROWS);
        if (pageIndex == 1) {
            isRefreshing = true;
        }
        EasyHttp.doPost(mContext, ServerURL.GETWORKLIST, params, null, OrderPageModel.class, false, new EasyHttp.OnEasyHttpDoneListener<OrderPageModel>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<OrderPageModel> resultBean) {
                if (resultBean.data.page.total > 0) {
                    viewMyListSNoList.setVisibility(View.INVISIBLE);
                    lvStudentOrderLeftFragment.setVisibility(View.VISIBLE);
                } else {
                    viewMyListSNoList.setVisibility(View.VISIBLE);
                    lvStudentOrderLeftFragment.setVisibility(View.INVISIBLE);
                }
                if (resultBean.data.orderList != null && resultBean.data.orderList.size() > 0) {
                    if (isRefreshing) { //下拉刷新
                        mStudentUNFinishOrderListAdapter.mData = resultBean.data.orderList;
                    } else {
                        mStudentUNFinishOrderListAdapter.mData.addAll(resultBean.data.orderList);
                    }
                    mStudentUNFinishOrderListAdapter.notifyDataSetChanged();
                }
                isRefreshing = false;

                //下拉刷新结束
                ptrStudentUnfinishOrder.refreshComplete();
                //上拉加载设置
                loadMore.setIsLastPage(resultBean.data.page.total == mStudentUNFinishOrderListAdapter.getCount());

            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                ToastUtil.showToastOnce(mContext, message);
                isRefreshing = false;
                //下拉刷新结束
                ptrStudentUnfinishOrder.refreshComplete();
                //上拉加载设置
                loadMore.setIsLastPage(false);
            }
        });
    }

    @Override
    public void onLoadMore() {
        pageIndex++;
        getData();
    }
}
