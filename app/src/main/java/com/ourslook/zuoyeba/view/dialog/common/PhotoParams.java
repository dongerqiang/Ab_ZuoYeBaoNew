package com.ourslook.zuoyeba.view.dialog.common;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import java.io.Serializable;


/**
 * [裁剪参数配置类]
 * 
 * @author huxinwu
 * @version 1.0
 * @date 2015-1-7
 * 
 **/
public class PhotoParams implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 8137152200188550229L;
	public static final String CROP_TYPE = "image/*";
    public static final String CROP_FILE_NAME = "crop_file.jpg";
    public static final String OUTPUT_FORMAT = Bitmap.CompressFormat.JPEG.toString();
    
    public static final int DEFAULT_ASPECT_X = 1;
    public static final int DEFAULT_ASPECT_Y = 2;
    public static final int DEFAULT_OUTPUT_X = 480;
    public static final int DEFAULT_OUTPUT_Y = 800;

    /** 临时地址 **/ 
    public transient Uri uri;
    /** 输出地址 **/ 
    public transient Uri outputUri;
    
    /** 类型 **/ 
    public String type;
    
    /** 输入类型，图片如jpg **/ 
    public String outputFormat;
    
    /** crop为true可以剪裁 **/ 
    public String crop;
    public boolean scale;
    public boolean returnData;
    public boolean noFaceDetection;  //人脸识别
    public boolean scaleUpIfNeeded;
    
    /** aspectX aspectY 是宽高的比例 **/ 
    public int aspectX;
    public int aspectY;
    
    /** outputX,outputY 是剪裁图片的宽高 **/ 
    public int outputX;
    public int outputY;

    public PhotoParams() {
    	crop = "true";
    	type = CROP_TYPE;
    	//这两个值都应该动态 这样选择多个图片文件的时候不会重复
    	//uri = buildUri();
    	outputUri = saveBuildUri();
    	uri = outputUri;
    	scale = true;
    	/** 三星 s4 移动版 返回true才能上传照片，其他机型则返回false正常**/
    	returnData = false;
    	noFaceDetection = true;
    	scaleUpIfNeeded = true;
        outputFormat = OUTPUT_FORMAT;
        aspectX = DEFAULT_ASPECT_X;
        aspectY = DEFAULT_ASPECT_Y;
        outputX = DEFAULT_OUTPUT_X;
        outputY = DEFAULT_OUTPUT_Y;
    }
    
    private Uri buildUri(){
        return Uri.fromFile(Environment.getExternalStorageDirectory()).buildUpon().appendPath(CROP_FILE_NAME).build();
    }
    /**
     * 保存图片文件路径
     * @return
     */
    private Uri saveBuildUri(){
 
    	return Uri.fromFile(Environment.getExternalStorageDirectory())
    			 .buildUpon().appendPath(Constants.IMAGE_PATH +
    					 System.currentTimeMillis() + ".jpg").build();
    }
    
}
