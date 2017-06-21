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
import android.util.Log;
import android.view.View;
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
import java.util.Set;

import butterknife.Bind;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by huangyi on 16/5/16.
 * 设置界面
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.tv_settingAtyS_v)
    TextView tvSettingAtySV;   //版本号
    @Bind(R.id.ll_settingAtyS_update)
    LinearLayout llSettingAtySUpdate;//版本更新
    @Bind(R.id.ll_settingAtyS_aboutUs)
    LinearLayout llSettingAtySAboutUs;//关于我们
    @Bind(R.id.ll_settingAtyS_changePwd)
    LinearLayout llSettingAtySChangePwd;//修改密码
    @Bind(R.id.ll_settingAtyS_changePhone)
    LinearLayout llSettingAtySChangePhone;//修改手机号
    @Bind(R.id.ll_settingAtyS_cancel_bind)
    LinearLayout llSettingAtySCancelBind;//取消绑定支付宝
    @Bind(R.id.tv_settingAtyS_stel)
    TextView tvSettingAtySStel;
    @Bind(R.id.ll_settingAtyS_stel)
    LinearLayout llSettingAtySStel;
    @Bind(R.id.tv_settingAtyS_exit)
    TextView tvSettingAtySExit;//退出登录

    int versionCode;
    String versionName;

    long downloadId;
    private final String apkName = "zuoyeba_update.apk";
    AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithDefaultTitle(R.layout.activity_setting, "设置");
    }

    @Override
    protected void initView() {
        versionCode = PackageUtils.getVersionCode(mContext);
        versionName = PackageUtils.getVersionName(mContext);
        tvSettingAtySV.setText(versionName);
        setOnClickListeners(this, llSettingAtySUpdate, llSettingAtySAboutUs, llSettingAtySChangePwd, llSettingAtySChangePhone, llSettingAtySCancelBind, tvSettingAtySExit);


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppConfig.iscreditvalid) {
            llSettingAtySCancelBind.setVisibility(View.VISIBLE);
        } else {
            llSettingAtySCancelBind.setVisibility(View.GONE);
        }
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
            case R.id.ll_settingAtyS_update://版本跟新
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.CODE_201);
                } else {
                    checkVersion();
                }
                break;
            case R.id.ll_settingAtyS_aboutUs://关于我们
                Intent intentOurs = new Intent(this, AboutOursActivity.class);
                startActivity(intentOurs);
                break;
            case R.id.ll_settingAtyS_changePwd://修改密码
                Intent intentChangePassword = new Intent(this, ChangePasswordActivity.class);
                startActivity(intentChangePassword);
                break;
            case R.id.ll_settingAtyS_changePhone://修改手机号
                Intent intentChangePhoneNumber = new Intent(this, ChangePhoneNumberActivity.class);
                startActivity(intentChangePhoneNumber);
                break;
            case R.id.ll_settingAtyS_cancel_bind://取消绑定支付宝
                openActivity(CancelBindAlipayActivity.class);
                break;
            case R.id.tv_settingAtyS_exit://退出登录
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
        //设置推送别名
        JPushInterface.setAlias(mContext, "", new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                logForDebug(s);
            }
        });
    }
}
