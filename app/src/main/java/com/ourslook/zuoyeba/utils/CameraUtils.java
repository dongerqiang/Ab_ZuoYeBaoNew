package com.ourslook.zuoyeba.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 拍照工具类
 * Created by Administrator on 2016/4/13.
 */
public class CameraUtils {
    /**
     * 拍照
     *
     * @param context
     * @param uri         存储路径
     * @param requestCode 请求码
     */
    public static void tackPhoto(Context context, Uri uri, int requestCode) {
        Activity activity = (Activity) context;
        if (isSdcardExisting()) {
            Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            activity.startActivityForResult(cameraIntent, requestCode);
        } else {
            ToastUtil.showToastOnce(activity, "请插入sd卡");
        }
    }

    /**
     * 图像处理
     *
     * @param path 图像路径
     */
    public static void compressImage(String path) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        Bitmap image;
        BitmapFactory.decodeFile(path, opts);
        int sampleSize = 1;//缩放的比例
        while (opts.outHeight / sampleSize > 640 || opts.outWidth / sampleSize > 480) {
            sampleSize *= 2;
        }
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = sampleSize;
        image = BitmapFactory.decodeFile(path, opts);
        File file = new File(path);
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            image.recycle();
        }
    }

    private static boolean isSdcardExisting() {
        final String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }
}
