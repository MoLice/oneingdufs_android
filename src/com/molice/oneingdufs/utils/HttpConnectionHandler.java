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
 * 对{@link HttpConnectionUtils}的不同处理阶段进行监听控制。通过继承本类并重写不同的监听句柄，可自定义各请求阶段的处理逻辑。<br/>
 * <strong>注意！！！如果生成实例的类不是Activity的子类，则需要用内部类的声明方式（参考{@link Logout}）</strong>
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
	 * 请求开始，可在此进行请求参数的设置
	 */
	protected void onStart(HttpRequestBase method) {
		this.currentMethod = method;
		progressDialog = ProgressDialog.show(context, "请等待...", "网络连接中...", true, true);
		
		// 如果在请求过程中使用返回键关闭对话框，则中断请求
		progressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				currentMethod.abort();
				progressDialog.dismiss();
			}
		});
	}
	
	/**
	 * 普通请求成功时调用
	 * @param result 服务器返回的数据
	 */
	protected void onSucceed(JSONObject result) {
		if(progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}
	
	/**
	 * Bitmap请求成功时调用
	 * @param result 服务器返回的图片资源
	 */
	protected void onSucceed(Bitmap result) {
		if(progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}
	
	/**
	 * 请求成功但处理错误时调用。若为错误请求代码如404等，也由本方法处理，因此特殊情况下需判断resultMsg字段。
	 * @param result 服务端返回的JSONObject对象，至少包含"success":false,"resultMsg":"failed result"
	 */
	protected void onFailed(JSONObject result) {
		if(progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		Toast.makeText(context, result.optString("resultMsg"), Toast.LENGTH_LONG).show();
		Log.d("处理错误", "result=" + result.toString());
	}
	
	/**
	 * 请求超时时调用
	 */
	protected void onTimeout() {
		if(progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		Toast.makeText(context, "网络连接超时", Toast.LENGTH_LONG).show();
		Log.d("请求超时", "请求超时");
	}
	
	/**
	 * 请求出现异常时调用
	 * @param e 异常对象
	 */
	protected void onError(Exception e) {
		if(progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		Log.d("测试看看", "HttpConnectionHandler#onError, e=" + e.toString());
		Toast.makeText(context, "网络连接错误，" + e.toString(), Toast.LENGTH_LONG).show();
		Log.d("网络连接错误", "HttpConnectionHanlder#onError, e=" + e.toString());
		if(e instanceof SocketException)
			Log.d("网络连接错误", "HttpConnectionHanlder#onError, 无网络连接, e=" + e.toString());
	}
	
	/**
	 * 请求结束时调用
	 */
	protected void onComplete(HttpClient httpClient) {
		if(progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		httpClient.getConnectionManager().shutdown();
	}
	
	protected void onNoCsrfToken() {
		Log.d("context", "HttpConnectionHandler#onNoCsrfToken, context=" + context.toString());
		Toast.makeText(context, "设置参数失败，操作中断", Toast.LENGTH_SHORT).show();
	}
	
	protected void actionShowDialog(JSONObject msg) {
		actionProgressDialog = ProgressDialog.show(context, msg.optString("title"), msg.optString("msg"), true, true);
		Log.d("测试", "HttpConnectionHandler#actionShowDialog, actionProgressDialog=" + actionProgressDialog.toString() + ", isShowing=" + String.valueOf(actionProgressDialog.isShowing()));
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
		Log.d("测试", "toast=" + toast);
		if(!toast.equals("null") && !toast.equals("")) {
			Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void handleMessage(Message message) {
		switch(message.what) {
		case HttpConnectionUtils.STATUS_START:
			// 请求开始
			onStart((HttpRequestBase) message.obj);
			break;
		case HttpConnectionUtils.STATUS_SUCCEED:
			// 请求成功
			if(message.obj instanceof JSONObject)
				onSucceed((JSONObject) message.obj);
			else
				onSucceed((Bitmap) message.obj);
			break;
		case HttpConnectionUtils.STATUS_FAILED:
			// 请求成功，处理失败
			onFailed((JSONObject) message.obj);
			break;
		case HttpConnectionUtils.STATUS_TIMEOUT:
			// 请求超时
			onTimeout();
			break;
		case HttpConnectionUtils.STATUS_ERROR:
			// 请求出错
			onError((Exception) message.obj);
			break;
		case HttpConnectionUtils.STATUS_COMPLETE:
			// 请求结束
			onComplete((HttpClient) message.obj);
			break;
		case HttpConnectionUtils.STATUS_NOCSRFTOKEN:
			// 发送请求前检查csrftoken不通过，不发起请求
			onNoCsrfToken();
			break;
		case HttpConnectionUtils.ACTION_SHOWDIALOG:
			// 打开一个对话框
			actionShowDialog((JSONObject) message.obj);
			break;
		case HttpConnectionUtils.ACTION_CHANGEDIALOG:
			actionChangeDialog((JSONObject) message.obj);
			break;
		case HttpConnectionUtils.ACTION_DISMISSDIALOG:
			// 关闭已打开的对话框
			actionDismissDialog(String.valueOf(message.obj));
			break;
		}
	}
}
