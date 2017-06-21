package com.ourslook.zuoyeba.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioButton;

import com.ourslook.zuoyeba.BaseActivity;
import com.ourslook.zuoyeba.Constants;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.activity.fragement.student.StudentFinishOrderListFragment;
import com.ourslook.zuoyeba.activity.fragement.student.StudentUNFinishOrderListFragment;
import com.ourslook.zuoyeba.activity.fragement.teacher.TeacherFinishOrderListFragment;
import com.ourslook.zuoyeba.activity.fragement.teacher.TeacherUNFinishOrderListFragment;
import com.ourslook.zuoyeba.adapter.MyOrderListFragmentAdapter;
import com.ourslook.zuoyeba.event.RefreshOrderListEvent;
import com.ourslook.zuoyeba.net.EasyHttp;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created by huangyi on 16/5/16. 订单列表(老师+学生)
 */
public class OrderListActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    @Bind(R.id.rd_left)
    RadioButton rdLeft;
    @Bind(R.id.rd_right)
    RadioButton rdRight;
    @Bind(R.id.vp_my_indent)
    ViewPager vpMyIndent;
    StudentFinishOrderListFragment mStudentFinishOrderListFragment;
    StudentUNFinishOrderListFragment mStudentUNFinishOrderListFragment;
    TeacherFinishOrderListFragment mTeacherFinishOrderListFragment;
    TeacherUNFinishOrderListFragment mTeacherUNFinishOrderListFragment;
    private ArrayList<Fragment> mStudentFragments = new ArrayList<>();
    private ArrayList<Fragment> mTeacherFragments = new ArrayList<>();
    //放置radiobutton
    private RadioButton[] rbs;

    private String DIS_TYPE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DIS_TYPE = getIntent().getStringExtra(Constants.DIS_TYPE);
        setContentViewWithDefaultTitle(R.layout.order_list_activity, "我的订单");

        EventBus.getDefault().register(this);
    }

    @Override
    protected void initView() {
        vpMyIndent.addOnPageChangeListener(this);
        rbs = new RadioButton[3];
        rbs[0] = rdLeft;
        rbs[1] = rdRight;
        setOnClickListeners(this, rbs[0], rbs[1]);
        mStudentFinishOrderListFragment = new StudentFinishOrderListFragment();
        mStudentUNFinishOrderListFragment = new StudentUNFinishOrderListFragment();
        mTeacherUNFinishOrderListFragment = new TeacherUNFinishOrderListFragment();
        mTeacherFinishOrderListFragment = new TeacherFinishOrderListFragment();
        mTeacherFragments.add(mTeacherUNFinishOrderListFragment);
        mTeacherFragments.add(mTeacherFinishOrderListFragment);
        mStudentFragments.add(mStudentUNFinishOrderListFragment);
        mStudentFragments.add(mStudentFinishOrderListFragment);
        if (Constants.STUDENT.equals(DIS_TYPE)) {//学生
            vpMyIndent.setAdapter(new MyOrderListFragmentAdapter(getSupportFragmentManager(), mStudentFragments));
        } else {
            vpMyIndent.setAdapter(new MyOrderListFragmentAdapter(getSupportFragmentManager(), mTeacherFragments));
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                rbs[0].setChecked(true);
                break;
            case 1:
                rbs[1].setChecked(true);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rd_left:
                vpMyIndent.setCurrentItem(0);
                break;
            case R.id.rd_right:
                vpMyIndent.setCurrentItem(1);
                break;
            default:
                break;

        }
    }

    @Override
    protected void onDestroy() {
        EasyHttp.cancelRequest(mContext);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onEvent(RefreshOrderListEvent event) {
        if (Constants.STUDENT.equals(DIS_TYPE)) {//学生

        } else {//教师
            if (event.index == 0) {//刷新未完成订单列表
                ((TeacherUNFinishOrderListFragment) mTeacherFragments.get(0)).getHttpData();
            } else if (event.index == 1) {//刷新已完成订单列表
                ((TeacherFinishOrderListFragment) mTeacherFragments.get(1)).getHttpData();
            } else if (event.index == 2) {//刷新所有订单列表
                ((TeacherUNFinishOrderListFragment) mTeacherFragments.get(0)).getHttpData();
                ((TeacherFinishOrderListFragment) mTeacherFragments.get(1)).getHttpData();
            }

        }
    }
}
