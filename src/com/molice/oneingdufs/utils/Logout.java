package com.molice.oneingdufs.utils;

import org.json.JSONObject;

import com.molice.oneingdufs.activities.MainActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;

/**
 * 连接网络，调用{@link #logout()}方法注销登录，注销成功则清除本地存储的sessionid并设置isLogin=false，然后返回{@link MainActivity}
 * TODO 看AndroidPN有什么方法用于注销AndroidPN服务器的登录
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-3-24
 */
public class Logout {
	private Context context;
	private SharedPreferencesStorager storager;
	private AlertDialog sureLogout;
	
	public Logout(Context context) {
		this.context = context;
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
					new HttpConnectionUtils(connectionHandler, storager).post(ProjectConstants.URL_LOGOUT, new JSONObject());
				}
			})
			.setNegativeButton("取消", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.show();
	}
	private HttpConnectionHandler connectionHandler = new HttpConnectionHandler(context) {
		@Override
		protected void onSucceed(JSONObject result) {
			// 注销成功，清除用户数据，关闭后台Service
			storager.del("sessionid")
				.set("isLogin", false)
				.save();
			// 关闭对话框
			sureLogout.dismiss();
			// 跳转到首页
			context.startActivity(new Intent(context.getApplicationContext(), MainActivity.class));
		}
		@Override
		protected void onFailed(JSONObject result) {
			super.onFailed(result);
			sureLogout.setMessage(result.optString("resultMsg") + "，请重试");
		}
	};
}
