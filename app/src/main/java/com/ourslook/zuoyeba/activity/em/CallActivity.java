package com.ourslook.zuoyeba.activity.em;

import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.widget.Toast;

import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.EMNoActiveCallException;
import com.hyphenate.exceptions.EMServiceNotReadyException;
import com.hyphenate.media.EMLocalSurfaceView;
import com.hyphenate.media.EMOppositeSurfaceView;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.NetUtils;
import com.ourslook.zuoyeba.BaseActivity;
import com.ourslook.zuoyeba.utils.ToastUtil;

public class CallActivity extends BaseActivity {
    protected final int MSG_CALL_MAKE_VIDEO = 0;
    protected final int MSG_CALL_MAKE_VOICE = 1;
    protected final int MSG_CALL_ANSWER = 2;
    protected final int MSG_CALL_REJECT = 3;
    protected final int MSG_CALL_END = 4;
    protected final int MSG_CALL_RLEASE_HANDLER = 5;
    private static int currVolume = 0;
    protected boolean isInComingCall;
    protected String username;
    protected CallingState callingState = CallingState.CANCED;
    protected String callDruationText;
    protected String msgid;
    protected AudioManager audioManager;
    protected SoundPool soundPool;
    protected Ringtone ringtone;
    protected int outgoing;
    protected EMCallStateChangeListener callStateListener;
    protected EMLocalSurfaceView localSurface;
    protected EMOppositeSurfaceView oppositeSurface;
    protected boolean isAnswered = false;
    protected int streamID = -1;

    /**
     * 0：音频，1：视频
     */
    protected int callType = 0;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
//    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onDestroy() {
        if (soundPool != null)
            soundPool.release();
        if (ringtone != null && ringtone.isPlaying())
            ringtone.stop();
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setMicrophoneMute(false);

        if (callStateListener != null)
            EMClient.getInstance().callManager().removeCallStateChangeListener(callStateListener);
        releaseHandler();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        handler.sendEmptyMessage(MSG_CALL_END);
        finish();
        super.onBackPressed();
    }

    @Override
    protected void initView() {

    }

    Runnable timeoutHangup = new Runnable() {

        @Override
        public void run() {
            handler.sendEmptyMessage(MSG_CALL_END);
        }
    };

    HandlerThread callHandlerThread = new HandlerThread("callHandlerThread");

    {
        callHandlerThread.start();
    }

    protected Handler handler = new Handler(callHandlerThread.getLooper()) {
        @Override
        public void handleMessage(Message msg) {
            EMLog.d("EMCallManager CallActivity", "handleMessage ---enter--- msg.what:" + msg.what);
            switch (msg.what) {
                case MSG_CALL_MAKE_VIDEO:
                case MSG_CALL_MAKE_VOICE:
                    try {
                        streamID = playMakeCallSounds();
                        if (msg.what == MSG_CALL_MAKE_VIDEO) {
                            EMClient.getInstance().callManager().makeVideoCall(username);
                        } else {
                            EMClient.getInstance().callManager().makeVoiceCall(username);
                        }

                        final int MAKE_CALL_TIMEOUT = 50 * 1000;
                        handler.removeCallbacks(timeoutHangup);
                        handler.postDelayed(timeoutHangup, MAKE_CALL_TIMEOUT);
                    } catch (EMServiceNotReadyException e) {
                        e.printStackTrace();
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            final String st2 = getResources().getString(R.string.Is_not_yet_connected_to_the_server);
//                            Toast.makeText(CallActivity.this, st2, Toast.LENGTH_SHORT).show();
//                        }
//                    });
                        ToastUtil.showToastDefault(CallActivity.this, "接通异常，请您重新登录后重试！");
                    }
                    break;
                case MSG_CALL_ANSWER:
                    if (ringtone != null)
                        ringtone.stop();
                    if (isInComingCall) {
                        try {
                            if (NetUtils.hasDataConnection(CallActivity.this)) {
                                EMClient.getInstance().callManager().answerCall();
                                isAnswered = true;
                            } else {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        ToastUtil.showToastDefault(mContext,"尚未连接至服务器");
                                        CallActivity.this.finish();
                                    }
                                });
                                throw new Exception();
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            finish();
                            return;
                        }
                    }
                    break;
                case MSG_CALL_REJECT:
                    if (ringtone != null)
                        ringtone.stop();
                    try {
                        EMClient.getInstance().callManager().rejectCall();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        finish();
                    }
                    callingState = CallingState.REFUESD;
                    break;
                case MSG_CALL_END:
                    if (soundPool != null)
                        soundPool.stop(streamID);
                    try {
                        EMClient.getInstance().callManager().endCall();
                    } catch (Exception e) {
                        e.printStackTrace();
                        finish();
                    }
                    break;
                case MSG_CALL_RLEASE_HANDLER:
                    try {
                        EMClient.getInstance().callManager().endCall();
                    } catch (EMNoActiveCallException e) {
                        e.printStackTrace();
                    }
                    handler.removeCallbacks(timeoutHangup);
                    handler.removeMessages(MSG_CALL_MAKE_VIDEO);
                    handler.removeMessages(MSG_CALL_MAKE_VOICE);
                    handler.removeMessages(MSG_CALL_ANSWER);
                    handler.removeMessages(MSG_CALL_REJECT);
                    handler.removeMessages(MSG_CALL_END);
                    callHandlerThread.quit();
                    break;
                default:
                    break;
            }
            EMLog.d("EMCallManager CallActivity", "handleMessage ---exit--- msg.what:" + msg.what);
        }
    };

    void releaseHandler() {
        handler.sendEmptyMessage(MSG_CALL_RLEASE_HANDLER);
    }

    /**
     * 播放拨号响铃
     */
    protected int playMakeCallSounds() {
        try {
            // 最大音量
            float audioMaxVolumn = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
            // 当前音量
            float audioCurrentVolumn = audioManager.getStreamVolume(AudioManager.STREAM_RING);
            float volumnRatio = audioCurrentVolumn / audioMaxVolumn;

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

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.connect();
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Call Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://com.ourslook.ZuoYeBao.Activity/http/host/path")
//        );
//        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Call Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://com.ourslook.ZuoYeBao.Activity/http/host/path")
//        );
//        AppIndex.AppIndexApi.end(client, viewAction);
//        client.disconnect();
    }


    enum CallingState {
        CANCED, NORMAL, REFUESD, BEREFUESD, UNANSWERED, OFFLINE, NORESPONSE, BUSY
    }

    public void OpenSpeaker() {
        try {

            audioManager.setMode(AudioManager.ROUTE_SPEAKER);
            //获取当前通话音量
            currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            if (!audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(true);
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                        AudioManager.STREAM_VOICE_CALL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //关闭扬声器
    public void CloseSpeaker() {
        try {
            if (audioManager != null) {
                if (audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currVolume,
                            AudioManager.STREAM_VOICE_CALL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Toast.makeText(context,扬声器已经关闭",Toast.LENGTH_SHORT).show();
    }
}
