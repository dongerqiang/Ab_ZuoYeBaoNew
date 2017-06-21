/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p>
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

import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.EMLog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ourslook.zuoyeba.AppConfig;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ServerURL;
import com.ourslook.zuoyeba.model.AccMemberImModel;
import com.ourslook.zuoyeba.model.OrderCostModel;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 语音通话页面
 */
public class VoiceCallActivity extends CallActivity implements OnClickListener {
    private LinearLayout comingBtnContainer;
    private Button hangupBtn;
    private Button refuseBtn;
    private Button answerBtn;
    private ImageView muteImage;
    private ImageView handsFreeImage;

    private boolean isMuteState;
    private boolean isHandsfreeState;

    private TextView callStateTextView;
    private int streamID;
    private boolean endCallTriggerByMe = false;
    private TextView nickTextView;
    private ImageView iv_head;
    private TextView durationTextView;
    private Chronometer chronometer;
    String st1;
    private boolean isAnswered;
    private LinearLayout voiceContronlLayout;

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
//        setContentViewWithDefaultTitle(R.layout.activity_voice_call, "语音授课");
        setContentView(R.layout.activity_voice_call);

        workid = getIntent().getLongExtra("workid", 0);

        comingBtnContainer = (LinearLayout) findViewById(R.id.ll_coming_call);
        refuseBtn = (Button) findViewById(R.id.btn_refuse_call);
        answerBtn = (Button) findViewById(R.id.btn_answer_call);
        hangupBtn = (Button) findViewById(R.id.btn_hangup_call);
        muteImage = (ImageView) findViewById(R.id.iv_mute);
        handsFreeImage = (ImageView) findViewById(R.id.iv_handsfree);
        callStateTextView = (TextView) findViewById(R.id.tv_call_state);
        nickTextView = (TextView) findViewById(R.id.tv_nick);
        iv_head = (ImageView) findViewById(R.id.swing_card);
        durationTextView = (TextView) findViewById(R.id.tv_calling_duration);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        voiceContronlLayout = (LinearLayout) findViewById(R.id.ll_voice_control);

        refuseBtn.setOnClickListener(this);
        answerBtn.setOnClickListener(this);
        hangupBtn.setOnClickListener(this);
        muteImage.setOnClickListener(this);
        handsFreeImage.setOnClickListener(this);

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        uiHandler = new Handler();

        // 注册语音电话的状态的监听
        addCallStateListener();
        msgid = UUID.randomUUID().toString();

        username = getIntent().getStringExtra("username");
        // 语音电话是否为接收的
        isInComingCall = getIntent().getBooleanExtra("isComingCall", false);
        String nickname = getIntent().getStringExtra("nickname");
        String photourl = getIntent().getStringExtra("photourl");
//        // 设置通话人
        if (isInComingCall) {
//            nickTextView.setText(username);

        } else {
            nickTextView.setText(nickname);
            ImageLoader.getInstance().displayImage(photourl, iv_head);
        }
        if (AppConfig.userVo.type == 1) {
            hangupBtn.setText("结束作业");
        } else {
            hangupBtn.setText("结束授课");
        }

        getMemberByIm();

        if (!isInComingCall) {// 拨打电话
            soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
            outgoing = soundPool.load(this, R.raw.em_outgoing, 1);

            comingBtnContainer.setVisibility(View.INVISIBLE);
            hangupBtn.setVisibility(View.VISIBLE);
            st1 = getResources().getString(R.string.Are_connected_to_each_other);
            callStateTextView.setText(st1);
            handler.sendEmptyMessage(MSG_CALL_MAKE_VOICE);
        } else { // 有电话进来
            voiceContronlLayout.setVisibility(View.INVISIBLE);
            Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            audioManager.setMode(AudioManager.MODE_RINGTONE);
            audioManager.setSpeakerphoneOn(true);
            ringtone = RingtoneManager.getRingtone(this, ringUri);
            ringtone.play();
        }
    }

    /**
     * 设置电话监听
     */
    void addCallStateListener() {
        callStateListener = new EMCallStateChangeListener() {

            @Override
            public void onCallStateChanged(CallState callState, CallError error) {
                // Message msg = handler.obtainMessage();
                EMLog.d("EMCallManager", "onCallStateChanged:" + callState);
                switch (callState) {

                    case CONNECTING: // 正在连接对方
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                callStateTextView.setText(st1);
                            }
                        });
                        break;
                    case CONNECTED: // 双方已经建立连接
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                String st3 = getResources().getString(R.string.have_connected_with);
                                callStateTextView.setText(st3);
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
                                if (!isHandsfreeState)
                                    closeSpeakerOn();
                                //显示是否为直连，方便测试
                                ((TextView) findViewById(R.id.tv_is_p2p)).setText(EMClient.getInstance().callManager().isDirectCall()
                                        ? R.string.direct_call : R.string.relay_call);
                                chronometer.setVisibility(View.VISIBLE);
                                chronometer.setBase(SystemClock.elapsedRealtime());
                                // 开始记时
                                chronometer.start();
                                String str4 = getResources().getString(R.string.In_the_call);
                                callStateTextView.setText(str4);
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
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Log.d("AAA", "CALL DISCONNETED");

                                                finish();
                                            }
                                        });
                                    }
                                }, 1500);
                            }

                            @Override
                            public void run() {
                                chronometer.stop();
                                callDruationText = chronometer.getText().toString();
                                String st2 = getResources().getString(R.string.The_other_party_refused_to_accept);
                                String st3 = getResources().getString(R.string.Connection_failure);
                                String st4 = getResources().getString(R.string.The_other_party_is_not_online);
                                String st5 = getResources().getString(R.string.The_other_is_on_the_phone_please);

                                String st6 = getResources().getString(R.string.The_other_party_did_not_answer_new);
                                String st7 = getResources().getString(R.string.hang_up);
                                String st8 = getResources().getString(R.string.The_other_is_hang_up);

                                String st9 = getResources().getString(R.string.did_not_answer);
                                String st10 = getResources().getString(R.string.Has_been_cancelled);
                                String st11 = getResources().getString(R.string.hang_up);

                                if (fError == CallError.REJECTED) {
                                    callingState = CallingState.BEREFUESD;
                                    callStateTextView.setText(st2);
                                } else if (fError == CallError.ERROR_TRANSPORT) {
                                    callStateTextView.setText(st3);
                                }
//                                else if (fError == CallError.ERROR_INAVAILABLE) {
//                                    callingState = CallingState.OFFLINE;
//                                    callStateTextView.setText(st4);
//                                }
                                else if (fError == CallError.ERROR_BUSY) {
                                    callingState = CallingState.BUSY;
                                    callStateTextView.setText(st5);
                                } else if (fError == CallError.ERROR_NORESPONSE) {
                                    callingState = CallingState.NORESPONSE;
                                    callStateTextView.setText(st6);
                                } else {
                                    isEnd = true;
                                    if (!isInComingCall && isStart)
                                        endWorkById();
                                    if (isAnswered) {
                                        callingState = CallingState.NORMAL;
                                        if (endCallTriggerByMe) {
//                                        callStateTextView.setText(st7);
                                        } else {
                                            callStateTextView.setText(st8);
                                        }
                                    } else {
                                        if (isInComingCall) {
                                            callingState = CallingState.UNANSWERED;
                                            callStateTextView.setText(st9);
                                        } else {
                                            if (callingState != CallingState.NORMAL) {
                                                callingState = CallingState.CANCED;
                                                callStateTextView.setText(st10);
                                            } else {
                                                callStateTextView.setText(st11);
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

//    /**
//     * 设置电话监听
//     */
//    void addCallStateListener() {
//        callStateListener = new EMCallStateChangeListener() {
//
//            @Override
//            public void onCallStateChanged(CallState callState, CallError error) {
//                // Message msg = handler.obtainMessage();
//                switch (callState) {
//                    case CONNECTING: // 正在连接对方
//                        runOnUiThread(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                // TODO Auto-generated method stub
//                                callStateTextView.setText(st1);
//                            }
//
//                        });
//                        break;
//                    case CONNECTED: // 双方已经建立连接
//                        runOnUiThread(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                // TODO Auto-generated method stub
//                                String st3 = getResources().getString(R.string.have_connected_with);
//                                callStateTextView.setText(st3);
//                            }
//
//                        });
//                        break;
//
//                    case ACCEPTED: // 电话接通成功
//                        runOnUiThread(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                try {
//                                    if (soundPool != null)
//                                        soundPool.stop(streamID);
//                                } catch (Exception e) {
//                                }
//                                if (!isHandsfreeState)
//                                    closeSpeakerOn();
//                                //显示是否为直连，方便测试
//                                ((TextView) findViewById(R.id.tv_is_p2p)).setText(EMChatManager.getInstance().isDirectCall()
//                                        ? R.string.direct_call : R.string.relay_call);
//                                chronometer.setVisibility(View.VISIBLE);
//                                chronometer.setBase(SystemClock.elapsedRealtime());
//                                // 开始记时
//                                chronometer.start();
//                                String str4 = getResources().getString(R.string.In_the_call);
//                                callStateTextView.setText(str4);
//                                callingState = CallingState.NORMAL;
//
//                                if (!isInComingCall){
//                                    startWorkById();
//                                }
//                            }
//
//                        });
//                        break;
//                    case DISCONNNECTED: // 电话断了
//                        final CallError fError = error;
//                        runOnUiThread(new Runnable() {
//                            private void postDelayedCloseMsg() {
//                                handler.postDelayed(new Runnable() {
//
//                                    @Override
//                                    public void run() {
//                                        Animation animation = new AlphaAnimation(1.0f, 0.0f);
//                                        animation.setDuration(1500);
//                                        findViewById(R.id.root_layout).startAnimation(animation);
//                                        finish();
//                                    }
//
//                                }, 200);
//                            }
//
//                            @Override
//                            public void run() {
//                                chronometer.stop();
//                                callDruationText = chronometer.getText().toString();
//                                String st2 = getResources().getString(R.string.The_other_party_refused_to_accept);
//                                String st3 = getResources().getString(R.string.Connection_failure);
//                                String st4 = getResources().getString(R.string.The_other_party_is_not_online);
//                                String st5 = getResources().getString(R.string.The_other_is_on_the_phone_please);
//
//                                String st6 = getResources().getString(R.string.The_other_party_did_not_answer_new);
//                                String st7 = getResources().getString(R.string.hang_up);
//                                String st8 = getResources().getString(R.string.The_other_is_hang_up);
//
//                                String st9 = getResources().getString(R.string.did_not_answer);
//                                String st10 = getResources().getString(R.string.Has_been_cancelled);
//                                String st11 = getResources().getString(R.string.hang_up);
//
//                                if (fError == CallError.REJECTED) {
//                                    callingState = CallingState.BEREFUESD;
//                                    callStateTextView.setText(st2);
//                                } else if (fError == CallError.ERROR_TRANSPORT) {
//                                    callStateTextView.setText(st3);
//                                } else if (fError == CallError.ERROR_INAVAILABLE) {
//                                    callingState = CallingState.OFFLINE;
//                                    callStateTextView.setText(st4);
//                                } else if (fError == CallError.ERROR_BUSY) {
//                                    callingState = CallingState.BUSY;
//                                    callStateTextView.setText(st5);
//                                } else if (fError == CallError.ERROR_NORESPONSE) {
//                                    callingState = CallingState.NORESPONSE;
//                                    callStateTextView.setText(st6);
//                                } else {
//                                    isEnd = true;
//                                    if (!isInComingCall&&isStart)
//                                    endWorkById();
//                                    if (isAnswered) {
//                                        callingState = CallingState.NORMAL;
//                                        if (endCallTriggerByMe) {
//                                            callStateTextView.setText(st7);
//                                        } else {
//                                            callStateTextView.setText(st8);
//                                        }
//
//                                    } else {
//                                        if (isInComingCall) {
//                                            callingState = CallingState.UNANSWERED;
//                                            callStateTextView.setText(st9);
//                                        } else {
//                                            if (callingState != CallingState.NORMAL) {
//                                                callingState = CallingState.CANCED;
//                                                callStateTextView.setText(st10);
//                                            } else {
//                                                callStateTextView.setText(st11);
//                                            }
//                                        }
//                                    }
//                                }
//                                postDelayedCloseMsg();
//                            }
//
//                        });
//
//                        break;
//
//                    default:
//                        break;
//                }
//
//            }
//        };
//        EMChatManager.getInstance().addCallStateChangeListener(callStateListener);
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_refuse_call: // 拒绝接听
                refuseBtn.setEnabled(false);
                handler.sendEmptyMessage(MSG_CALL_REJECT);
                break;

            case R.id.btn_answer_call: // 接听电话
                isAnswered = true;
                answerBtn.setEnabled(false);
                closeSpeakerOn();
                callStateTextView.setText("正在接听...");
                comingBtnContainer.setVisibility(View.INVISIBLE);
                hangupBtn.setVisibility(View.VISIBLE);
                voiceContronlLayout.setVisibility(View.VISIBLE);
                handler.sendEmptyMessage(MSG_CALL_ANSWER);
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
                    handsFreeImage.setImageResource(R.drawable.mianti1);
                    closeSpeakerOn();
                    isHandsfreeState = false;
                } else {
                    handsFreeImage.setImageResource(R.drawable.mianti2);
                    openSpeakerOn();
                    isHandsfreeState = true;
                }
                break;
            default:
                break;
        }
    }

    /**
     * 挂断电话后退出
     */
    private void endWorkShowAndCost() {
        if (isEnd) {
            callDruationText = chronometer.getText().toString();
//            int type = AppConfig.userVo.type;
//            Intent i;
//            if (type == 1) {
//                i = new Intent(VoiceCallActivity.this, ListDaty_S_End.class);
//            } else {
//                i = new Intent(VoiceCallActivity.this, ListDaty_T_End.class);
//            }
//            i.putExtra("orderCostModel", orderCostModel);
//            i.putExtra("workid", workid);
//            startActivity(i);
            //finish();
        } else {
            ToastUtil.showToastDefault(VoiceCallActivity.this, "通话还在进行中，请挂断后退出");
        }
    }


    @Override
    public void onBackPressed() {
        callDruationText = chronometer.getText().toString();
        if (isAnswered) {

        } else {
            super.onBackPressed();
        }
    }

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

//        OkHttpClientManager.postAsyn(Api.STARTWORKBYID, params,
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
//                        isStart = true;
//                    }
//
//                    @Override
//                    public void onBefore(Request request) {
//                    }
//                }, Api.STARTWORKBYID);
    }

    boolean isStart = false;

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
//                    @Override
//                    public void onAfter() {
//                    }
//                }, Api.ENDWORKBYID);
    }

    /**
     * 根据环信账号获取用户信息
     */
    private void getMemberByIm() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("imacc", username);

        EasyHttp.doPost(mContext, ServerURL.GETMEMBERBYIM, params, null, AccMemberImModel.class, false, new EasyHttp.OnEasyHttpDoneListener<AccMemberImModel>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<AccMemberImModel> resultBean) {
                String name = resultBean.data.name;
                String photourl = resultBean.data.photourl;
                // 设置通话人
                if (isInComingCall) {
                    nickTextView.setText(name);
                    ImageLoader.getInstance().displayImage(photourl, iv_head);
                }
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {

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
//                        String photourl = response.getPhotourl();
//                        // 设置通话人
//                        if (isInComingCall) {
//                            nickTextView.setText(name);
//                            ImageLoader.getInstance().displayImage(photourl, iv_head);
//                        }
//                    }
//
//                    @Override
//                    public void onBefore(Request request) {
//                    }
//                }, Api.GETMEMBERBYIM);
    }

}
