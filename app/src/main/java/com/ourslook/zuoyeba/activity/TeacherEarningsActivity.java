package com.ourslook.zuoyeba.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ourslook.zuoyeba.AppConfig;
import com.ourslook.zuoyeba.BaseActivity;
import com.ourslook.zuoyeba.Constants;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ServerURL;
import com.ourslook.zuoyeba.adapter.EarnListAdapter;
import com.ourslook.zuoyeba.model.PayTeacherIncomeModel;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.ToastUtil;
import com.ourslook.zuoyeba.view.LoadMore;
import com.ourslook.zuoyeba.view.dialog.LoadingDialog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by huangyi on 16/5/19.
 * 我的收益界面
 */
public class TeacherEarningsActivity extends BaseActivity implements View.OnClickListener, PtrHandler, LoadMore.OnLoadMoreListener {
    @Bind(R.id.tv_myMoneyAty_leftMoney)
    TextView tvMyMoneyAtyLeftMoney; //我的于额
    @Bind(R.id.iv_myMoneyAty_left)
    ImageView ivMyMoneyAtyLeft;//左边箭头
    @Bind(R.id.tv_myMoneyAty_month)
    TextView tvMyMoneyAtyMonth;//月份
    @Bind(R.id.iv_myMoneyAty_right)
    ImageView ivMyMoneyAtyRight;//右边箭头
    @Bind(R.id.pList_myMoneyAty_monthMoney)
    ListView pListMyMoneyAtyMonthMoney;//展示列表数据
    @Bind(R.id.tv_myMoneyAty_allMoney)
    TextView tvMyMoneyAtyAllMoney;//本月实际总收益
    @Bind(R.id.view_myMoneyAty_haveMoney)
    LinearLayout viewMyMoneyAtyHaveMoney;//当月存在收益记录
    @Bind(R.id.view_myMoneyAty_noneMoney)
    RelativeLayout viewMyMoneyAtyNoneMoney;//当月不存在收益记录


    int pageIndex = 1;
    @Bind(R.id.ptr_earn_order)
    PtrClassicFrameLayout ptrEarnOrder;
    LoadMore loadMore;
    boolean isRefreshing;


    private int chooseYear;
    private int chooseMonth;

    int index = 0;


    PayTeacherIncomeModel mPayTeacherIncomeModel;//数据

    EarnListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithDefaultTitle(R.layout.activity_teacher_earning, "我的收益");

    }

    @Override
    protected void initView() {
        setOnClickListeners(this, ivMyMoneyAtyLeft, ivMyMoneyAtyRight);
        ivMyMoneyAtyRight.setVisibility(View.INVISIBLE);
        mAdapter = new EarnListAdapter(mContext, null, R.layout.item_earn_list);
        pListMyMoneyAtyMonthMoney.setAdapter(mAdapter);
        //下拉刷新初始化
        ptrEarnOrder.setPtrHandler(this);
        ptrEarnOrder.setLastUpdateTimeRelateObject(this);

        //上拉加载初始化
        loadMore = new LoadMore(pListMyMoneyAtyMonthMoney);
        loadMore.setOnLoadMoreListener(this);
        //noinspection deprecation
        pListMyMoneyAtyMonthMoney.setOnScrollListener(loadMore);
        getInfo();
        getHttp(String.format("%1$d%2$02d", chooseYear, chooseMonth));
    }


    /**
     *
     */
    private void getInfo() {
        Calendar calendar = Calendar.getInstance();
        chooseMonth = calendar.get(Calendar.MONTH) + 1;
        chooseYear = calendar.get(Calendar.YEAR);
        if (chooseMonth + index <= 0) {//跨年
            chooseYear -= (chooseMonth + index - 12) / -12;
            chooseMonth = 12 + (chooseMonth + index) % 12;
        } else {
            chooseMonth = chooseMonth + index;
        }
        ivMyMoneyAtyRight.setVisibility(index < 0 ? View.VISIBLE : View.INVISIBLE);
        tvMyMoneyAtyMonth.setText(String.format("%1$d年%2$02d月", chooseYear, chooseMonth));
    }

    /**
     * 获取收益数据
     * 格式201601
     */
    private void getHttp(String month) {
        Map<String, String> params = new HashMap<>();
        LoadingDialog.showLoadingDialog(mContext);
        params.put("token", AppConfig.token);
        params.put("page", "" + pageIndex);
        params.put("rows", "" + Constants.ROWS);
        params.put("yearmonth", month);
        if (pageIndex == 1) {
            isRefreshing = true;
        }
        EasyHttp.doPost(mContext, ServerURL.GETINCOMELIST, params, null, PayTeacherIncomeModel.class, false, new EasyHttp.OnEasyHttpDoneListener<PayTeacherIncomeModel>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<PayTeacherIncomeModel> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                if (resultBean.data.page.total == 0) {
                    viewMyMoneyAtyNoneMoney.setVisibility(View.VISIBLE);
                    viewMyMoneyAtyHaveMoney.setVisibility(View.GONE);
                } else {
                    viewMyMoneyAtyNoneMoney.setVisibility(View.GONE);
                    viewMyMoneyAtyHaveMoney.setVisibility(View.VISIBLE);
                }
                if (resultBean.data.incomeList != null && resultBean.data.incomeList.size() > 0) {
                    mPayTeacherIncomeModel = resultBean.data;
                    if (isRefreshing) { //下拉刷新
                        mAdapter.mData = resultBean.data.incomeList;
                    } else {
                        mAdapter.mData.addAll(resultBean.data.incomeList);
                    }
                    updateView();
                }
                isRefreshing = false;

                //下拉刷新结束
                ptrEarnOrder.refreshComplete();
                //上拉加载设置
                loadMore.setIsLastPage(resultBean.data.page.total == mAdapter.getCount());
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastDefault(mContext, message);
                isRefreshing = false;
                //下拉刷新结束
                ptrEarnOrder.refreshComplete();
                //上拉加载设置
                loadMore.setIsLastPage(false);
            }
        });
    }

    /**
     * 更新界面数据
     */
    private void updateView() {
        tvMyMoneyAtyLeftMoney.setText("￥" + mPayTeacherIncomeModel.balancemoney);//总收益
        tvMyMoneyAtyAllMoney.setText("￥" + mPayTeacherIncomeModel.monthmoney);//本月实际收益
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_myMoneyAty_left://左边箭头
                index--;
                pageIndex = 1;
                getInfo();
                getHttp(String.format("%1$d%2$02d", chooseYear, chooseMonth));
                break;
            case R.id.iv_myMoneyAty_right://右边箭头
                pageIndex = 1;
                index++;
                getInfo();
                getHttp(String.format("%1$d%2$02d", chooseYear, chooseMonth));
                break;
        }
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(frame, pListMyMoneyAtyMonthMoney, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        pageIndex = 1;
        getHttp(String.format("%1$d%2$02d", chooseYear, chooseMonth));
    }

    @Override
    public void onLoadMore() {
        pageIndex++;
        getHttp(String.format("%1$d%2$02d", chooseYear, chooseMonth));
    }
}
