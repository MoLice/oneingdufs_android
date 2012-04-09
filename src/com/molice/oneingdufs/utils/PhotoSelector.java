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
 * ����ʵ���󣬵��ø���set�������ò����������ļ�����ͼƬ��߱ȡ��̶����ֵ��JPGѹ������<br/>
 * ��onActivityResult()�������ú󣬼���ʹ��{@link #getResultPicture()}�õ�Bitmap���͵�ͼƬ����<br/>
 * ͼƬ�洢·��д��{@link ProjectConstants#IMAGE_PATH}��
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-3-29
 */
public class PhotoSelector {
	public static final int PHOTOGHRAPH = 10;// ����
	public static final int PHOTOZOOM = 11;// ����
	public static final int PHOTORESOULT = 12;// �ü����
	
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
		// ���ó�ʼֵ
		setFilenameByCurrentTime();
		this.quality = 80;
	}
	public Bitmap getResultPicture() {
		return this.resultPicture;
	}
	/**
	 * �ļ����ǲ���б�ܵ��ַ��������磬temp.jpg����Ҫ�󣬵�/temp.jpg������<br/>
	 * �������ã���Ĭ��Ϊ��ǰ������ʱ����.jpg
	 * @param filename
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getFilename() {
		return this.filename;
	}
	/**
	 * ���òü��Ŀ�߱���
	 * @param x ��
	 * @param y ��
	 */
	public void setXYProportion(int x, int y) {
		this.x = x;
		this.y = y;
	}
	/**
	 * �̶����ֵ
	 * @param x ��
	 * @param y ��
	 */
	public void setXYValue(int x, int y) {
		this.xValue = x;
		this.yValue = y;
	}
	/**
	 * ����ͼƬ��ѹ��������Ĭ��Ϊ80
	 * @param quality 0-100ֵ
	 */
	public void setQuality(int quality) {
		this.quality = quality;
	}
	/**
	 * ת��������ѡ��
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
	 * �ڵ�����ͼ��onActivityResult�ڵ��ã���Ҫ��super.onActivityResult()��ӵ�����
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == 0)
			return;
		// ����
		if (requestCode == PHOTOGHRAPH) {
			File picture = new File(Environment.getExternalStorageDirectory()
					+ "/" + filename);
			startPhotoZoom(Uri.fromFile(picture));
		}
		if (data == null) {
			return;
		}
		// ��ȡ�������ͼƬ
		if (requestCode == PHOTOZOOM) {
			startPhotoZoom(data.getData());
		}
		// ������
		if (requestCode == PHOTORESOULT) {
			Bundle extras = data.getExtras();
			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				// (0 - 100)ѹ���ļ�
				photo.compress(Bitmap.CompressFormat.JPEG, quality, stream);
				resultPicture = photo;
			}
		}
	}
	private void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, ProjectConstants.IMAGE_PATH);
		intent.putExtra("crop", "true");
		// aspectX aspectY �ǿ�ߵı���
		if(x != 0 && y != 0) {
			intent.putExtra("aspectX", x);
			intent.putExtra("aspectY", y);
		}
		// outputX outputY �ǲü�ͼƬ���
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
