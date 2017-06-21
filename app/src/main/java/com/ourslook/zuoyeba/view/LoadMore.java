package com.ourslook.zuoyeba.view;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.AbsListView;

/**
 * 上拉加载框架
 * Created by wy on 2016/1/15.
 */
public class LoadMore extends RecyclerView.OnScrollListener implements AbsListView.OnScrollListener {

    private OnLoadMoreListener loadMoreListener;
    private boolean isLastPage;

    public LoadMore(RecyclerView recyclerView){
        //noinspection deprecation
        recyclerView.setOnScrollListener(this);
    }

    public LoadMore(AbsListView absListView){
        absListView.setOnScrollListener(this);
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (isLastPage){
            return;
        }
        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            int lastVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
            if (lastVisiblePosition == recyclerView.getAdapter().getItemCount() - 1) {
                if (loadMoreListener != null) {
                    loadMoreListener.onLoadMore();
                }
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (isLastPage){
            return;
        }
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            int lastVisiblePosition = view.getLastVisiblePosition();
            if (lastVisiblePosition == view.getCount() - 1) {
                if (loadMoreListener != null) {
                    loadMoreListener.onLoadMore();
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.loadMoreListener = listener;
    }

    public void setIsLastPage(boolean isLastPage){
        this.isLastPage = isLastPage;
    }
}
