package com.ourslook.zuoyeba.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.ourslook.zuoyeba.AppConfig;
import com.ourslook.zuoyeba.BaseActivity;
import com.ourslook.zuoyeba.Constants;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ServerURL;
import com.ourslook.zuoyeba.adapter.MyPointsAdapter;
import com.ourslook.zuoyeba.model.AccStudentIntegrationModel;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.ToastUtil;
import com.ourslook.zuoyeba.view.LoadMore;
import com.ourslook.zuoyeba.view.dialog.LoadingDialog;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by huangyi on 16/5/16.
 * 我的积分界面
 */
public class MyPointActivity extends BaseActivity implements PtrHandler, LoadMore.OnLoadMoreListener {

    @Bind(R.id.tv_myCountAty_count)
    TextView tvMyCountAtyCount;
    @Bind(R.id.lv_myCountAty)
    ListView lvMyCountAty;

    MyPointsAdapter mMyPointsAdapter;


    int pageIndex = 1;
    @Bind(R.id.ptr_points_order)
    PtrClassicFrameLayout ptrPointsOrder;

    LoadMore loadMore;
    boolean isRefreshing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithDefaultTitle(R.layout.activity_mypoints, "我的积分");
    }

    @Override
    protected void initView() {
        mMyPointsAdapter = new MyPointsAdapter(mContext, null, R.layout.item_points);
        lvMyCountAty.setAdapter(mMyPointsAdapter);
        //下拉刷新初始化
        ptrPointsOrder.setPtrHandler(this);
        ptrPointsOrder.setLastUpdateTimeRelateObject(this);

        //上拉加载初始化
        loadMore = new LoadMore(lvMyCountAty);
        loadMore.setOnLoadMoreListener(this);
        //noinspection deprecation
        lvMyCountAty.setOnScrollListener(loadMore);
        getHttpData();
    }


    /**
     * 获取积分数据
     */
    private void getHttpData() {
        Map<String, String> params = new HashMap<String, String>();
        LoadingDialog.showLoadingDialog(mContext);
        params.put("token", AppConfig.token);
        params.put("page", "" + pageIndex);
        params.put("rows", "" + Constants.ROWS);
//        params.put("status", "1");//1:未使用红包，不填时为所有红包
        if (pageIndex == 1) {
            isRefreshing = true;
        }
        EasyHttp.doPost(mContext, ServerURL.GETINTEGRATIONLIST, params, null, AccStudentIntegrationModel.class, false, new EasyHttp.OnEasyHttpDoneListener<AccStudentIntegrationModel>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<AccStudentIntegrationModel> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                if (resultBean.data.integrationList != null && resultBean.data.integrationList.size() > 0) {
                    tvMyCountAtyCount.setText(""+resultBean.data.totalintegration);
                    if (isRefreshing) { //下拉刷新
                        mMyPointsAdapter.mData = resultBean.data.integrationList;
                    } else {
                        mMyPointsAdapter.mData.addAll(resultBean.data.integrationList);
                    }
                    mMyPointsAdapter.notifyDataSetChanged();
                }
                isRefreshing = false;

                //下拉刷新结束
                ptrPointsOrder.refreshComplete();
                //上拉加载设置
                loadMore.setIsLastPage(resultBean.data.page.total == mMyPointsAdapter.getCount());
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                ToastUtil.showToastDefault(mContext, message);
                LoadingDialog.dismissLoadingDialog();
                isRefreshing = false;
                //下拉刷新结束
                ptrPointsOrder.refreshComplete();
                //上拉加载设置
                loadMore.setIsLastPage(false);
            }
        });
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(frame, lvMyCountAty, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
       pageIndex=1;
        getHttpData();
    }

    @Override
    public void onLoadMore() {
        pageIndex++;
        getHttpData();
    }
}
