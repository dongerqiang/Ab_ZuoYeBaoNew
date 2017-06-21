package com.ourslook.zuoyeba.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.ourslook.zuoyeba.AppConfig;
import com.ourslook.zuoyeba.BaseActivity;
import com.ourslook.zuoyeba.Constants;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ServerURL;
import com.ourslook.zuoyeba.adapter.MyMessageAdapter;
import com.ourslook.zuoyeba.model.InfMessageModel;
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
 * 消息界面
 */
public class MessageActivity extends BaseActivity implements AdapterView.OnItemClickListener, PtrHandler, LoadMore.OnLoadMoreListener {
    @Bind(R.id.view_myMsgAty_newMsg)
    LinearLayout viewMyMsgAtyNewMsg;
    @Bind(R.id.lv_myMsgAty)
    ListView lvMyMsgAty;
    @Bind(R.id.view_myMsgAty_haveMsg)
    LinearLayout viewMyMsgAtyHaveMsg;
    @Bind(R.id.view_myMsgAty_noMsg)
    RelativeLayout viewMyMsgAtyNoMsg;

    MyMessageAdapter mMyMessageAdapter;

    int pageIndex = 1;
    @Bind(R.id.ptr_message_order)
    PtrClassicFrameLayout ptrMessageOrder;

    LoadMore loadMore;
    boolean isRefreshing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithDefaultTitle(R.layout.activity_message, "我的消息");
    }

    @Override
    protected void initView() {
        mMyMessageAdapter = new MyMessageAdapter(mContext, null, R.layout.item_meaaage);
        lvMyMsgAty.setAdapter(mMyMessageAdapter);
        lvMyMsgAty.setOnItemClickListener(this);
        //下拉刷新初始化
        ptrMessageOrder.setPtrHandler(this);
        ptrMessageOrder.setLastUpdateTimeRelateObject(this);

        //上拉加载初始化
        loadMore = new LoadMore(lvMyMsgAty);
        loadMore.setOnLoadMoreListener(this);
        //noinspection deprecation
        lvMyMsgAty.setOnScrollListener(loadMore);
        getHttpData();
    }


    /**
     * 获取消息列表数据
     */
    private void getHttpData() {
        LoadingDialog.showLoadingDialog(mContext);
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", AppConfig.token);
        params.put("page", "" + pageIndex);
        params.put("rows", "" + Constants.ROWS);
        if (pageIndex == 1) {
            isRefreshing = true;
        }
        EasyHttp.doPost(mContext, ServerURL.GETMESSAGELIST, params, null, InfMessageModel.class, false, new EasyHttp.OnEasyHttpDoneListener<InfMessageModel>() {
                    @Override
                    public void onEasyHttpSuccess(ResultBean<InfMessageModel> resultBean) {
                        LoadingDialog.dismissLoadingDialog();
                            if (resultBean.data.messageList.size() > 0) {
                                if (isRefreshing) { //下拉刷新
                                    mMyMessageAdapter.mData = resultBean.data.messageList;
                                } else {
                                    mMyMessageAdapter.mData.addAll(resultBean.data.messageList);
                                }
                                mMyMessageAdapter.notifyDataSetChanged();
                                viewMyMsgAtyHaveMsg.setVisibility(View.VISIBLE);
                                viewMyMsgAtyNoMsg.setVisibility(View.GONE);
                                boolean allRead = true;
                                for (int i = 0; i < mMyMessageAdapter.mData.size(); i++) {
                                    if (!mMyMessageAdapter.mData.get(i).isread) {
                                        allRead = false;
                                        break;
                                    }
                                }
                                viewMyMsgAtyNewMsg.setVisibility(allRead ? View.GONE : View.VISIBLE);
                            }
                        if( mMyMessageAdapter.mData.size()==0){
                            viewMyMsgAtyHaveMsg.setVisibility(View.GONE);
                            viewMyMsgAtyNoMsg.setVisibility(View.VISIBLE);
                        }else{
                            viewMyMsgAtyHaveMsg.setVisibility(View.VISIBLE);
                            viewMyMsgAtyNoMsg.setVisibility(View.GONE);
                        }
                        isRefreshing = false;

                        //下拉刷新结束
                        ptrMessageOrder.refreshComplete();
                        //上拉加载设置
                        loadMore.setIsLastPage(resultBean.data.page.total == mMyMessageAdapter.getCount());

                    }

                    @Override
                    public void onEasyHttpFailure(String code, String message) {
                        LoadingDialog.dismissLoadingDialog();
                        ToastUtil.showToastDefault(mContext, message);
                        isRefreshing = false;
                        //下拉刷新结束
                        ptrMessageOrder.refreshComplete();
                        //上拉加载设置
                        loadMore.setIsLastPage(false);
                    }
                }
        );
    }


    /**
     * 子条目点击  判断是否已读
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!mMyMessageAdapter.mData.get((int) id).isread) {
            changeState(id);
        }
    }

    /**
     * 已读未读
     */
    private void changeState(final long index) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", AppConfig.token);
        LoadingDialog.showLoadingDialog(mContext);
        params.put("messageids", mMyMessageAdapter.mData.get((int) index).id + "");
        EasyHttp.doPost(mContext, ServerURL.READMESSAGE, params, null, null, false, new EasyHttp.OnEasyHttpDoneListener<Object>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<Object> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                mMyMessageAdapter.mData.get((int) index).isread = true;
                mMyMessageAdapter.notifyDataSetChanged();
                boolean allRead = true;
                for (int i = 0; i < mMyMessageAdapter.mData.size(); i++) {
                    if (!mMyMessageAdapter.mData.get(i).isread) {
                        allRead = false;
                        break;
                    }
                }
                if (allRead) {
                    viewMyMsgAtyNewMsg.setVisibility(View.GONE);
                }

            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastDefault(mContext, message);
            }
        });
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(frame, lvMyMsgAty, header);
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
