package com.molice.oneingdufs.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;

import com.molice.oneingdufs.activities.MainActivity;
import com.molice.oneingdufs.interfaces.OnHttpRequestListener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * 连接网络，调用{@link #logout()}方法注销登录，注销成功则清除本地存储的sessionid并设置isLogin=false，然后返回{@link MainActivity}
 * TODO 看AndroidPN有什么方法用于注销AndroidPN服务器的登录
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-3-24
 */
public class Logout {
	private Context context;
	private ClientToServer client;
	private SharedPreferencesStorager storager;
	private AlertDialog sureLogout;
	
	public Logout(Context context) {
		this.context = context;
		this.client = new ClientToServer(this.context);
		this.storager = new SharedPreferencesStorager(this.context);
	}
	
	/**
	 * 注销成功则返回到{@link MainActivity}
	 */
	public void logout() {
		sureLogout = new AlertDialog.Builder(context)
			.setTitle("确定退出登录吗？")
			.setPositiveButton("退出", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					/*
					Log.d("退出登录", "真的发送了");
					client.setOnRequestListener(listener);
					client.post(ProjectConstants.URL_LOGOUT, new JSONObject(), 0);
					*/
					storager.del("sessionid")
						.set("isLogin", false)
						.save();
					// 关闭对话框
					sureLogout.dismiss();
					Toast.makeText(context, "退出成功", Toast.LENGTH_SHORT).show();
					// 跳转到首页
					context.startActivity(new Intent(context.getApplicationContext(), MainActivity.class));
				}
			})
			.setNegativeButton("取消", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			})
			.show();
	}
	
	private OnHttpRequestListener listener = new OnHttpRequestListener() {
		
		@Override
		public void onTimeout(int requestCode, ClientToServer target,
				HttpRequestBase method, ConnectTimeoutException e) {
			Toast.makeText(context, "网络延时，请稍后重试", Toast.LENGTH_LONG);
		}
		
		@Override
		public void onSuccess(int requestCode, ClientToServer target,
				HttpRequestBase method, HttpResponse response, JSONObject result) {
			switch(requestCode) {
			case 0:
				// 注销登录
				if(result.optBoolean("success")) {
					// 注销成功，清除用户数据，关闭后台Service
					storager.del("sessionid")
						.set("isLogin", false)
						.save();
					// 关闭对话框
					sureLogout.dismiss();
					// 跳转到首页
					context.startActivity(new Intent(context.getApplicationContext(), MainActivity.class));
				} else {
					sureLogout.setMessage(result.optString("resultMsg") + "，请重试");
				}
				break;
			}
		}
		
		@Override
		public void onFailed(int requestCode, ClientToServer target,
				HttpRequestBase method, HttpResponse response) {
			sureLogout.setMessage("退出登录失败，请重试");
			Log.d("Logout", "onFailed, status=" + String.valueOf(response.getStatusLine().getStatusCode()));
			Log.d("Logout", response.getEntity().toString());
		}
		
		@Override
		public void onException(int requestCode, ClientToServer target,
				HttpRequestBase method, Exception e) {
			sureLogout.setMessage("退出登录失败，请重试");
			Log.d("Logout", "logout, e=" + e.toString());
		}
		
		@Override
		public void beforeSend(int requestCode, ClientToServer target,
				HttpRequestBase method, HttpClient client) {
		}
		
		@Override
		public void afterSend(int requestCode, ClientToServer target,
				HttpRequestBase method, HttpClient client) {
		}
	};
}
