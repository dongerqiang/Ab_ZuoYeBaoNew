package com.ourslook.zuoyeba.view.dialog;


import android.app.Activity;
import android.app.Dialog;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.view.dialog.common.PhotoParams;
import com.ourslook.zuoyeba.view.dialog.common.PhotoUtils;


/**
* @ClassName: TakePhotoDialog 
* @Description: 图片选择方式 
* @author tu 
* @date 2015年10月12日 上午9:28:37 
* @version V1.0
*
 */
public class TakePhotoDialog {
	private Activity mContext;
	private TextView tvTakePhoto;
	private TextView tvImage;
	private Dialog dialog;
	private LayoutInflater mInflater;
	private View layout;
	private PhotoUtils mPhotoUtils;
	public TakePhotoDialog(Activity context) {

		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		this.layout = this.mInflater.inflate(R.layout.dialog_pick_picture, null);
		initView();
	}

	private void initView() {
		tvTakePhoto = (TextView) layout.findViewById(R.id.tv_dialogPic_photo);
		tvImage = (TextView) layout.findViewById(R.id.tv_dialogPic_album);

		initDialog();
		initUtils();
	}
	private void initUtils(){
		mPhotoUtils = new PhotoUtils(mContext);
		mPhotoUtils.setOnPhotoResultListener(new PhotoUtils.OnPhotoResultListener() {
			@Override
			public void onPhotoResult(Uri uri) {
//				String path = mPhotoUtils.getPath(mContext,uri);
				onPhotoResultListener.onPhotoResult(uri);
			}
			@Override
			public void onPhotoCancel() {
				onPhotoResultListener.onPhotoCancel();
			}
		});
	}

	private void initDialog() {
		if (null != tvTakePhoto) {
			tvTakePhoto.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mPhotoUtils.takePicture(mContext, new PhotoParams());
					cancelDialog();
				}
			});
		}
		if (null != tvImage) {
			tvImage.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mPhotoUtils.selectPicture(mContext, new PhotoParams());
					cancelDialog();
				}
			});
		}
		dialog = new Dialog(mContext, R.style.FullHeightDialog);
		dialog.setContentView(layout);
		dialog.setCanceledOnTouchOutside(true);
//		Window dialogWindow = dialog.getWindow();
//		dialogWindow.setWindowAnimations(R.style.dialogWindowAnim);

//		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//		dialogWindow.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
//		lp.width = (int) (CommonUtils.getScreenWidth(mContext) * 0.95); // 宽度设置为屏幕的
//		dialogWindow.setAttributes(lp);

		// * 将对话框的大小按屏幕大小的百分比设置

		// WindowManager m = getWindowManager();
		// Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		// WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //
		// 获取对话框当前的参数值
		// p.height = (int) (Utils.getDisplayWidth() * 0.6); // 高度设置为屏幕的0.6
		// p.width = (int) (Utils.getDisplayWidth() * 1); // 宽度设置为屏幕的1
		// dialogWindow.setAttributes(p);

	}

	public void showDialog() {
		dialog.show();
	}

	public void cancelDialog() {
		if (null != dialog)
			dialog.dismiss();
	}

	private  OnPhotoUrlResultListener onPhotoResultListener;
	/**
	 * 图片路径回调监听
	 * @return
	 */
	public interface OnPhotoUrlResultListener {
		void onPhotoResult(Uri uri);
		void onPhotoCancel();
	}

	public void setOnPhotoUrlResultListener(OnPhotoUrlResultListener onPhotoUrlResultListener) {
		this.onPhotoResultListener = onPhotoUrlResultListener;
	}

	public OnPhotoUrlResultListener getOnPhotoResultListener() {
		return onPhotoResultListener;
	}


	public void setmPhotoUtils(PhotoUtils mPhotoUtils) {
		this.mPhotoUtils = mPhotoUtils;
	}
	public PhotoUtils getmPhotoUtils() {
		return mPhotoUtils;
	}
}
