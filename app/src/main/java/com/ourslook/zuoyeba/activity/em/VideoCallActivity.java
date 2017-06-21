/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ourslook.zuoyeba.activity.em;

import android.content.Intent;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.media.EMLocalSurfaceView;
import com.hyphenate.media.EMOppositeSurfaceView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ourslook.zuoyeba.AppConfig;
import com.ourslook.zuoyeba.Constants;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ServerURL;
import com.ourslook.zuoyeba.activity.StudentOrderDetailFinishActivity;
import com.ourslook.zuoyeba.activity.TeacherOrderEndDetailActivity;
import com.ourslook.zuoyeba.model.AccMemberModel;
import com.ourslook.zuoyeba.model.OrderCostModel;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VideoCallActivity extends CallActivity implements OnClickListener {

    private boolean isMuteState;
    private boolean isHandsfreeState;
    private boolean isAnswered;
    private boolean endCallTriggerByMe = false;
    private boolean monitor = true;

    private TextView callStateTextView;

    private LinearLayout comingBtnContainer;
    private Button refuseBtn;
    private Button answerBtn;
    private Button hangupBtn;
    private ImageView muteImage;
    private ImageView handsFreeImage;
    private TextView nickTextView;
    private Chronometer chronometer;
    private LinearLayout voiceContronlLayout;
    private RelativeLayout rootContainer;
    private RelativeLayout btnsContainer;
    private LinearLayout topContainer;
    private LinearLayout bottomContainer;
    private TextView monitorTextView;
    ImageView iv_head;//对方图像
    TextView mTvVideoNike;

    private Handler uiHandler;

    long workid;// 订单ID
    OrderCostModel orderCostModel;// 订单价格
    private boolean isEnd = false;// 是否结束

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            finish();
            return;
        }
//        setContentViewWithDefaultTitle(R.layout.activity_video_call, "视频授课");
        setContentView(R.layout.activity_video_call);
        workid = getIntent().getLongExtra("workid", 0);

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        uiHandler = new Handler();

        callStateTextView = (TextView) findViewById(R.id.tv_call_state);
        comingBtnContainer = (LinearLayout) findViewById(R.id.ll_coming_call);
        rootContainer = (RelativeLayout) findViewById(R.id.root_layout);
        refuseBtn = (Button) findViewById(R.id.btn_refuse_call);
        answerBtn = (Button) findViewById(R.id.btn_answer_call);
        hangupBtn = (Button) findViewById(R.id.btn_hangup_call);
        muteImage = (ImageView) findViewById(R.id.iv_mute);
        handsFreeImage = (ImageView) findViewById(R.id.iv_handsfree);
        callStateTextView = (TextView) findViewById(R.id.tv_call_state);
        nickTextView = (TextView) findViewById(R.id.tv_nick);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        voiceContronlLayout = (LinearLayout) findViewById(R.id.ll_voice_control);
        btnsContainer = (RelativeLayout) findViewById(R.id.ll_btns);
        topContainer = (LinearLayout) findViewById(R.id.ll_top_container);
        bottomContainer = (LinearLayout) findViewById(R.id.ll_bottom_container);
        monitorTextView = (TextView) findViewById(R.id.tv_call_monitor);

        iv_head = (ImageView) findViewById(R.id.swing_video_card);
        mTvVideoNike = (TextView) findViewById(R.id.tv_video_nick);

        refuseBtn.setOnClickListener(this);
        answerBtn.setOnClickListener(this);
        hangupBtn.setOnClickListener(this);
        muteImage.setOnClickListener(this);
        handsFreeImage.setOnClickListener(this);
        rootContainer.setOnClickListener(this);

        msgid = UUID.randomUUID().toString();
        // 获取通话是否为接收方向的
        isInComingCall = getIntent().getBooleanExtra("isComingCall", false);
        username = getIntent().getStringExtra("username");
        String nickname = getIntent().getStringExtra("nickname");
        // 设置通话人
        if (isInComingCall) {
//            nickTextView.setText(username);
        } else {
            nickTextView.setText(nickname);
        }
        if (AppConfig.userVo.type == 1) {
            hangupBtn.setText("结束作业");
        } else {
            hangupBtn.setText("结束授课");
        }

        getMemberByIm();

        // 显示本地图像的surfaceview
        localSurface = (EMLocalSurfaceView) findViewById(R.id.local_surface);
        localSurface.setZOrderMediaOverlay(true);
        localSurface.setZOrderOnTop(true);

        // 显示对方图像的surfaceview
        oppositeSurface = (EMOppositeSurfaceView) findViewById(R.id.opposite_surface);

        // 设置通话监听
        addCallStateListener();
        if (!isInComingCall) {// 拨打电话
            soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
            outgoing = soundPool.load(this, R.raw.em_outgoing, 1);

            comingBtnContainer.setVisibility(View.INVISIBLE);
            hangupBtn.setVisibility(View.VISIBLE);
            String st = "正在连接对方...";
            callStateTextView.setText(st);
            EMClient.getInstance().callManager().setSurfaceView(localSurface, oppositeSurface);
            handler.sendEmptyMessage(MSG_CALL_MAKE_VIDEO);
        } else { // 有电话进来
            voiceContronlLayout.setVisibility(View.INVISIBLE);
            localSurface.setVisibility(View.INVISIBLE);
            Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            audioManager.setMode(AudioManager.MODE_RINGTONE);
            audioManager.setSpeakerphoneOn(true);
            ringtone = RingtoneManager.getRingtone(this, ringUri);
            ringtone.play();
            EMClient.getInstance().callManager().setSurfaceView(localSurface, oppositeSurface);
        }
    }

    /**
     * 设置通话状态监听
     */
    void addCallStateListener() {
        callStateListener = new EMCallStateChangeListener() {

            @Override
            public void onCallStateChanged(CallState callState, CallError error) {
                // Message msg = handler.obtainMessage();
                switch (callState) {

                    case CONNECTING: // 正在连接对方
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                callStateTextView.setText("正在连接对方...");
                            }

                        });
                        break;
                    case CONNECTED: // 双方已经建立连接
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                callStateTextView.setText(R.string.have_connected_with);
                            }

                        });
                        break;

                    case ACCEPTED: // 电话接通成功
                        handler.removeCallbacks(timeoutHangup);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    if (soundPool != null)
                                        soundPool.stop(streamID);
                                } catch (Exception e) {
                                }
                                openSpeakerOn();
                                ((TextView) findViewById(R.id.tv_is_p2p)).setText(EMClient.getInstance().callManager().isDirectCall()
                                        ? R.string.direct_call : R.string.relay_call);
                                handsFreeImage.setImageResource(R.drawable.mianti2);
                                isHandsfreeState = true;
                                chronometer.setVisibility(View.VISIBLE);
                                chronometer.setBase(SystemClock.elapsedRealtime());
                                // 开始记时
                                chronometer.start();
                                nickTextView.setVisibility(View.INVISIBLE);
                                callStateTextView.setText(R.string.In_the_call);
                                callingState = CallingState.NORMAL;

                                if (!isInComingCall) {
                                    startWorkById();
                                }
                            }

                        });
                        break;
                    case DISCONNNECTED: // 电话断了
                        handler.removeCallbacks(timeoutHangup);
                        final CallError fError = error;
                        runOnUiThread(new Runnable() {
                            private void postDelayedCloseMsg() {
                                uiHandler.postDelayed(new Runnable() {

                                    @Override
                                    public void run() {

                                        finish();
                                    }

                                }, 1500);
                            }

                            @Override
                            public void run() {
                                chronometer.stop();
                                callDruationText = chronometer.getText().toString();
                                String s1 = getResources().getString(R.string.The_other_party_refused_to_accept);
                                String s2 = getResources().getString(R.string.Connection_failure);
                                String s3 = getResources().getString(R.string.The_other_party_is_not_online);
                                String s4 = getResources().getString(R.string.The_other_is_on_the_phone_please);
                                String s5 = getResources().getString(R.string.The_other_party_did_not_answer);

                                String s6 = getResources().getString(R.string.hang_up);
                                String s7 = getResources().getString(R.string.The_other_is_hang_up);
                                String s8 = getResources().getString(R.string.did_not_answer);
                                String s9 = getResources().getString(R.string.Has_been_cancelled);

                                if (fError == CallError.REJECTED) {
                                    callingState = CallingState.BEREFUESD;
                                    callStateTextView.setText(s1);
                                } else if (fError == CallError.ERROR_TRANSPORT) {
                                    callStateTextView.setText(s2);
                                }
//                                else if (fError == CallError.ERROR_INAVAILABLE) {
//                                    callingState = CallingState.OFFLINE;
//                                    callStateTextView.setText(s3);
//                                }
                                else if (fError == CallError.ERROR_BUSY) {
                                    callingState = CallingState.BUSY;
                                    callStateTextView.setText(s4);
                                } else if (fError == CallError.ERROR_NORESPONSE) {
                                    callingState = CallingState.NORESPONSE;
                                    callStateTextView.setText(s5);
                                } else {
                                    isEnd = true;
                                    if (!isInComingCall && isStart)
                                        endWorkById();
                                    if (isAnswered) {
                                        callingState = CallingState.NORMAL;
                                        if (endCallTriggerByMe) {
//                                        callStateTextView.setText(s6);
                                        } else {
                                            callStateTextView.setText(s7);
                                        }
                                    } else {
                                        if (isInComingCall) {
                                            callingState = CallingState.UNANSWERED;
                                            callStateTextView.setText(s8);
                                        } else {
                                            if (callingState != CallingState.NORMAL) {
                                                callingState = CallingState.CANCED;
                                                callStateTextView.setText(s9);
                                            } else {
                                                callStateTextView.setText(s6);
                                            }
                                        }
                                    }
                                }
                                postDelayedCloseMsg();
                            }

                        });

                        break;

                    default:
                        break;
                }

            }
        };
        EMClient.getInstance().callManager().addCallStateChangeListener(callStateListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_refuse_call: // 拒绝接听
                refuseBtn.setEnabled(false);
                handler.sendEmptyMessage(MSG_CALL_REJECT);
                break;

            case R.id.btn_answer_call: // 接听电话
                answerBtn.setEnabled(false);
                openSpeakerOn();
                if (ringtone != null)
                    ringtone.stop();

                callStateTextView.setText("正在接听...");
                handler.sendEmptyMessage(MSG_CALL_ANSWER);
                handsFreeImage.setImageResource(R.drawable.mianti2);
                isAnswered = true;
                isHandsfreeState = true;
                comingBtnContainer.setVisibility(View.INVISIBLE);
                hangupBtn.setVisibility(View.VISIBLE);
                voiceContronlLayout.setVisibility(View.VISIBLE);
//                localSurface.setVisibility(View.VISIBLE);

                break;

            case R.id.btn_hangup_call: // 挂断电话
                hangupBtn.setEnabled(false);
                chronometer.stop();
                endCallTriggerByMe = true;
                callStateTextView.setText(getResources().getString(R.string.hanging_up));
                handler.sendEmptyMessage(MSG_CALL_END);
                break;

            case R.id.iv_mute: // 静音开关
                if (isMuteState) {
                    // 关闭静音
                    muteImage.setImageResource(R.drawable.jingyin1);
                    audioManager.setMicrophoneMute(false);
                    isMuteState = false;
                } else {
                    // 打开静音
                    muteImage.setImageResource(R.drawable.jingyin2);
                    audioManager.setMicrophoneMute(true);
                    isMuteState = true;
                }
                break;
            case R.id.iv_handsfree: // 免提开关
                if (isHandsfreeState) {
                    // 关闭免提
                    //closeSpeakerOn();
                    CloseSpeaker();
                    isHandsfreeState = false;
                    handsFreeImage.setImageResource(R.drawable.mianti1);
                } else {
                    //openSpeakerOn();
                    OpenSpeaker();
                    isHandsfreeState = true;
                    handsFreeImage.setImageResource(R.drawable.mianti2);
                }
                break;
            case R.id.root_layout:
                if (callingState == CallingState.NORMAL) {
                    if (bottomContainer.getVisibility() == View.VISIBLE) {
                        bottomContainer.setVisibility(View.GONE);
                        topContainer.setVisibility(View.GONE);

                    } else {
                        bottomContainer.setVisibility(View.VISIBLE);
                        topContainer.setVisibility(View.VISIBLE);

                    }
                }

                break;
            default:
                break;
        }

    }

    @Override
    protected void onDestroy() {
        localSurface = null;
        oppositeSurface = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        callDruationText = chronometer.getText().toString();
        if (isAnswered) {

        } else {
            super.onBackPressed();
        }
    }


    boolean isStart = false;

    /**
     * 开始作业
     */
    private void startWorkById() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", AppConfig.token);
        params.put("workid", workid + "");

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

//        EasyHttp.postAsyn(Api.STARTWORKBYID, params,
//                new OkHttpClientManager.ResultCallback<Object>(
//                        this) {
//
//                    @Override
//                    public void onError(XaResult<Object> result,
//                                        Request request, Exception e) {
//                        // 这里使用父类的信息提示方法
//                        super.onError(result, request, e);
//
//                    }
//
//                    @Override
//                    public void onResponse(Object response) {
//
//                    }
//
//                    @Override
//                    public void onBefore(Request request) { }
//
//                }, Api.STARTWORKBYID);
    }

    /**
     * 挂断电话后退出
     */
    private void endWorkShowAndCost() {
        if (isEnd) {
            callDruationText = chronometer.getText().toString();
            int type = AppConfig.userVo.type;
            Intent i;
            if (type == 1) {
                i = new Intent(VideoCallActivity.this, StudentOrderDetailFinishActivity.class);
            } else {
                i = new Intent(VideoCallActivity.this, TeacherOrderEndDetailActivity.class);
            }
            i.putExtra("orderCostModel", orderCostModel);
            i.putExtra(Constants.PASS_ORDER, workid);
            startActivity(i);
//            finish();
        } else {
            ToastUtil.showToastDefault(VideoCallActivity.this, "通话还在进行中，请挂断后退出");
        }
    }

    /**
     * 结束作业
     */
    private void endWorkById() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", AppConfig.token);
        params.put("workid", workid + "");

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

//        OkHttpClientManager.postAsyn(Api.ENDWORKBYID, params,
//                new OkHttpClientManager.ResultCallback<OrderCostModel>(
//                        this) {
//
//                    @Override
//                    public void onError(XaResult<OrderCostModel> result,
//                                        Request request, Exception e) {
//                        // 这里使用父类的信息提示方法
//                        super.onError(result, request, e);
//                    }
//
//                    @Override
//                    public void onResponse(OrderCostModel response) {
//                        isStart = false;
//                        orderCostModel = response;
//                        endWorkShowAndCost();
//                    }
//
//                    @Override
//                    public void onBefore(Request request) {
//                    }
//
//                }, Api.ENDWORKBYID);
    }

    /**
     * 根据环信账号获取用户信息
     */
    private void getMemberByIm() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("imacc", username);

        EasyHttp.doPost(mContext, ServerURL.GETMEMBERBYIM, params, null, AccMemberModel.class, false, new EasyHttp.OnEasyHttpDoneListener<AccMemberModel>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<AccMemberModel> resultBean) {
                String name = resultBean.data.name;
                String photourl = resultBean.data.photourl;
                // 设置通话人
//                if (isInComingCall) {
//                    nickTextView.setText(name);
//
//                    mTvVideoNike.setText(name);
//                    ImageLoader.getInstance().displayImage(photourl, iv_head);
//                }
                mTvVideoNike.setText(name);
                ImageLoader.getInstance().displayImage(photourl, iv_head);
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                ToastUtil.showToastDefault(mContext, message);
            }
        });

//        OkHttpClientManager.postAsyn(Api.GETMEMBERBYIM, params,
//                new OkHttpClientManager.ResultCallback<AccMemberImModel>(
//                        this) {
//
//                    @Override
//                    public void onError(XaResult<AccMemberImModel> result,
//                                        Request request, Exception e) {
//                        // 这里使用父类的信息提示方法
//                        super.onError(result, request, e);
//                    }
//
//                    @Override
//                    public void onResponse(AccMemberImModel response) {
//                        String name = response.getName();
//                        // 设置通话人
//                        if (isInComingCall) {
//                            nickTextView.setText(name);
//                        }
//                    }
//
//                    @Override
//                    public void onBefore(Request request) {
//                    }
//                }, Api.GETMEMBERBYIM);
    }
}
