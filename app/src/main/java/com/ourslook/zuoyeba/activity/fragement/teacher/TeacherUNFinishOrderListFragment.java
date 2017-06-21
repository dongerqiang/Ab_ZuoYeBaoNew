package com.ourslook.zuoyeba.activity.fragement.teacher;

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
import com.ourslook.zuoyeba.activity.TeacherOrderDetailActivity;
import com.ourslook.zuoyeba.activity.fragement.BaseFragment;
import com.ourslook.zuoyeba.adapter.StudentUNFinishOrderListAdapter;
import com.ourslook.zuoyeba.model.OrderPageModel;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.ToastUtil;
import com.ourslook.zuoyeba.view.LoadMore;
import com.ourslook.zuoyeba.view.dialog.LoadingDialog;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;


/**
 * Created by huangyi on 16/5/16. 老师未完成订单
 */
public class TeacherUNFinishOrderListFragment extends BaseFragment implements AdapterView.OnItemClickListener, PtrHandler, LoadMore.OnLoadMoreListener {
    @Bind(R.id.lv_teacher_order_left_fragment)
    ListView lvTeacherOrderLeftFragment;
    @Bind(R.id.view_myListT_right_noList)
    RelativeLayout viewMyListTRightNoList;

    StudentUNFinishOrderListAdapter mTeacherUNFinishOrderListAdapter;

    int indexPager = 1;
    @Bind(R.id.ptr_teacher_unfinish_order)
    PtrClassicFrameLayout ptrTeacherUnfinishOrder;

    LoadMore loadMore;

    boolean isRefreshing;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_teacher_unfinish_order);
    }

    @Override
    protected void initView() {
        mTeacherUNFinishOrderListAdapter = new StudentUNFinishOrderListAdapter(mContext, null, R.layout.item_student_order_unfinish_list, Constants.TEACHER);
        lvTeacherOrderLeftFragment.setAdapter(mTeacherUNFinishOrderListAdapter);
        lvTeacherOrderLeftFragment.setOnItemClickListener(this);
        //下拉刷新初始化
        ptrTeacherUnfinishOrder.setPtrHandler(this);
        ptrTeacherUnfinishOrder.setLastUpdateTimeRelateObject(this);

        //上拉加载初始化
        loadMore = new LoadMore(lvTeacherOrderLeftFragment);
        loadMore.setOnLoadMoreListener(this);
        //noinspection deprecation
        lvTeacherOrderLeftFragment.setOnScrollListener(loadMore);
    }

    @Override
    public void onStart() {
        onRefreshBegin(ptrTeacherUnfinishOrder);
        super.onStart();
    }

    public void getData(){
        Map<String, String> params = new HashMap<>();
        params.put("token", AppConfig.token);
        params.put("page", "" + indexPager);
        params.put("rows", "" + Constants.ROWS);
        params.put("type", "1");
        if (indexPager == 1) {
            isRefreshing = true;
        }
        LoadingDialog.showLoadingDialog(mContext);
        EasyHttp.doPost(mContext, ServerURL.GETORDERLIST, params, null, OrderPageModel.class, false, new EasyHttp.OnEasyHttpDoneListener<OrderPageModel>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<OrderPageModel> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                if (resultBean.data.page.total > 0) {
                    viewMyListTRightNoList.setVisibility(View.INVISIBLE);
                    ptrTeacherUnfinishOrder.setVisibility(View.VISIBLE);
                } else {
                    viewMyListTRightNoList.setVisibility(View.VISIBLE);
                    ptrTeacherUnfinishOrder.setVisibility(View.INVISIBLE);
                }

                if (resultBean.data.orderList != null && resultBean.data.orderList.size() > 0) {
                    if (isRefreshing) { //下拉刷新
                        mTeacherUNFinishOrderListAdapter.mData = resultBean.data.orderList;
                    } else {
                        mTeacherUNFinishOrderListAdapter.mData.addAll(resultBean.data.orderList);
                    }
                    mTeacherUNFinishOrderListAdapter.notifyDataSetChanged();
                }
                isRefreshing = false;

                //下拉刷新结束
                ptrTeacherUnfinishOrder.refreshComplete();
                //上拉加载设置
                loadMore.setIsLastPage(resultBean.data.page.total == mTeacherUNFinishOrderListAdapter.getCount());
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastDefault(mContext, message);
                isRefreshing = false;
                //下拉刷新结束
                ptrTeacherUnfinishOrder.refreshComplete();
                //上拉加载设置
                loadMore.setIsLastPage(false);
            }
        });
    }

    @Override
    public void getHttpData() {

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(mContext, TeacherOrderDetailActivity.class);
        intent.putExtra(Constants.PASS_ORDER, mTeacherUNFinishOrderListAdapter.mData.get((int) id).id);
        startActivity(intent);
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(frame, lvTeacherOrderLeftFragment, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        indexPager = 1;
        getData();
    }

    @Override
    public void onLoadMore() {
        indexPager++;
        getData();
    }
}
