package com.molice.oneingdufs.utils;

import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONObject;

import com.molice.oneingdufs.activities.MainActivity;
import com.molice.oneingdufs.activities.SettingsActivity;
import com.molice.oneingdufs.androidpn.ServiceManager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.widget.Toast;

/**
 * 连接网络，调用{@link #logout()}方法注销登录，注销成功则清除本地存储的sessionid并设置isLogin=false，然后返回
 * {@link MainActivity} TODO 看AndroidPN有什么方法用于注销AndroidPN服务器的登录
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-3-24
 */
public class Logout {
	private Context context;
	private SharedPreferencesStorager storager;
	
	private AlertDialog sureLogout;
	private ProgressDialog logouting;

	public Logout(Context context) {
		this.context = context;
		this.storager = new SharedPreferencesStorager(this.context);
	}

	/**
	 * 注销成功则返回到{@link MainActivity}
	 */
	public void logout() {
		sureLogout = new AlertDialog.Builder(context).setTitle("确定退出登录吗？")
				.create();
		sureLogout.setCancelable(true);
		sureLogout.setButton("退出", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				new HttpConnectionUtils(connectionHandler, storager).get(
						ProjectConstants.URL.logout, null);
			}
		});
		sureLogout.setButton2("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		sureLogout.show();
	}

	private HttpConnectionHandler connectionHandler = new HttpConnectionHandler(
			context) {
		@Override
		protected void onStart(HttpRequestBase method) {
			// 因为已经存在一个对话框，所以这里需要重写onStart，否则context出错
			logouting = ProgressDialog.show(context, "退出中", "请等待...", true);
		}

		@Override
		protected void onSucceed(JSONObject result) {
			// 注销成功，清除用户数据
			storager.del("sessionid").set("isLogin", false).save();
			// 关闭后台Service
			if (SettingsActivity.getNotification(context))
				new ServiceManager(context).stopService();
			Toast.makeText(context, result.optString("resultMsg"),
					Toast.LENGTH_SHORT).show();
			logouting.dismiss();
			// 跳转到首页
			context.startActivity(new Intent(context.getApplicationContext(),
					MainActivity.class));
		}
	};
}
