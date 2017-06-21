package com.ourslook.zuoyeba.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.ourslook.zuoyeba.AppConfig;
import com.ourslook.zuoyeba.BaseActivity;
import com.ourslook.zuoyeba.Constants;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.ServerURL;
import com.ourslook.zuoyeba.ZuoYeBaApplication;
import com.ourslook.zuoyeba.model.AccMemberModel;
import com.ourslook.zuoyeba.model.CommonAreaModel;
import com.ourslook.zuoyeba.model.CommonUploadModel;
import com.ourslook.zuoyeba.model.GradeModel;
import com.ourslook.zuoyeba.net.EasyHttp;
import com.ourslook.zuoyeba.net.ResultBean;
import com.ourslook.zuoyeba.utils.DisplayUtils;
import com.ourslook.zuoyeba.utils.FileUtils;
import com.ourslook.zuoyeba.utils.ToastUtil;
import com.ourslook.zuoyeba.view.dialog.ChooseGradleDialog;
import com.ourslook.zuoyeba.view.dialog.ChooseLocalDialog;
import com.ourslook.zuoyeba.view.dialog.LoadingDialog;
import com.ourslook.zuoyeba.view.dialog.PickDialogPic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;


/**
 * Created by huangyi on 16/5/16.
 * 详细信息界面
 */
public class DetailInfoActivity extends BaseActivity implements View.OnClickListener, ChooseGradleDialog.onChooseDialogListern, ChooseLocalDialog.OnChooseLocalListener {
    @Bind(R.id.iv_detailInfoAty_headBg)
    ImageView ivDetailInfoAtyHeadBg; //头像
    @Bind(R.id.iv_detailInfoAty_head)
    ImageView ivDetailInfoAtyHead;
    @Bind(R.id.ll_detailInfoAty_head)
    LinearLayout llDetailInfoAtyHead;//点击头像
    @Bind(R.id.tv_detailInfoAty_name)
    TextView tvDetailInfoAtyName;//姓名
    @Bind(R.id.ll_detailInfoAty_name)
    LinearLayout llDetailInfoAtyName;//点击姓名
    @Bind(R.id.tv_detailInfoAty_sex)
    TextView tvDetailInfoAtySex;//性别
    @Bind(R.id.ll_detailInfoAty_sex)
    LinearLayout llDetailInfoAtySex;//点击性别
    @Bind(R.id.tv_detailInfoAty_grade)
    TextView tvDetailInfoAtyGrade;//年级
    @Bind(R.id.ll_detailInfoAty_grade)
    LinearLayout llDetailInfoAtyGrade;//点击年级
    @Bind(R.id.tv_detailInfoAty_area)
    TextView tvDetailInfoAtyArea;//地区
    @Bind(R.id.ll_detailInfoAty_area)
    LinearLayout llDetailInfoAtyArea;//点击地区
    @Bind(R.id.tv_detailInfoAty_address)
    TextView tvDetailInfoAtyAddress;//详细地址
    @Bind(R.id.ll_detailInfoAty_address)
    LinearLayout llDetailInfoAtyAddress;//点击详细地址


    private PickDialogPic dialogPic;// 选择照片
    DisplayImageOptions options;

    private ArrayList<GradeModel> gradeList = new ArrayList<>();
    private ArrayList<String> grades = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithDefaultTitle(R.layout.activity_detail_info, "详细信息");
    }

    @Override
    protected void initView() {
        dialogPic = new PickDialogPic(mContext, R.style.FullHeightDialog, this);
        setOnClickListeners(this, llDetailInfoAtyHead, llDetailInfoAtyName, llDetailInfoAtySex, llDetailInfoAtyGrade, llDetailInfoAtyArea, llDetailInfoAtyAddress);
        setInfo();
    }

    /**
     * 展示用户信息
     */
    private void setInfo() {
        int cornerRadius = DisplayUtils.dp2px(18, this);
        options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(cornerRadius))
                .build();
        //加载用户头像
        if (AppConfig.userVo.photourl != null) {
            if (AppConfig.userVo.photourl.length() > 0) {
                ivDetailInfoAtyHead.setVisibility(View.VISIBLE);
                ivDetailInfoAtyHeadBg.setVisibility(View.GONE);
                ZuoYeBaApplication.imageLoader.displayImage(AppConfig.userVo.photourl, ivDetailInfoAtyHead, options);
            }
        } else {
            ivDetailInfoAtyHead.setVisibility(View.GONE);
            ivDetailInfoAtyHeadBg.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        tvDetailInfoAtyName.setText(AppConfig.userVo.name);
        tvDetailInfoAtyGrade.setText(AppConfig.userVo.student.grade);
        tvDetailInfoAtySex.setText(AppConfig.userVo.sex == 1 ? "男" : AppConfig.userVo.sex == 2 ? "女" : "");
        tvDetailInfoAtyArea.setText(AppConfig.userVo.province + AppConfig.userVo.city + AppConfig.userVo.region);
        tvDetailInfoAtyAddress.setText(AppConfig.userVo.address);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_detailInfoAty_head:
                if (!dialogPic.isShowing()) {
                    dialogPic.show();
                }
                break;
            case R.id.ll_detailInfoAty_name:
                Intent intent = new Intent(this, EditNameActivity.class);
                intent.putExtra(Constants.PASS_NAME, AppConfig.userVo.name);
                startActivity(intent);
                break;
            case R.id.ll_detailInfoAty_sex:
                Intent intentSex = new Intent(this, ChooseSexActivity.class);
                intentSex.putExtra(Constants.PASS_SEX, AppConfig.userVo.sex);
                startActivity(intentSex);
                break;
            case R.id.ll_detailInfoAty_grade://年级
                //年级选择对话框
                getGradleList();
                break;
            case R.id.ll_detailInfoAty_area://选择省市区
                getLocalData();
                break;
            case R.id.ll_detailInfoAty_address://详细地址
                Intent intentAddress = new Intent(this, EditAddressActivity.class);
                intentAddress.putExtra(Constants.PASS_ADDRESS, AppConfig.userVo.address);
                startActivity(intentAddress);
                break;
            case R.id.tv_dialogPic_photo://点击了相机
                clickPhoto();
                break;
            case R.id.tv_dialogPic_album://点击了相簿
                clickAlbum();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        File choose_imageFile = new File(Constants.CHOOSE_PHOTO);
        File cut_imageFile = new File(Constants.CUT_PHOTO);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                if (!cut_imageFile.exists()) {
                    try {
                        cut_imageFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                        ToastUtil.showToastDefault(this, "载入出错，请重新选择图片");
                        return;
                    }
                }
            }
            switch (requestCode) {
                case Constants.CODE_REQUEST_CAMERA://相机数据已经被写入SDCard
                    //创建一个新的File路径  进行图片裁剪
                    FileUtils.startPhotoZoom(mContext, Uri.fromFile(choose_imageFile), 2, 200, 200, Uri.fromFile(cut_imageFile));
                    break;
                case Constants.CODE_REQUEST_GALLERY://相册
                    //跳转到图片裁剪界面
                    FileUtils.startPhotoZoom(mContext, data.getData(), 2, 200, 200, Uri.fromFile(cut_imageFile));
                    break;
                case Constants.CODE_REQUEST_ZOOM://裁剪照片的返回
                    putImageFile();
                    break;
                case Constants.CODE_503://修改姓名的返回
                    String name = data.getStringExtra(Constants.PASS_NAME);
                    tvDetailInfoAtyName.setText(name);
                    break;
                case Constants.CODE_507://修改性别的返回
                    int sex = data.getIntExtra(Constants.PASS_SEX, -1);
                    if (sex != -1) {
                        tvDetailInfoAtyName.setText(sex == 1 ? "男" : "女");
                    }
                    break;
            }
        }

    }

    /**
     * 请求权限的结果
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            try {
                File imageFile = new File(Constants.CHOOSE_PHOTO);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                    startActivityForResult(intent, Constants.CODE_REQUEST_CAMERA);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            for (int i = 0; i < grantResults.length; i++) {
                if (i == 0 && grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    ToastUtil.showToastDefault(mContext, "相机权限被禁用");
                }
                if (i == 1 && grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    ToastUtil.showToastDefault(mContext, "读写权限被禁用");
                }
            }
        }
    }

    /**
     * 点击了相机
     */
    private void clickPhoto() {
        //取消Dialog
        dialogPic.dismiss();
        //TODO 此处需要申请用户权限
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.CODE_201);
        } else {
            try {
                File imageFile = new File(Constants.CHOOSE_PHOTO);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                    startActivityForResult(intent, Constants.CODE_REQUEST_CAMERA);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 点击了相册
     */
    private void clickAlbum() {
        //取消Dialog
        dialogPic.dismiss();
        try {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, Constants.CODE_REQUEST_GALLERY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 裁剪照片成功  开始上传至服务器
     */
    private void putImageFile() {
        LoadingDialog.showLoadingDialog(mContext);
        File cutFile = new File(Constants.CUT_PHOTO);

        LoadingDialog.showLoadingDialog(mContext);
        EasyHttp.zuoyebaUpLoadPicture(mContext, 1, cutFile, new EasyHttp.OnEasyHttpDoneListener<CommonUploadModel>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<CommonUploadModel> resultBean) {
                changeUserInfo(resultBean.data.pathid);
                LoadingDialog.dismissLoadingDialog();
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                ToastUtil.showToastDefault(mContext, message);
                LoadingDialog.dismissLoadingDialog();
            }
        });

    }

    /**
     * 修改头像信息
     */
    private void changeUserInfo(int imageId) {
        LoadingDialog.showLoadingDialog(mContext);
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", AppConfig.token);
        params.put("imgid", imageId + "");
        EasyHttp.doPost(mContext, ServerURL.UPDSTUDENTINFO, params, null, null, false, new EasyHttp.OnEasyHttpDoneListener() {
            @Override
            public void onEasyHttpSuccess(ResultBean resultBean) {
                ToastUtil.showToastDefault(mContext, "头像修改成功!");
                LoadingDialog.dismissLoadingDialog();
                //TODO 此处需要修改用户实体   通知学生我的刷新头像
                //加载用户头像
                getUserInfo();

            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                ToastUtil.showToastDefault(mContext, message);
                LoadingDialog.dismissLoadingDialog();
            }
        });

    }

    /**
     * 获取用户信息
     */
    private void getUserInfo() {
        Map<String, String> params = new HashMap<String, String>();
        LoadingDialog.showLoadingDialog(mContext);
        params.put("token", AppConfig.token);
        EasyHttp.doPost(mContext, ServerURL.GETMEMBERINFO, params, null, AccMemberModel.class, false, new EasyHttp.OnEasyHttpDoneListener<AccMemberModel>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<AccMemberModel> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                AppConfig.userVo = resultBean.data;
                if (AppConfig.userVo.photourl != null) {
                    ivDetailInfoAtyHead.setVisibility(View.VISIBLE);
                    ivDetailInfoAtyHeadBg.setVisibility(View.GONE);
                    ZuoYeBaApplication.imageLoader.displayImage(AppConfig.userVo.photourl, ivDetailInfoAtyHead, options);
                } else {
                    ivDetailInfoAtyHead.setVisibility(View.GONE);
                    ivDetailInfoAtyHeadBg.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                LoadingDialog.dismissLoadingDialog();
                ToastUtil.showToastDefault(mContext, message);
            }
        });
    }


    /**
     * 获取年级列表
     */
    private void getGradleList() {
        LoadingDialog.showLoadingDialog(mContext);
        EasyHttp.doPost(mContext, ServerURL.GETGRADELIST, null, null, GradeModel.class, true, new EasyHttp.OnEasyHttpDoneListener<ArrayList<GradeModel>>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<ArrayList<GradeModel>> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                gradeList = resultBean.data;
                //筛选其中的年级字符串
                if (gradeList.size() == 0) {
                    ToastUtil.showToastDefault(mContext, "暂无年级!");
                } else {
                    new ChooseGradleDialog(mContext, gradeList, (int) AppConfig.userVo.student.gradeid, DetailInfoActivity.this).show();
                }
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                ToastUtil.showToastDefault(mContext, message);
                LoadingDialog.dismissLoadingDialog();
            }
        });

    }

    /**
     * 获取省市区列表
     */
    private void getLocalData() {
        LoadingDialog.showLoadingDialog(mContext);
        EasyHttp.doPost(mContext, ServerURL.GETAREA, null, null, CommonAreaModel.class, false, new EasyHttp.OnEasyHttpDoneListener<CommonAreaModel>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<CommonAreaModel> resultBean) {
                LoadingDialog.dismissLoadingDialog();
                if (resultBean.data == null) {
                    ToastUtil.showToastDefault(mContext, "暂无地区");
                    return;
                }
                if (resultBean.data.provinceList.size() == 0) {
                    ToastUtil.showToastDefault(mContext, "暂无地区");
                } else {
                    new ChooseLocalDialog(mContext, resultBean.data, DetailInfoActivity.this).show();
                }
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                ToastUtil.showToastDefault(mContext, message);
                LoadingDialog.dismissLoadingDialog();
            }
        });
    }

    /**
     * 年级选择回调
     *
     * @param model
     */
    @Override
    public void chooseDialog(final GradeModel model) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", AppConfig.token);
        params.put("gradeid", model.gradeid + "");
        LoadingDialog.showLoadingDialog(mContext);
        EasyHttp.doPost(mContext, ServerURL.UPDSTUDENTINFO, params, null, null, false, new EasyHttp.OnEasyHttpDoneListener<Object>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<Object> resultBean) {
                ToastUtil.showToastDefault(mContext, "操作成功");
                tvDetailInfoAtyGrade.setText(model.gradename);
                AppConfig.userVo.student.grade = model.gradename;
                AppConfig.userVo.student.gradeid = model.gradeid;
                LoadingDialog.dismissLoadingDialog();
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                ToastUtil.showToastDefault(mContext, message);
                LoadingDialog.dismissLoadingDialog();
            }
        });

    }

    @Override
    public void getChooseInfo(final String province, final String city, final String area, int areaId) {
        LoadingDialog.showLoadingDialog(mContext);
        Map<String, String> params = new HashMap();
        params.put("token", AppConfig.token);
        params.put("areaid", areaId + "");
        EasyHttp.doPost(mContext, ServerURL.UPDSTUDENTINFO, params, null, null, false, new EasyHttp.OnEasyHttpDoneListener<Object>() {
            @Override
            public void onEasyHttpSuccess(ResultBean<Object> resultBean) {
                ToastUtil.showToastDefault(mContext, "操作成功!");
                LoadingDialog.dismissLoadingDialog();
                tvDetailInfoAtyArea.setText(province + city + area);
                AppConfig.userVo.province = province;
                AppConfig.userVo.city = city;
                AppConfig.userVo.region = area;
            }

            @Override
            public void onEasyHttpFailure(String code, String message) {
                ToastUtil.showToastDefault(mContext, message);
                LoadingDialog.dismissLoadingDialog();
            }
        });

    }
}
