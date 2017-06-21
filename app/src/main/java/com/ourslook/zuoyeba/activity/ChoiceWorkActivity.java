package com.ourslook.zuoyeba.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.ourslook.zuoyeba.BaseActivity;
import com.ourslook.zuoyeba.Constants;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.activity.login.LoginActivity;
import com.ourslook.zuoyeba.utils.PreferencesManager;

import butterknife.Bind;

public class ChoiceWorkActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.student)
    ImageView mStudent;
    @Bind(R.id.teacher)
    ImageView mTeacher;
    @Bind(R.id.btn_isHaveAccount)
    Button hasAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_work);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Translucent navigation bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    @Override
    protected void initView() {
        mStudent.setOnClickListener(this);
        mTeacher.setOnClickListener(this);
        hasAccount.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.student:
                PreferencesManager.getInstance(this).put(Constants.IS_STUDENT,true);
                Log.i("deq","i am student :"+PreferencesManager.getInstance(this).get(Constants.IS_STUDENT,false));
                inHomeActivity();
                break;
            case R.id.teacher:
//              PreferencesManager.getInstance(this).put(Constants.IS_TEACHER,true);
                Log.i("deq","i am teacher :"+PreferencesManager.getInstance(this).get(Constants.IS_TEACHER,false));
                inTeacher();
                break;
            case R.id.btn_isHaveAccount:
                initLogin();
                break;
            default:
                break;
        }
    }

    private void initLogin() {
        Intent intent = new Intent(ChoiceWorkActivity.this, LoginActivity.class);
        intent.putExtra("choice_login",22);
        startActivity(intent);
    }

    private void inHomeActivity() {
        Intent intent = new Intent();
        intent.setClass(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void inTeacher() {
        Intent intent = new Intent();
        intent.setClass(mContext, ApplyTeacherActivity.class);
        startActivity(intent);
    }
}
