package com.ourslook.zuoyeba.activity.fragement.student;

import android.content.Intent;
import android.os.Bundle;
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
import com.ourslook.zuoyeba.activity.StudentOrderDetailFinishActivity;
import com.ourslook.zuoyeba.activity.fragement.BaseFragment;
import com.ourslook.zuoyeba.adapter.StudentUNFinishOrderListAdapter;
import com.ourslook.zuoyeba.model.OrderPageModel;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.ToastUtil;
import com.ourslook.zuoyeba.view.LoadMore;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;


/**
 * Created by huangyi on 16/5/16.  学生订单列表
 */
public class StudentFinishOrderListFragment extends BaseFragment implements AdapterView.OnItemClickListener, LoadMore.OnLoadMoreListener, PtrHandler {
    @Bind(R.id.lv_student_order_left_fragment)
    ListView lvStudentOrderLeftFragment;
    @Bind(R.id.view_myListS_noList)
    RelativeLayout viewMyListSNoList;
    StudentUNFinishOrderListAdapter mStudentUNFinishOrderListAdapter;
    @Bind(R.id.ptr_student_finish_order)
    PtrClassicFrameLayout ptrStudentFinishOrder;

    private int pageIndex = 0;

    LoadMore loadMore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_student_finish_order);
    }

    @Override
    protected void initView() {
        mStudentUNFinishOrderListAdapter = new StudentUNFinishOrderListAdapter(mContext, null, R.layout.item_student_order_unfinish_list, Constants.STUDENT);

        lvStudentOrderLeftFragment.setAdapter(mStudentUNFinishOrderListAdapter);
        lvStudentOrderLeftFragment.setDividerHeight(0);
        lvStudentOrderLeftFragment.setOnItemClickListener(this);

        //下拉刷新初始化
        ptrStudentFinishOrder.setPtrHandler(this);
        ptrStudentFinishOrder.setLastUpdateTimeRelateObject(this);

        //上拉加载初始化
        loadMore = new LoadMore(lvStudentOrderLeftFragment);
        loadMore.setOnLoadMoreListener(this);
        //noinspection deprecation
        lvStudentOrderLeftFragment.setOnScrollListener(loadMore);

    }

    @Override
    public void onStart() {
        onRefreshBegin(ptrStudentFinishOrder);
        super.onStart();
    }


    public void getData(){
        Map<String, String> params = new HashMap<>();
        params.put("token", AppConfig.token);
        params.put("type", "" + 2);
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
                    ptrStudentFinishOrder.setVisibility(View.VISIBLE);
                } else {
                    viewMyListSNoList.setVisibility(View.VISIBLE);
                    ptrStudentFinishOrder.setVisibility(View.INVISIBLE);
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
                ptrStudentFinishOrder.refreshComplete();
                //上拉加载设置
                loadMore.setIsLastPage(resultBean.data.page.total == mStudentUNFinishOrderListAdapter.getCount());

            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                isRefreshing = false;
                //下拉刷新结束
                ptrStudentFinishOrder.refreshComplete();
                //上拉加载设置
                loadMore.setIsLastPage(false);
                ToastUtil.showToastOnce(mContext, message);
            }
        });
    }

    boolean isRefreshing;

    @Override
    public void getHttpData() {


    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //TODO 跳转到
        Intent intent = new Intent(mContext, StudentOrderDetailFinishActivity.class);
        intent.putExtra(Constants.PASS_ORDER, mStudentUNFinishOrderListAdapter.mData.get((int) id).id);
        startActivity(intent);
    }


    @Override
    public void onLoadMore() {
        pageIndex++;
        getData();
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {

        return PtrDefaultHandler.checkContentCanBePulledDown(frame, lvStudentOrderLeftFragment, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        pageIndex = 1;
        getData();
    }
}
