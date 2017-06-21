package com.ourslook.zuoyeba.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.ourslook.zuoyeba.AppConfig;
import com.ourslook.zuoyeba.AppManager;
import com.ourslook.zuoyeba.BaseActivity;
import com.ourslook.zuoyeba.Constants;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ServerURL;
import com.ourslook.zuoyeba.ZuoYeBaApplication;
import com.ourslook.zuoyeba.adapter.MyRedBagAdapter;
import com.ourslook.zuoyeba.model.PayRedPacketModel;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.DisplayUtils;
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
 * Created by huangyi on 16/5/16.
 * 红包界面
 */
public class RedBagActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, PtrHandler, LoadMore.OnLoadMoreListener {
    @Bind(R.id.iv_myRedAty_back)
    ImageView ivMyRedAtyBack;
    @Bind(R.id.iv_myRedAty_head)
    ImageView ivMyRedAtyHead;
    @Bind(R.id.lv_myRedAty)
    ListView lvMyRedAty;
    @Bind(R.id.view_myRedAty_noRed)
    RelativeLayout viewMyRedAtyNoRed;

    int pageIndex = 1;

    LoadMore loadMore;
    boolean isRefreshing;

    int status = -1;//此处需要筛选红包的使用状态(只显示未使用红包)


    long teacherId = -1;

    long orderId = -1;


    MyRedBagAdapter mMyRedBagAdapter;
    @Bind(R.id.ptr_red_bage_order)
    PtrClassicFrameLayout ptrRedBageOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO 此处需要接受状态用于区分是否筛选
        teacherId = getIntent().getLongExtra(Constants.TEACHER_ID, -1);
        orderId = getIntent().getLongExtra(Constants.PASS_ORDER, -1);
        status = getIntent().getIntExtra(Constants.PASS_STATUS, -1);
        setContentView(R.layout.activity_redbag);
        ButterKnife.bind(this);
    }

    @Override
    protected void initView() {
        mMyRedBagAdapter = new MyRedBagAdapter(mContext, null, R.layout.item_redbag);
        lvMyRedAty.setAdapter(mMyRedBagAdapter);
        lvMyRedAty.setOnItemClickListener(this);
        int cornerRadius = DisplayUtils.dp2px(40, mContext);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(cornerRadius))
                .build();
        //加载用户头像
        if (AppConfig.userVo.photourl != null) {
            if (AppConfig.userVo.photourl.length() > 0) {
                ZuoYeBaApplication.imageLoader.displayImage(AppConfig.userVo.photourl, ivMyRedAtyHead, options);
            }

        }
        setOnClickListeners(this, ivMyRedAtyBack);
        //下拉刷新初始化
        ptrRedBageOrder.setPtrHandler(this);
        ptrRedBageOrder.setLastUpdateTimeRelateObject(this);

        //上拉加载初始化
        loadMore = new LoadMore(lvMyRedAty);
        loadMore.setOnLoadMoreListener(this);
        //noinspection deprecation
        lvMyRedAty.setOnScrollListener(loadMore);
        getHttpData();
    }

    /**
     * 获取红包列表数据
     */
    private void getHttpData() {
        LoadingDialog.showLoadingDialog(mContext);
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", AppConfig.token);
        params.put("page", "" + pageIndex);
        params.put("rows", "" + Constants.ROWS);
        if (status != -1) {
            params.put("status", "" + 1);
        }
        if (pageIndex == 1) {
            isRefreshing = true;
        }
        EasyHttp.doPost(mContext, ServerURL.GETREDPACKETLIST, params, null, PayRedPacketModel.class, false, new EasyHttp.OnEasyHttpDoneListener<PayRedPacketModel>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<PayRedPacketModel> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                if (resultBean.data.page.total > 0) {
                    viewMyRedAtyNoRed.setVisibility(View.INVISIBLE);
                    lvMyRedAty.setVisibility(View.VISIBLE);
                } else {
                    viewMyRedAtyNoRed.setVisibility(View.VISIBLE);
                    lvMyRedAty.setVisibility(View.INVISIBLE);
                }
                if (resultBean.data.redpacketList != null && resultBean.data.redpacketList.size() > 0) {
                    if (isRefreshing) { //下拉刷新
                        mMyRedBagAdapter.mData = resultBean.data.redpacketList;
                    } else {
                        mMyRedBagAdapter.mData.addAll(resultBean.data.redpacketList);
                    }
                    mMyRedBagAdapter.notifyDataSetChanged();
                }
                isRefreshing = false;

                //下拉刷新结束
                ptrRedBageOrder.refreshComplete();
                //上拉加载设置
                loadMore.setIsLastPage(resultBean.data.page.total == mMyRedBagAdapter.getCount());

            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastDefault(mContext, message);
                isRefreshing = false;
                //下拉刷新结束
                ptrRedBageOrder.refreshComplete();
                //上拉加载设置
                loadMore.setIsLastPage(false);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_myRedAty_back:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (status == -1) {
            return;
        }
        sureOrder(mMyRedBagAdapter.mData.get((int) id).id);

    }


    /**
     * 确认订单   跳转到学生订单详情界面
     *
     * @param redBagId
     */
    private void sureOrder(long redBagId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", AppConfig.token);
        params.put("teacherid", teacherId + "");
        params.put("redpacketid", redBagId + "");
        EasyHttp.doPost(mContext, ServerURL.CONFIRMTEACHER, params, null, null, false, new EasyHttp.OnEasyHttpDoneListener<Object>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<Object> resultBean) {
                ToastUtil.showToastDefault(mContext, "确认老师成功");
                PickUpTeacherActivity.flag=true;
                Intent intent = new Intent(mContext, StudentOrderDetailNoFinishActivity.class);
                intent.putExtra(Constants.PASS_ORDER, orderId);
                startActivity(intent);
                finish();
                AppManager.getInstance().finishActivity(DetailTeacherActivity.class);
                AppManager.getInstance().finishActivity(PickUpTeacherActivity.class);
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                ToastUtil.showToastDefault(mContext, message);
            }
        });

    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(frame, lvMyRedAty, header);
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
