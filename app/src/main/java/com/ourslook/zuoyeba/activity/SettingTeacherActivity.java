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
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.ourslook.zuoyeba.AppConfig;
import com.ourslook.zuoyeba.BaseActivity;
import com.ourslook.zuoyeba.Constants;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ServerURL;
import com.ourslook.zuoyeba.activity.login.LoginActivity;
import com.ourslook.zuoyeba.model.CommonVersionModel;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.PackageUtils;
import com.ourslook.zuoyeba.utils.PreferencesManager;
import com.ourslook.zuoyeba.utils.ToastUtil;
import com.ourslook.zuoyeba.view.dialog.LoadingDialog;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import cn.jpush.android.api.JPushInterface;

/**
 * 设置(老师)
 * Created by DuanLu on 2016/5/19.
 */
public class SettingTeacherActivity extends BaseActivity implements CompoundButton.OnClickListener {
    @Bind(R.id.ll_setting_teacher_update)
    LinearLayout mLlUpdate;//版本更新
    @Bind(R.id.tv_setting_teacher_code)
    TextView mTvCode;//版本名
    @Bind(R.id.ll_setting_teacher_about)
    LinearLayout mLlAbout;//关于我们
    @Bind(R.id.ll_setting_teacher_changePwd)
    LinearLayout mChangePwd;//更改密码
    @Bind(R.id.ll_setting_teacher_stel)
    LinearLayout mLlStel;//客服电话
    @Bind(R.id.tv_setting_teacher_stel)
    TextView mTvStel;//客服电话号码
    @Bind(R.id.tv_setting_teacher_exit)
    TextView mTvExit;//退出

    private String mStel;//客服电话号码

    String versionName;
    int versionCode;

    long downloadId;
    private final String apkName = "zuoyeba_update.apk";
    AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("--", "SettingTeacherActivity~~");
        setContentViewWithDefaultTitle(R.layout.activity_setting_teacher, "设置");
    }

    @Override
    protected void initView() {
        getServiceTel();

        versionCode = PackageUtils.getVersionCode(mContext);
        versionName = PackageUtils.getVersionName(mContext);
        mTvCode.setText(versionName);
        setOnClickListeners(this, mLlUpdate, mLlAbout, mChangePwd, mLlStel, mTvExit);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkVersion();
        } else {
            ToastUtil.showToastDefault(mContext, "版本更新需要开启sd卡权限---->应用---->权限");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_setting_teacher_update://版本更新
                //checkVersion();
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.CODE_201);
                } else {
                    checkVersion();
                }
                break;
            case R.id.ll_setting_teacher_about://关于我们
                Intent intentOurs = new Intent(this, AboutOursActivity.class);
                startActivity(intentOurs);
                break;
            case R.id.ll_setting_teacher_changePwd://更改密码
                Intent intentChangePassword = new Intent(this, ChangePasswordActivity.class);
                startActivity(intentChangePassword);
                break;
            case R.id.ll_setting_teacher_stel://客服电话
                showCallDialog();
                break;
            case R.id.tv_setting_teacher_exit://退出登录
                logout();
                AppConfig.isLogin = false;
                AppConfig.userVo = null;
                AppConfig.token = "";
                AppConfig.iscreditvalid = false;
                clearShare();
                Intent intentLogin = new Intent(this, LoginActivity.class);
                intentLogin.putExtra("source", 1);
                startActivity(intentLogin);
                finish();
                break;
        }
    }

    /**
     * 置空Share
     */
    private void clearShare() {
        PreferencesManager preference = PreferencesManager.getInstance(mContext);
        preference.put(Constants.PASSWORD, "");
        preference.put(Constants.ACCOUNT, "");
    }

    private void checkVersion() {
        Map<String, String> params = new HashMap<>();
        params.put("type", "2");
        LoadingDialog.showLoadingDialog(mContext);
        EasyHttp.doPost(mContext, ServerURL.GETVERSION, params, null, CommonVersionModel.class, false, new EasyHttp.OnEasyHttpDoneListener<CommonVersionModel>() {
            @Override
            public void onEasyHttpSuccess(final ResultBean<CommonVersionModel> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                if (PackageUtils.dealVersionlName(resultBean.data.version) > PackageUtils.dealVersionlName(versionName)) {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
                    builder.setMessage("发现新版本:" + resultBean.data.version + ",确认下载?");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startDownload(resultBean.data.url);
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAlertDialog.dismiss();
                        }
                    });
                    mAlertDialog = builder.create();
                    mAlertDialog.show();
                } else {
                    ToastUtil.showToastDefault(mContext, "当前是最新版");
                }
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                LoadingDialog.dismissLoadingDialog();
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

    /**
     * 获取客服电话
     */
    private void getServiceTel() {
        LoadingDialog.showLoadingDialog(mContext);
        Map<String, String> params = new HashMap<String, String>();
        EasyHttp.doPost(mContext, ServerURL.GETSERVICETEL, params, null, null, false, new EasyHttp.OnEasyHttpDoneListener<Object>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<Object> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                mLlStel.setVisibility(View.VISIBLE);
                mStel = resultBean.data.toString();
                mTvStel.setText(mStel);
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastOnce(mContext, message);
            }
        });
    }

    /**
     * 拨打电话
     */
    private void showCallDialog() {
        if (TextUtils.isEmpty(mStel)) {
            ToastUtil.showToastOnce(mContext, "获取客服电话失败");
            mLlStel.setClickable(false);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("是否拨打客服电话");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //拨打电话
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    Uri data = Uri.parse("tel:" + mStel);
                    intent.setData(data);
                    if (ActivityCompat.checkSelfPermission(SettingTeacherActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("取消", null);
            builder.show();
        }
    }

    /**
     * 退出环信和极光推送
     */
    private void logout() {
        EMClient.getInstance().logout(true, new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.d("loginEMC---success", "退出聊天服务器成功！");
            }

            @Override
            public void onError(int i, String s) {
                Log.d("loginEMC---error", "退出聊天服务器失败！");
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
        //退出极光推送
        if (!JPushInterface.isPushStopped(getApplicationContext())) {
            JPushInterface.stopPush(getApplicationContext());
        }
    }

}
