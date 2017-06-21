package com.ourslook.zuoyeba.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.ourslook.zuoyeba.AppManager;
import com.ourslook.zuoyeba.BaseActivity;
import com.ourslook.zuoyeba.R;

/**
 * 老师上传资料
 * Created by zhaotianlong on 2016/5/19.
 */
public class SendInfoActivity extends BaseActivity implements View.OnClickListener{
    private TextView tv_return;//返回首页
    private TextView tv_leftTime;//返回首页
    int leftTime = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithDefaultTitle(R.layout.acivity_send_info,"资料提交成功");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler.hasMessages(1))
            handler.removeMessages(1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.sendEmptyMessageDelayed(1,1000);
    }
    @Override
    protected void initView() {
        tv_leftTime = (TextView) findViewById(R.id.tv_sendinfoAty_leftTime);
        tv_return = (TextView) findViewById(R.id.tv_sendinfoAty_return);
        tv_return.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_sendinfoAty_return:
                if (handler.hasMessages(1)) {
                    handler.removeMessages(1);
                }
                startActivity(new Intent(SendInfoActivity.this,HomeActivity.class));
                finish();
                break;
            default:
                break;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    leftTime--;
                    if (leftTime<0){
//                        finish();
                        AppManager.getInstance().finishActivity(ChoiceWorkActivity.class);
                        startActivity(new Intent(SendInfoActivity.this,HomeActivity.class));
                        finish();
                    }else{
                        handler.sendEmptyMessageDelayed(1,1000);
                        tv_leftTime.setText(leftTime+"s");
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
