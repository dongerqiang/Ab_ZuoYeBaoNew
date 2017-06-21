package com.ourslook.zuoyeba.activity.fragement;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.ourslook.zuoyeba.AppConfig;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.activity.fragement.student.StudentMainFragment;
import com.ourslook.zuoyeba.activity.fragement.student.StudentMineFragment;
import com.ourslook.zuoyeba.activity.fragement.teacher.TeacherMainFragment;
import com.ourslook.zuoyeba.activity.fragement.teacher.TeacherMineFragment;
import com.ourslook.zuoyeba.event.HiddenTittleEvent;
import com.ourslook.zuoyeba.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by wangyu on 16/5/19.
 */
public class ContainerFragment extends BaseFragment {

 public    StudentMainFragment mStudentMainFragment; //学生首页
    StudentMineFragment mStudentMineFragment; //学生我的

    TeacherMainFragment teacherMainFragment;
  public   TeacherMineFragment teacherMineFragment;

    int position;

    FragmentManager fm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);

        position = getArguments().getInt("position");


    }


    @Override
    protected void initView() {
        fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (position == 0) {
            mStudentMainFragment = new StudentMainFragment();
            teacherMainFragment = new TeacherMainFragment();

            ft.add(R.id.fl_fragment_container, mStudentMainFragment);
            ft.add(R.id.fl_fragment_container, teacherMainFragment);

            ft.show(mStudentMainFragment);
            ft.hide(teacherMainFragment);

            if (AppConfig.isLogin) {
                if (AppConfig.userVo.type == 1) {
                    ft.show(mStudentMainFragment);
                    ft.hide(teacherMainFragment);
                } else if (AppConfig.userVo.type == 2) {
                    ft.show(teacherMainFragment);
                    ft.hide(mStudentMainFragment);
                }
            }
        } else {
            mStudentMineFragment = new StudentMineFragment();
            teacherMineFragment = new TeacherMineFragment();

            ft.add(R.id.fl_fragment_container, mStudentMineFragment);
            ft.add(R.id.fl_fragment_container, teacherMineFragment);

            ft.show(mStudentMineFragment);
            ft.hide(teacherMineFragment);

            if (AppConfig.isLogin) {
                if (AppConfig.userVo.type == 1) {
                    ft.show(mStudentMineFragment);
                    ft.hide(teacherMineFragment);
                } else if (AppConfig.userVo.type == 2) {
                    ft.show(teacherMineFragment);
                    ft.hide(mStudentMineFragment);
                }
            }
        }
        ft.commit();

    }

    @Override
    public void onStart() {
        FragmentTransaction ft = fm.beginTransaction();
        if (position == 0) {
            if (AppConfig.isLogin) {
                if (AppConfig.userVo.type == 1) {
                    ft.show(mStudentMainFragment);
                    ft.hide(teacherMainFragment);
                } else if (AppConfig.userVo.type == 2) {
                    ft.show(teacherMainFragment);
                    ft.hide(mStudentMainFragment);
                }
            }
        } else {
            if (AppConfig.isLogin) {
                if (AppConfig.userVo.type == 1) {
                    ft.show(mStudentMineFragment);
                    ft.hide(teacherMineFragment);
                } else if (AppConfig.userVo.type == 2) {
                    ft.show(teacherMineFragment);
                    ft.hide(mStudentMineFragment);
                }
            }
        }

        ft.commit();

        super.onStart();
    }

    @Override
    public void getHttpData() {

    }
}
