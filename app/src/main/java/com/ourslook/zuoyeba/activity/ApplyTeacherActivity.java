package com.ourslook.zuoyeba.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ourslook.zuoyeba.BaseActivity;
import com.ourslook.zuoyeba.Constants;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ServerURL;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.PreferencesManager;
import com.ourslook.zuoyeba.utils.StringUtils;
import com.ourslook.zuoyeba.utils.ToastUtil;
import com.ourslook.zuoyeba.utils.VerificationUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

/**
 * 申请老师（提交资料）
 * Created by zhaotianlong on 2016/5/19.
 */
public class ApplyTeacherActivity extends BaseActivity implements View.OnClickListener{

    @Bind(R.id.iv_upload_back)
    ImageView back;
    @Bind(R.id.et_upload_name)
    EditText et_name;
    @Bind(R.id.et_upload_number)
    EditText et_phoneNumber;
    @Bind(R.id.tv_upload_title)
    TextView title;
    @Bind(R.id.tv_upload_button)
    TextView uploadBtn;
    @Bind(R.id.tv_phoneNumber)
    TextView helpNumber;
    @Bind(R.id.ll_helpNumber)
    LinearLayout ll_help;


    private Intent intent;
    private String stel;
    private String pNum;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_teacher);
        getServiceTel();
    }

    @Override
    protected void initView() {
        title.setText("提交资料");
        back.setOnClickListener(this);
        uploadBtn.setOnClickListener(this);
        helpNumber.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_upload_back:
                finish();
                break;
            case R.id.tv_phoneNumber:
                showCallDialog();
                break;
            case R.id.tv_upload_button:
                if (checked()){
                    applyTeacher();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 获取客服电话
     */
    private void getServiceTel() {

        EasyHttp.doPost(mContext, ServerURL.GETSERVICETEL, null, null, Object.class, false, new EasyHttp.OnEasyHttpDoneListener<Object>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<Object> resultBean) {
                String s = resultBean.data.toString();
                if (StringUtils.isNotEmpty(s)){
                    ll_help.setVisibility(View.VISIBLE);
                    helpNumber.setText(s);
                    stel = s;
                }

            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                ToastUtil.showToastDefault(mContext, message);
            }
        });
    }

    /**
     * 老师申请
     */
    private void applyTeacher() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile", pNum);// 电话
        params.put("name", name);//
        EasyHttp.doPost(mContext, ServerURL.APPLYTEACHER, params, null, Object.class, false, new EasyHttp.OnEasyHttpDoneListener<Object>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<Object> resultBean) {
                intent = new Intent(ApplyTeacherActivity.this,SendInfoActivity.class);
                PreferencesManager.getInstance(ApplyTeacherActivity.this).put(Constants.IS_TEACHER,true);
                startActivity(intent);
                finish();

            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                ToastUtil.showToastDefault(mContext, message);
            }
        });
    }

    /**
     * 拨打客服电话
     */
    private void showCallDialog(){
        if (StringUtils.isEmpty(stel)) {
            ToastUtil.showToastDefault(this, "获取客服电话失败");
            helpNumber.setClickable(false);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("是否拨打客服电话");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 拨打电话
                    intent = new Intent(Intent.ACTION_CALL);
                    Uri data = Uri.parse("tel:" + stel);
                    intent.setData(data);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("取消", null);
            builder.show();
        }
    }

    /**
     * 检查输入
     */
    private boolean checked(){
        pNum = et_phoneNumber.getText().toString().trim();
        name = et_name.getText().toString().trim();

        if (StringUtils.isEmpty(name)){
            ToastUtil.showToastDefault(this,"姓名不能为空");
            return false;
        }
        if (StringUtils.isEmpty(pNum)){
            ToastUtil.showToastDefault(this, "手机号不能为空");
            return false;
        }
        if (!VerificationUtil.checkMobile1(pNum)) {
            ToastUtil.showToastDefault(this, "手机号格式不正确");
            return false;
        }

        return true;
    }
}
