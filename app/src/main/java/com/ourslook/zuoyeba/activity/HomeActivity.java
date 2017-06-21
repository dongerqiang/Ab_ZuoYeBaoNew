package com.ourslook.zuoyeba.activity;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.EMNoActiveCallException;
import com.ourslook.zuoyeba.AppConfig;
import com.ourslook.zuoyeba.AppManager;
import com.ourslook.zuoyeba.BaseActivity;
import com.ourslook.zuoyeba.Constants;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ServerURL;
import com.ourslook.zuoyeba.activity.fragement.ContainerFragment;
import com.ourslook.zuoyeba.activity.fragement.student.StudentMainFragment;
import com.ourslook.zuoyeba.activity.fragement.student.StudentMineFragment;
import com.ourslook.zuoyeba.activity.fragement.teacher.TeacherMainFragment;
import com.ourslook.zuoyeba.activity.fragement.teacher.TeacherMineFragment;
import com.ourslook.zuoyeba.activity.login.LoginActivity;
import com.ourslook.zuoyeba.adapter.MainPagerAdapter;
import com.ourslook.zuoyeba.model.CommonVersionModel;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.PackageUtils;
import com.ourslook.zuoyeba.utils.ToastUtil;
import com.ourslook.zuoyeba.view.BottomNavigationLayout;
import com.ourslook.zuoyeba.view.dialog.LoadingDialog;
import com.ourslook.zuoyeba.view.dialog.WarningDailog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * 首页
 * Created by wy on 2015/12/11.
 */
public class HomeActivity extends BaseActivity implements
        BottomNavigationLayout.OnTabSelectedListener, CompoundButton.OnClickListener {

    @Bind(R.id.vp_home)
    ViewPager mVpHome;

    @Bind(R.id.bnl_home)
    BottomNavigationLayout mBnlHome;


    MainPagerAdapter mPagerAdapter;

    StudentMainFragment mStudentMainFragment; //学生首页
    StudentMineFragment mStudentMineFragment; //学生我的

    List<Fragment> teacherFragList = new ArrayList<>();
    TeacherMainFragment teacherMainFragment;
    TeacherMineFragment teacherMineFragment;

    ContainerFragment containerMine;

    ContainerFragment containerMain;


    long timeStamp;

    private WarningDailog mLoginDailog;// 提示的dialog
    private static final String TAG ="HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentViewWithDefaultTitle(R.layout.activity_home, "作业吧");
        //给推送设置的别名
//        if (AppConfig.isLogin) {
//            JPushInterface.setAlias(mContext, AppConfig.userVo.mobile, null);
//        }
        //友盟自动更新
//        UmengUpdateAgent.update(this);
        handler.sendEmptyMessageDelayed(0, 20000);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            putTeacherLocal();
        }


    };


    /**
     * 上传老师坐标
     */
    private void putTeacherLocal() {
        if (AppConfig.isLogin) {
            if (AppConfig.userVo.teacher != null) {
                Map<String, String> params = new HashMap<>();
                params.put("token", AppConfig.token);
                params.put("coordinatex", AppConfig.longitude + "");
                params.put("coordinatey", "" + AppConfig.latitude);
                EasyHttp.doPost(mContext, ServerURL.UPDCOORDINATE, params, null, null, false, new EasyHttp.OnEasyHttpDoneListener<Object>() {
                    @Override
                    public void onEasyHttpSuccess(ResultBean<Object> resultBean) {

                    }

                    @Override
                    public void onEasyHttpFailure(String code, String message) {
                        Log.w(TAG,"putTeacherLocal() onEasyHttpFailure message = "+message);
                    }
                });
            }
        }
    }

    //    @Subscribe
//    public void HiddenTittle(HiddenTittleEvent event){
//        mIvTitleLeft.setVisibility(View.INVISIBLE);
//        mIvTitleRight.setVisibility(View.INVISIBLE);
//        mTvTitleLeft.setVisibility(View.INVISIBLE);
//    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void initView() {
        mIvTitleBack.setVisibility(View.GONE);
        mIvTitleLeft.setImageResource(R.drawable.dingwei);
        mTvTitleLeft.setText(AppConfig.city);
        mIvTitleRight.setImageResource(R.drawable.xiaoxi);
        mTvTitleLeft.setVisibility(View.VISIBLE);
        mIvTitleLeft.setVisibility(View.VISIBLE);
        mIvTitleRight.setVisibility(View.VISIBLE);

        setOnClickListeners(this, mIvTitleRight);
        initPager();

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.CODE_201);
        } else {
            checkUpdateVersion();
        }
    }

    /**
     * 弹出提示框(未登录)
     */
    private void showWarningDialogLogin() {
        if (mLoginDailog == null)
            mLoginDailog = new WarningDailog(mContext, R.style.FullHeightDialog, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLoginDailog.dismiss();
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                }
            });
        mLoginDailog.show();
    }


    private void initPager() {
        mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());

        containerMain = new ContainerFragment();
        Bundle bundleMain = new Bundle();
        bundleMain.putInt("position", 0);
        containerMain.setArguments(bundleMain);


        containerMine = new ContainerFragment();
        Bundle bundleMine = new Bundle();
        bundleMine.putInt("position", 1);
        containerMine.setArguments(bundleMine);

//        mStudentMainFragment = new StudentMainFragment();
//        mStudentMineFragment = new StudentMineFragment();
//
//        teacherMineFragment=new TeacherMineFragment();
//        teacherMainFragment=new TeacherMainFragment();

//        teacherFragList.add(teacherMainFragment);
//        teacherFragList.add(teacherMineFragment);

        mPagerAdapter.addFragment(containerMain, "首页");
        mPagerAdapter.addFragment(containerMine, "我的");

        mVpHome.setAdapter(mPagerAdapter);

        int[] unselected = new int[]{R.drawable.home2, R.drawable.cloud1};
        int[] selected = new int[]{R.drawable.home1, R.drawable.cloud2};

        mBnlHome.setupWithViewPager(mVpHome);

        mBnlHome.setTabImage(unselected, selected);

        mBnlHome.setOnTabSelectedListener(this);
    }

    @Override
    public void onTabSelected(int position) {
        if (position == 0) {
            mTvTitleName.setText("作业吧");
            mTvTitleLeft.setVisibility(View.VISIBLE);
            mIvTitleLeft.setVisibility(View.VISIBLE);
            mIvTitleRight.setVisibility(View.VISIBLE);
            if (AppConfig.isLogin) {
                if (AppConfig.userVo.teacher != null) {
                    mTvTitleLeft.setVisibility(View.INVISIBLE);
                    mIvTitleLeft.setVisibility(View.INVISIBLE);
                    mIvTitleRight.setVisibility(View.INVISIBLE);
                }
                if (AppConfig.userVo.type == 1) {//学生端
                    Log.w(TAG,"containerMain = "+containerMain+"\nmStudentMainFragment = "+mStudentMainFragment);
                    if(containerMain !=null && mStudentMainFragment!=null){
                        containerMain.mStudentMainFragment.getAppointment();
                    }
                }
            }
        } else {
            if (!AppConfig.isLogin) {
                showWarningDialogLogin();
            } else {
                if (AppConfig.userVo.type == 2) {//每次切换调用接口
                    containerMine.teacherMineFragment.refreshUserInfo();
                }
            }
            mTvTitleName.setText("我的");
            mTvTitleLeft.setVisibility(View.INVISIBLE);
            mIvTitleLeft.setVisibility(View.INVISIBLE);
            mIvTitleRight.setVisibility(View.INVISIBLE);
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (AppConfig.isLogin && AppConfig.userVo.type == 2) {
            mTvTitleLeft.setVisibility(View.INVISIBLE);
            mIvTitleLeft.setVisibility(View.INVISIBLE);
            mIvTitleRight.setVisibility(View.INVISIBLE);
        } else {
            mTvTitleLeft.setVisibility(View.VISIBLE);
            mIvTitleLeft.setVisibility(View.VISIBLE);
            mIvTitleRight.setVisibility(View.VISIBLE);
        }

        if (mVpHome.getCurrentItem() == 0) {
            mTvTitleName.setText("作业吧");
            mTvTitleLeft.setVisibility(View.VISIBLE);
            mIvTitleLeft.setVisibility(View.VISIBLE);
            mIvTitleRight.setVisibility(View.VISIBLE);
            if (AppConfig.isLogin) {
                if (AppConfig.userVo.teacher != null) {
                    mTvTitleLeft.setVisibility(View.INVISIBLE);
                    mIvTitleLeft.setVisibility(View.INVISIBLE);
                    mIvTitleRight.setVisibility(View.INVISIBLE);
                }
            }
        } else {
            if (!AppConfig.isLogin) {
                showWarningDialogLogin();
            } else {//切换时需要调用接口

            }
            mTvTitleName.setText("我的");
            mTvTitleLeft.setVisibility(View.INVISIBLE);
            mIvTitleLeft.setVisibility(View.INVISIBLE);
            mIvTitleRight.setVisibility(View.INVISIBLE);

        }

        try {
            EMClient.getInstance().callManager().endCall();
        } catch (EMNoActiveCallException e) {

        }
//        startActivity(new Intent(HomeActivity.this,QuanActivity.class));
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - timeStamp > 2000) {
            ToastUtil.showToastOnce(this, "再按一次退出程序");
        } else {
            AppManager.getInstance().AppExit(this);
        }
        timeStamp = System.currentTimeMillis();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_title_right://消息中心
                if (AppConfig.isLogin) {
                    openActivity(MessageActivity.class);
                } else {
                    showWarningDialogLogin();
                }

                break;
        }
    }

    //TODO 一下为2016年10月9日根据需求添加的强制更新代码
    int versionCode;
    String versionName;
    long downloadId;
    private final String apkName = "zuoyeba_update.apk";
    AlertDialog mAlertDialog;


    private void checkUpdateVersion() {
        versionCode = PackageUtils.getVersionCode(mContext);
        versionName = PackageUtils.getVersionName(mContext);
        int local_version_name = PackageUtils.dealVersionlName(versionName);
        Log.d(TAG,"LOCAL VERSION = "+local_version_name +"\n versioncode = "+versionCode);

        Map<String, String> params = new HashMap<>();
        params.put("type", "2");
        LoadingDialog.showLoadingDialog(mContext);
        EasyHttp.doPost(mContext, ServerURL.GETVERSION, params, null, CommonVersionModel.class, false, new EasyHttp.OnEasyHttpDoneListener<CommonVersionModel>() {
            @Override
            public void onEasyHttpSuccess(final ResultBean<CommonVersionModel> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                int remote_version = PackageUtils.dealVersionlName(resultBean.data.version);
                Log.d(TAG,"remote version = "+remote_version);
                if (PackageUtils.dealVersionlName(resultBean.data.version) > PackageUtils.dealVersionlName(versionName)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("发现新版本:" + resultBean.data.version + ",确认下载?");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (resultBean.data.url != null) {
                                startDownload(resultBean.data.url);
                            } else {
                                ToastUtil.showToastOnce(mContext, "下载失败，请重新下载");
                            }
                        }
                    });
                    if (resultBean.data.must) {
                        builder.setCancelable(false);
                    } else {
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAlertDialog.dismiss();
                            }
                        });
                    }
                    mAlertDialog = builder.create();
                    mAlertDialog.show();
                } else {
//                    ToastUtil.showToastDefault(mContext, "当前是最新版");
                }
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                LoadingDialog.dismissLoadingDialog();

                Log.w(TAG,"checkUpdateVersion() onEasyHttpFailure message = "+message);
                ToastUtil.showToastDefault(mContext, message);
            }
        });
    }

    private void startDownload(String url) {
        DownloadManager downloadManager = (DownloadManager) mContext
                .getSystemService(Context.DOWNLOAD_SERVICE);

        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        // 设置允许使用的网络类型，这里是移动网络和wifi都可以
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
                | DownloadManager.Request.NETWORK_WIFI);

        String path = Environment.getExternalStorageDirectory()
                + Constants.DOWNLOAD_PATH + apkName;

        File file = new File(path);

        if (file.exists()) {
            file.delete();
        }
        // 外部存储
        request.setDestinationInExternalPublicDir(Constants.DOWNLOAD_PATH,
                apkName);
        // 内部存储
        // request.setDestinationInExternalFilesDir(mContext,
        // Constants.DOWNLOAD_FILE, apkName);

        downloadId = downloadManager.enqueue(request);

        mContext.registerReceiver(receiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 这里可以取得下载的id，这样就可以知道哪个文件下载完成了。适用与多个下载任务的监听
            logForDebug(intent.getLongExtra(
                    DownloadManager.EXTRA_DOWNLOAD_ID, 0) + "");
            if (intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0) == downloadId) {
                String path = Environment.getExternalStorageDirectory()
                        + Constants.DOWNLOAD_PATH + apkName;
                installApk(path);
            }

        }
    };

    /**
     * 安装APK文件
     */
    public void installApk(String path) {
        File apkfile = new File(path);
        if (!apkfile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        mContext.startActivity(i);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkUpdateVersion();
        } else {
            ToastUtil.showToastDefault(mContext, "版本更新需要开启sd卡权限---->应用---->权限");
        }
    }

}
