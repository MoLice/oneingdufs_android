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
 * ��{@link HttpConnectionUtils}�Ĳ�ͬ����׶ν��м������ơ�ͨ���̳б��ಢ��д��ͬ�ļ�����������Զ��������׶εĴ����߼���<br/>
 * <strong>ע�⣡�����������ʵ�����಻��Activity�����࣬����Ҫ���ڲ����������ʽ���ο�{@link Logout}��</strong>
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-4-9
 */
public class HttpConnectionHandler extends Handler {
	private Context context;
	private ProgressDialog progressDialog;
	
	private ProgressDialog actionProgressDialog;
	
	private HttpRequestBase currentMethod;
	
	public HttpConnectionHandler(Context context) {
		this.context = context;
	}
	
	/**
	 * ����ʼ�����ڴ˽����������������
	 */
	protected void onStart(HttpRequestBase method) {
		this.currentMethod = method;
		progressDialog = ProgressDialog.show(context, "��ȴ�...", "����������...", true, true);
		
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
		Log.d("���Կ���", "HttpConnectionHandler#onError, e=" + e.toString());
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
	
	protected void onNoCsrfToken() {
		Log.d("context", "HttpConnectionHandler#onNoCsrfToken, context=" + context.toString());
		Toast.makeText(context, "���ò���ʧ�ܣ������ж�", Toast.LENGTH_SHORT).show();
	}
	
	protected void actionShowDialog(JSONObject msg) {
		actionProgressDialog = ProgressDialog.show(context, msg.optString("title"), msg.optString("msg"), true, true);
		Log.d("����", "HttpConnectionHandler#actionShowDialog, actionProgressDialog=" + actionProgressDialog.toString() + ", isShowing=" + String.valueOf(actionProgressDialog.isShowing()));
	}
	protected void actionChangeDialog(JSONObject msg) {
		if(actionProgressDialog != null && actionProgressDialog.isShowing()) {
			actionProgressDialog.setTitle(msg.optString("title"));
			actionProgressDialog.setMessage(msg.optString("msg"));
		}
	}
	protected void actionDismissDialog(String toast) {
		if(actionProgressDialog != null && actionProgressDialog.isShowing()) {
			actionProgressDialog.dismiss();
		}
		Log.d("����", "toast=" + toast);
		if(!toast.equals("null") && !toast.equals("")) {
			Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
		}
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
		case HttpConnectionUtils.STATUS_NOCSRFTOKEN:
			// ��������ǰ���csrftoken��ͨ��������������
			onNoCsrfToken();
			break;
		case HttpConnectionUtils.ACTION_SHOWDIALOG:
			// ��һ���Ի���
			actionShowDialog((JSONObject) message.obj);
			break;
		case HttpConnectionUtils.ACTION_CHANGEDIALOG:
			actionChangeDialog((JSONObject) message.obj);
			break;
		case HttpConnectionUtils.ACTION_DISMISSDIALOG:
			// �ر��Ѵ򿪵ĶԻ���
			actionDismissDialog(String.valueOf(message.obj));
			break;
		}
	}
}
