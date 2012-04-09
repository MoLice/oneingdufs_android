package com.molice.oneingdufs.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Time;

/**
 * 生成实例后，调用各个set方法设置参数，包括文件名、图片宽高比、固定宽高值、JPG压缩质量<br/>
 * 在onActivityResult()方法调用后，即可使用{@link #getResultPicture()}得到Bitmap类型的图片数据<br/>
 * 图片存储路径写在{@link ProjectConstants#IMAGE_PATH}里
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-3-29
 */
public class PhotoSelector {
	public static final int PHOTOGHRAPH = 10;// 拍照
	public static final int PHOTOZOOM = 11;// 缩放
	public static final int PHOTORESOULT = 12;// 裁剪结果
	
	private Activity activity;
	private String filename;
	private int x;
	private int y;
	private int xValue;
	private int yValue;
	private int quality;
	private Bitmap resultPicture;
	
	public PhotoSelector(Activity activity) {
		this.activity = activity;
		// 设置初始值
		setFilenameByCurrentTime();
		this.quality = 80;
	}
	public Bitmap getResultPicture() {
		return this.resultPicture;
	}
	/**
	 * 文件名是不带斜杠的字符串，例如，temp.jpg符合要求，但/temp.jpg不符合<br/>
	 * 若不设置，则默认为当前年月日时分秒.jpg
	 * @param filename
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getFilename() {
		return this.filename;
	}
	/**
	 * 设置裁剪的宽高比例
	 * @param x 宽
	 * @param y 高
	 */
	public void setXYProportion(int x, int y) {
		this.x = x;
		this.y = y;
	}
	/**
	 * 固定宽高值
	 * @param x 宽
	 * @param y 高
	 */
	public void setXYValue(int x, int y) {
		this.xValue = x;
		this.yValue = y;
	}
	/**
	 * 设置图片的压缩比例，默认为80
	 * @param quality 0-100值
	 */
	public void setQuality(int quality) {
		this.quality = quality;
	}
	/**
	 * 转到相册进行选择
	 */
	public void startSeletPhoto() {
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				ProjectConstants.IMAGE_PATH);
		activity.startActivityForResult(intent, PHOTOZOOM);
	}
	public void startTakeNewPhoto() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
				Environment.getExternalStorageDirectory(), filename)));
		activity.startActivityForResult(intent, PHOTOGHRAPH);
	}
	/**
	 * 在调用视图的onActivityResult内调用，需要将super.onActivityResult()添加到后面
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == 0)
			return;
		// 拍照
		if (requestCode == PHOTOGHRAPH) {
			File picture = new File(Environment.getExternalStorageDirectory()
					+ "/" + filename);
			startPhotoZoom(Uri.fromFile(picture));
		}
		if (data == null) {
			return;
		}
		// 读取相册缩放图片
		if (requestCode == PHOTOZOOM) {
			startPhotoZoom(data.getData());
		}
		// 处理结果
		if (requestCode == PHOTORESOULT) {
			Bundle extras = data.getExtras();
			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				// (0 - 100)压缩文件
				photo.compress(Bitmap.CompressFormat.JPEG, quality, stream);
				resultPicture = photo;
			}
		}
	}
	private void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, ProjectConstants.IMAGE_PATH);
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		if(x != 0 && y != 0) {
			intent.putExtra("aspectX", x);
			intent.putExtra("aspectY", y);
		}
		// outputX outputY 是裁剪图片宽高
		if(xValue != 0 && yValue != 0) {
			intent.putExtra("outputX", xValue);
			intent.putExtra("outputY", yValue);
		}
		intent.putExtra("return-data", true);
		activity.startActivityForResult(intent, PHOTORESOULT);
	}
	private void setFilenameByCurrentTime() {
		Time time = new Time("GMT+8");
		time.setToNow();
		this.filename = new StringBuilder().append(time.year).append(time.month).append(time.monthDay).append(time.hour).append(time.minute).append(time.second).append(".jpg").toString();
	}
}
