package com.ourslook.zuoyeba.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.EMNoActiveCallException;
import com.hyphenate.exceptions.EMServiceNotReadyException;
import com.ourslook.zuoyeba.AppConfig;
import com.ourslook.zuoyeba.BaseActivity;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ServerURL;
import com.ourslook.zuoyeba.activity.em.VideoCallNewActivity;
import com.ourslook.zuoyeba.activity.em.VoiceCallActivity;
import com.ourslook.zuoyeba.activity.em.VoiceCallNewActivity;
import com.ourslook.zuoyeba.model.AccMemberModel;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;


/**
 * Created by wy on 2015/12/10.
 * 测试代码和示例
 */
public class TestActivity extends BaseActivity implements View.OnClickListener {


    @Bind(R.id.btn_test_voice)
    Button btnTestVoice;
    @Bind(R.id.btn_test_video)
    Button btnTestVideo;
    @Bind(R.id.btn_test_login_teacher)
    Button btnTestLoginTeacher;
    @Bind(R.id.btn_test_login_student)
    Button btnTestLoginStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithDefaultTitle(R.layout.activity_test, "测试");
    }

    @Override
    protected void initView() {
        setOnClickListeners(this, btnTestVideo, btnTestVoice, btnTestLoginTeacher, btnTestLoginStudent);


    }

    private void login(String user, String pwd) {
        Map<String, String> params = new HashMap<>();
        params.put("mobile", user);// 电话
        params.put("pwd", pwd);//密码

        EasyHttp.doPost(mContext, ServerURL.LOGIN, params, null, AccMemberModel.class, false, new EasyHttp.OnEasyHttpDoneListener<AccMemberModel>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<AccMemberModel> resultBean) {
                AppConfig.isLogin = true;
                if (resultBean.data != null) {
                    AppConfig.userVo = resultBean.data;
                    AppConfig.token = resultBean.data.token;
                    AppConfig.imAcc = resultBean.data.imacc;
                    AppConfig.imPWd = resultBean.data.impwd;
                    //登录环信
                    loginEM();

                    //设置推送别名
                    JPushInterface.setAlias(mContext, resultBean.data.mobile, new TagAliasCallback() {
                        @Override
                        public void gotResult(int i, String s, Set<String> set) {
                            logForDebug(s);
                        }
                    });

                }
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                ToastUtil.showToastOnce(mContext, message);
            }
        });
    }


    private void loginEM() {
        EMClient.getInstance().login(AppConfig.imAcc, AppConfig.imPWd, new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        EMClient.getInstance().groupManager().loadAllGroups();
                        EMClient.getInstance().chatManager().loadAllConversations();

                        ToastUtil.showToastDefault(mContext, "登陆聊天服务器成功!" + EMClient.getInstance().callManager().getCallState().toString());
                        EMClient.getInstance().addConnectionListener(new MyConnectionListener());

                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {
                ToastUtil.showToastDefault(mContext, progress + "");
            }

            @Override
            public void onError(int code, String message) {
                Log.d("loginEMC---error", "登陆聊天服务器失败！");
                ToastUtil.showToastDefault(mContext, message);
            }
        });
    }

    /**
     * 环信登录掉线重新登录
     */
    private class MyConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
            //已连接到服务器
        }

        @Override
        public void onDisconnected(final int error) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    ToastUtil.showToastDefault(mContext, "环信错误连接错误-->" + error);
//                    loginEM();
                }
            });
        }
    }

    boolean isTeacher;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_test_login_student:
                login("18987180110", "q123456");
                isTeacher = false;
                break;
            case R.id.btn_test_login_teacher:
//                login("18852171326", "a123456");
//                login("15829656607", "a123456");
                login("18629477004", "123456aa");
                isTeacher = true;
                break;
            case R.id.btn_test_video:
                if (isTeacher) {
                    Intent i = new Intent(mContext, VideoCallNewActivity.class);
                    i.putExtra("isComingCall", false);
                    i.putExtra("to", "zyb_43");
//                    i.putExtra("nickname", model.teachername);
//                    i.putExtra("photourl", model.teacherphotourl);
//                    i.putExtra("workid", model.id);
                    mContext.startActivity(i);
                } else {
                    Intent i = new Intent(mContext, VideoCallNewActivity.class);
                    i.putExtra("isComingCall", false);
                    i.putExtra("to", "zyb_50");
//                    i.putExtra("nickname", model.teachername);
//                    i.putExtra("photourl", model.teacherphotourl);
//                    i.putExtra("workid", model.id);
                    mContext.startActivity(i);
                }
                break;
            case R.id.btn_test_voice:
                if (isTeacher) {
                    Intent i = new Intent(mContext, VoiceCallNewActivity.class);
                    i.putExtra("isComingCall", false);
                    i.putExtra("to", "zyb_43");
//                    i.putExtra("nickname", model.teachername);
//                    i.putExtra("photourl", model.teacherphotourl);
//                    i.putExtra("workid", model.id);
                    mContext.startActivity(i);
                } else {
                    Intent i = new Intent(mContext, VoiceCallNewActivity.class);
                    i.putExtra("isComingCall", false);
                    i.putExtra("to", "zyb_50");
//                    i.putExtra("nickname", model.teachername);
//                    i.putExtra("photourl", model.teacherphotourl);
//                    i.putExtra("workid", model.id);
                    mContext.startActivity(i);
                }
                break;
        }
    }
}
