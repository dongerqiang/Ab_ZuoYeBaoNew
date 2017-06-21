package com.ourslook.zuoyeba.activity.em;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMCallStateChangeListener.CallError;
import com.hyphenate.chat.EMCallStateChangeListener.CallState;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.EMNoActiveCallException;
import com.hyphenate.exceptions.EMServiceNotReadyException;
import com.hyphenate.media.EMLocalSurfaceView;
import com.hyphenate.media.EMOppositeSurfaceView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ourslook.zuoyeba.AppConfig;
import com.ourslook.zuoyeba.AppManager;
import com.ourslook.zuoyeba.BaseActivity;
import com.ourslook.zuoyeba.Constants;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ServerURL;
import com.ourslook.zuoyeba.activity.StudentOrderDetailFinishActivity;
import com.ourslook.zuoyeba.activity.StudentOrderDetailNoFinishActivity;
import com.ourslook.zuoyeba.activity.TeacherOrderDetailActivity;
import com.ourslook.zuoyeba.activity.TeacherOrderEndDetailActivity;
import com.ourslook.zuoyeba.model.AccMemberImModel;
import com.ourslook.zuoyeba.model.OrderCostModel;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.ToastUtil;
import com.ourslook.zuoyeba.view.MyChronometer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

/**
 * 视频通话重写
 * Created by wangyu on 16/5/23.
 */
public class VideoCallNewActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.iv_changeCamera)
    ImageView mIvChangeCamera;

    @Bind(R.id.opposite_surface)
    EMOppositeSurfaceView oppositeSurface;
    @Bind(R.id.tv_call_state)
    TextView tvCallState;
    @Bind(R.id.chronometer)
    MyChronometer chronometer;
    @Bind(R.id.tv_is_p2p)
    TextView tvIsP2p;
    @Bind(R.id.tv_nick)
    TextView tvNick;
    @Bind(R.id.ll_top_container)
    LinearLayout llTopContainer;
    @Bind(R.id.swing_video_card)
    ImageView swingVideoCard;
    @Bind(R.id.tv_video_nick)
    TextView tvVideoNick;
    @Bind(R.id.tv_call_monitor)
    TextView tvCallMonitor;
    @Bind(R.id.local_surface)
    EMLocalSurfaceView localSurface;
    @Bind(R.id.iv_mute)
    ImageView ivMute;
    @Bind(R.id.iv_handsfree)
    ImageView ivHandsfree;
    @Bind(R.id.ll_voice_control)
    LinearLayout llVoiceControl;
    @Bind(R.id.btn_hangup_call)
    Button btnHangupCall;
    @Bind(R.id.btn_refuse_call)
    Button btnRefuseCall;
    @Bind(R.id.btn_answer_call)
    Button btnAnswerCall;
    @Bind(R.id.ll_coming_call)
    LinearLayout llComingCall;
    @Bind(R.id.ll_bottom_container)
    LinearLayout llBottomContainer;
    @Bind(R.id.ll_surface_baseline)
    LinearLayout llSurfaceBaseline;
    @Bind(R.id.ll_btns)
    RelativeLayout llBtns;
    @Bind(R.id.root_layout)
    RelativeLayout rootLayout;

    boolean isComingCall;// 是否是打进来的
    //给谁打
    String to;
    //谁打进来的
    String from;
    //订单id
    String workId;

    AudioManager audioManager;
    SoundPool soundPool;
    Ringtone ringtone;
    int outgoing;
    int streamID = -1;
    EMCallStateChangeListener emListener;

    private boolean isMuteState;
    private boolean isHandsfreeState;

    private boolean isStart;//判断是否开始作业

    OrderCostModel orderCostModel;// 订单价格

    private boolean isAnswered;//是否为接电话
    private static final String TAG = "VideoCallNewActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            Log.w(TAG,"savedInstanceState = "+savedInstanceState);
            finish();
            return;
        }

        setContentView(R.layout.activity_video_call);

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (soundPool != null)
            soundPool.release();
        if (ringtone != null && ringtone.isPlaying())
            ringtone.stop();
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setMicrophoneMute(false);

        EMClient.getInstance().callManager().removeCallStateChangeListener(emListener);

        AppManager.getInstance().finishActivity(TeacherOrderDetailActivity.class);
        AppManager.getInstance().finishActivity(StudentOrderDetailNoFinishActivity.class);
        localSurface = null;
        oppositeSurface = null;
        Log.e("VideoCall", "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void initView() {
        // 在此接受上一级传过来的资料
        //是否为打进
        isComingCall = getIntent().getBooleanExtra("isComingCall", false);
        //给谁打
        to = getIntent().getStringExtra("to");
        //谁打进来的
        from = getIntent().getStringExtra("from");
        workId = getIntent().getStringExtra("workid");
        //在此调用接口  展示对方的姓名以及头像
        getOtherInfo();
        Log.w(TAG,"isComingCall = "+isComingCall+"\nto = "+to+
                "\nfrom = "+from+"\nworkId = "+workId);
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);


        setOnClickListeners(this, btnAnswerCall, btnRefuseCall, btnHangupCall, ivHandsfree, ivMute, mIvChangeCamera);

        emListener = new EMCallStateChangeListener() {
            @Override
            public void onCallStateChanged(CallState callState, CallError callError) {
                EventBus.getDefault().post(new VideoCallStateEvent(callState, callError));
            }
        };

        // local surfaceview
        localSurface = (EMLocalSurfaceView) findViewById(R.id.local_surface);
        localSurface.setZOrderMediaOverlay(true);
        localSurface.setZOrderOnTop(true);

        EMClient.getInstance().callManager().addCallStateChangeListener(emListener);



        if (isComingCall) {

            localSurface.setVisibility(View.INVISIBLE);
            Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            audioManager.setMode(AudioManager.MODE_RINGTONE);
            audioManager.setSpeakerphoneOn(true);
            ringtone = RingtoneManager.getRingtone(this, ringUri);
            ringtone.play();
            EMClient.getInstance().callManager().setSurfaceView(localSurface, oppositeSurface);

        } else {
            if (!TextUtils.isEmpty(to)) {
                makeVideoCall(to);
            }
            llComingCall.setVisibility(View.INVISIBLE);
            btnHangupCall.setVisibility(View.VISIBLE);
            soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
            outgoing = soundPool.load(this, R.raw.em_outgoing, 1);
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    streamID = playMakeCallSounds();
                }
            });
            EMClient.getInstance().callManager().setSurfaceView(localSurface, oppositeSurface);
        }

        if (AppConfig.userVo.type == 1) {
            btnHangupCall.setText("结束作业");
        }
    }

    /**
     * 拨打视频通话
     *
     * @param to
     * @throws EMServiceNotReadyException
     */
    private void makeVideoCall(String to) {
        try {
            EMClient.getInstance().callManager().makeVideoCall(to);
        } catch (EMServiceNotReadyException e) {
            e.printStackTrace();
        }
    }

    /**
     * 接听通话
     *
     * @throws EMNoActiveCallException
     */
    private void answerCall() {
        //TODO 在此需要申请权限   防止在6.0上发生奔溃
        //TODO 此处需要申请用户权限
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, Constants.CODE_201);
        } else {
            //已经拥有权限
            try {
                EMClient.getInstance().callManager().answerCall();
            } catch (EMNoActiveCallException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //已经获取拥有权限
            try {
                EMClient.getInstance().callManager().answerCall();
            } catch (EMNoActiveCallException e) {
                e.printStackTrace();
            }
        } else {
            ToastUtil.showToastDefault(mContext, "录音通话权限未授权---->应用----->权限管理");
        }
    }

    /**
     * 拒绝接听
     *
     * @throws EMNoActiveCallException
     */
    private void rejectCall() {
        try {
            EMClient.getInstance().callManager().rejectCall();
        } catch (EMNoActiveCallException e) {
            e.printStackTrace();
            Log.w(TAG,"rejectCall() = ");
            finish();
        }
    }

    /**
     * 挂断通话
     */
    private void endCall() {
        try {
            EMClient.getInstance().callManager().endCall();
        } catch (EMNoActiveCallException e) {
            e.printStackTrace();
            Log.w(TAG,"endCall() = ");
            finish();
        }
    }

    /**
     * 播放拨号响铃
     */
    private int playMakeCallSounds() {
        try {
            audioManager.setMode(AudioManager.MODE_RINGTONE);
            audioManager.setSpeakerphoneOn(false);
            // 播放
            int id = soundPool.play(outgoing, // 声音资源
                    0.3f, // 左声道
                    0.3f, // 右声道
                    1, // 优先级，0最低
                    -1, // 循环次数，0是不循环，-1是永远循环
                    1); // 回放速度，0.5-2.0之间。1为正常速度
            return id;
        } catch (Exception e) {
            return -1;
        }
    }

    // 打开扬声器
    protected void openSpeakerOn() {
        try {
            if (!audioManager.isSpeakerphoneOn())
                audioManager.setSpeakerphoneOn(true);
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 关闭扬声器
    protected void closeSpeakerOn() {

        try {
            if (audioManager != null) {
                // int curVolume =
                // audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
                if (audioManager.isSpeakerphoneOn())
                    audioManager.setSpeakerphoneOn(false);
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                // audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                // curVolume, AudioManager.STREAM_VOICE_CALL);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注意：在收到 DISCONNNECTED 回调时才能 finish 当前页面（保证通话所占用的资源都释放完），然后开始下一个通话。
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCallStateChanged(VideoCallStateEvent event) {
        Log.w(TAG,"stateChanged:state--->" + event.callState.toString() + ",error--->" + event.callError.toString());
        switch (event.callState) {
            case CONNECTING: // 正在连接对方
                tvCallState.setText("正在连接对方...");
                break;
            case CONNECTED: // 双方已经建立连接
                tvCallState.setText("已经和对方建立连接，等待对方接受...");
                break;
            case ACCEPTED: // 电话接通成功
                mIvChangeCamera.setVisibility(View.VISIBLE);
                llComingCall.setVisibility(View.INVISIBLE);
                btnHangupCall.setVisibility(View.VISIBLE);
                try {
                    if (soundPool != null)
                        soundPool.stop(streamID);
                } catch (Exception e) {
                }
                if (!isHandsfreeState)
                    closeSpeakerOn();


                chronometer.setVisibility(View.VISIBLE);
                chronometer.setBase(SystemClock.elapsedRealtime());
                // 开始记时
                chronometer.start();

                tvCallState.setText("通话中...");


                //开始作业
                if (!isAnswered) {
                    startWorkById();
                }

                break;
            case DISCONNNECTED: // 电话断了
                mIvChangeCamera.setVisibility(View.GONE);
                chronometer.stop();
                if (event.callError == CallError.REJECTED) {
                    tvCallState.setText("对方拒绝接受！");
                } else if (event.callError == CallError.ERROR_TRANSPORT) {
                    tvCallState.setText("连接建立失败！");
                }
//                else if (event.callError == CallError.ERROR_INAVAILABLE) {
//                    tvCallState.setText("对方不在线，请稍后再拨...");
//                }
                else if (event.callError == CallError.ERROR_BUSY) {
                    tvCallState.setText("对方正在通话中，请稍后再拨...");
                } else if (event.callError == CallError.ERROR_NORESPONSE) {
                    tvCallState.setText("对方未接听！");
                } else {
                    //结束作业   判断不是打进来的并且已经开始作业
                    if (!isComingCall && isStart) {
                        endWorkById();
                    }
                }

                rootLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.w(TAG,"onCallStateChanged()  DISCONNNECTED");
                        finish();
                    }
                }, 1500);

                break;
            case NETWORK_UNSTABLE: //网络不稳定
                if (event.callError == CallError.ERROR_NO_DATA) {
                    //无通话数据
                    tvCallState.setText("对方失去了连接！");
                    endCall();
                } else {
                }
                break;
            case NETWORK_NORMAL: //网络恢复正常
                break;
            default:
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_changeCamera://切换摄像头
                /*EMCallManager manager =*/ EMClient.getInstance().callManager().switchCamera();
                /*int i = manager.getCameraFacing();
                if (i == Camera.CameraInfo.CAMERA_FACING_BACK) {//后换前
                    try {
                        manager.setCameraFacing(Camera.CameraInfo.CAMERA_FACING_FRONT);
                        manager.switchCamera();
                    } catch (HyphenateException e) {
                        //e.printStackTrace();
                        return;
                    }
                } else if (i == Camera.CameraInfo.CAMERA_FACING_FRONT) {//前换后
                    try {
                        manager.setCameraFacing(Camera.CameraInfo.CAMERA_FACING_BACK);
                        manager.switchCamera();
                    } catch (HyphenateException e) {
                        //e.printStackTrace();
                        return;
                    }
                }*/
                break;
            case R.id.btn_answer_call:
                isAnswered = true;
                if (ringtone != null)
                    ringtone.stop();
                answerCall();
                localSurface.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_refuse_call:
                if (ringtone != null)
                    ringtone.stop();
                rejectCall();
                break;
            case R.id.btn_hangup_call://结束作业
                AlertDialog dialog = new AlertDialog.Builder(mContext)
                        .setMessage("确定结束授课？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                endCall();
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
                break;
            case R.id.iv_handsfree:
                if (isHandsfreeState) {
                    // 关闭免提
                    ivHandsfree.setImageResource(R.drawable.mianti1);
                    closeSpeakerOn();
                    isHandsfreeState = false;
                } else {
                    ivHandsfree.setImageResource(R.drawable.mianti2);
                    openSpeakerOn();
                    isHandsfreeState = true;
                }
                break;
            case R.id.iv_mute:
                if (isMuteState) {
                    // 关闭静音
                    ivMute.setImageResource(R.drawable.jingyin1);
                    audioManager.setMicrophoneMute(false);
                    isMuteState = false;
                } else {
                    // 打开静音
                    ivMute.setImageResource(R.drawable.jingyin2);
                    audioManager.setMicrophoneMute(true);
                    isMuteState = true;
                }
        }
    }

    class VideoCallStateEvent {
        public CallState callState;
        public CallError callError;

        public VideoCallStateEvent(CallState callState, CallError callError) {
            this.callState = callState;
            this.callError = callError;
        }
    }

    /**
     * 开始作业
     */
    private void startWorkById() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", AppConfig.token);
        params.put("workid", workId);

        EasyHttp.doPost(mContext, ServerURL.STARTWORKBYID, params, null, null, false, new EasyHttp.OnEasyHttpDoneListener() {
            @Override
            public void onEasyHttpSuccess(ResultBean resultBean) {
                isStart = true;
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                ToastUtil.showToastDefault(mContext, message);
            }
        });
    }


    /**
     * 结束作业
     */
    private void endWorkById() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", AppConfig.token);
        params.put("workid", workId);

        EasyHttp.doPost(mContext, ServerURL.ENDWORKBYID, params, null, OrderCostModel.class, false, new EasyHttp.OnEasyHttpDoneListener<OrderCostModel>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<OrderCostModel> resultBean) {
                isStart = false;
                orderCostModel = resultBean.data;
                endWorkShowAndCost();
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                ToastUtil.showToastDefault(mContext, message);
            }
        });
    }

    /**
     * 挂断电话后退出
     */
    private void endWorkShowAndCost() {
        int type = AppConfig.userVo.type;
        Intent i;
        if (type == 1) {
            i = new Intent(this, StudentOrderDetailFinishActivity.class);
        } else {
            i = new Intent(this, TeacherOrderEndDetailActivity.class);
        }

        i.putExtra("orderCostModel", orderCostModel);
        i.putExtra(Constants.PASS_ORDER, Long.parseLong(workId));
        startActivity(i);
    }

    /**
     * 获取对方的信息
     */
    private void getOtherInfo() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("imacc", isComingCall ? from : to);

        EasyHttp.doPost(mContext, ServerURL.GETMEMBERBYIM, params, null, AccMemberImModel.class, false, new EasyHttp.OnEasyHttpDoneListener<AccMemberImModel>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<AccMemberImModel> resultBean) {
                String name = resultBean.data.name;
                String photourl = resultBean.data.photourl;
                // 设置通话人
                tvVideoNick.setText(name);
                ImageLoader.getInstance().displayImage(photourl, swingVideoCard);

            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                ToastUtil.showToastDefault(mContext, message);
            }
        });

    }

    @Override
    public void onBackPressed() {
//        if (isAnswered) {
        ToastUtil.showToastDefault(mContext, "请先挂断电话再返回");
//        } else {
//            super.onBackPressed();
//        }
    }
}