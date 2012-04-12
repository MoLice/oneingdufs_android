package com.molice.oneingdufs.utils;

import java.net.SocketException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * ��{@link HttpConnectionUtils}�Ĳ�ͬ����׶ν��м������ơ�ͨ���̳б��ಢ��д��ͬ�ļ�����������Զ��������׶εĴ����߼���
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-4-9
 */
public class HttpConnectionHandler extends Handler {
	private Context context;
	private ProgressDialog progressDialog;
	
	private HttpRequestBase currentMethod;
	
	public HttpConnectionHandler(Context context) {
		this.context = context;
	}
	
	/**
	 * ����ʼ�����ڴ˽����������������
	 */
	protected void onStart(HttpRequestBase method) {
		this.currentMethod = method;
		progressDialog = ProgressDialog.show(context, "��ȴ�...", "����������...", true);
		progressDialog.setCancelable(true);
		
		// ��������������ʹ�÷��ؼ��رնԻ������ж�����
		progressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				currentMethod.abort();
				progressDialog.dismiss();
			}
		});
	}
	
	/**
	 * ��ͨ����ɹ�ʱ����
	 * @param result ���������ص�����
	 */
	protected void onSucceed(JSONObject result) {
		if(progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}
	
	/**
	 * Bitmap����ɹ�ʱ����
	 * @param result ���������ص�ͼƬ��Դ
	 */
	protected void onSucceed(Bitmap result) {
		if(progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}
	
	/**
	 * ����ɹ����������ʱ���á���Ϊ�������������404�ȣ�Ҳ�ɱ������������������������ж�resultMsg�ֶΡ�
	 * @param result ����˷��ص�JSONObject�������ٰ���"success":false,"resultMsg":"failed result"
	 */
	protected void onFailed(JSONObject result) {
		if(progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		Toast.makeText(context, result.optString("resultMsg"), Toast.LENGTH_LONG).show();
		Log.d("�������", "result=" + result.toString());
	}
	
	/**
	 * ����ʱʱ����
	 */
	protected void onTimeout() {
		if(progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		Toast.makeText(context, "�������ӳ�ʱ", Toast.LENGTH_LONG).show();
		Log.d("����ʱ", "����ʱ");
	}
	
	/**
	 * ��������쳣ʱ����
	 * @param e �쳣����
	 */
	protected void onError(Exception e) {
		if(progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		Toast.makeText(context, "�������Ӵ���" + e.toString(), Toast.LENGTH_LONG).show();
		Log.d("�������Ӵ���", "HttpConnectionHanlder#onError, e=" + e.toString());
		if(e instanceof SocketException)
			Log.d("�������Ӵ���", "HttpConnectionHanlder#onError, ����������, e=" + e.toString());
	}
	
	/**
	 * �������ʱ����
	 */
	protected void onComplete(HttpClient httpClient) {
		if(progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		httpClient.getConnectionManager().shutdown();
	}
	
	@Override
	public void handleMessage(Message message) {
		switch(message.what) {
		case HttpConnectionUtils.STATUS_START:
			// ����ʼ
			onStart((HttpRequestBase) message.obj);
			break;
		case HttpConnectionUtils.STATUS_SUCCEED:
			// ����ɹ�
			if(message.obj instanceof JSONObject)
				onSucceed((JSONObject) message.obj);
			else
				onSucceed((Bitmap) message.obj);
			break;
		case HttpConnectionUtils.STATUS_FAILED:
			// ����ɹ�������ʧ��
			onFailed((JSONObject) message.obj);
			break;
		case HttpConnectionUtils.STATUS_TIMEOUT:
			// ����ʱ
			onTimeout();
			break;
		case HttpConnectionUtils.STATUS_ERROR:
			// �������
			onError((Exception) message.obj);
			break;
		case HttpConnectionUtils.STATUS_COMPLETE:
			// �������
			onComplete((HttpClient) message.obj);
			break;
		}
	}
}
