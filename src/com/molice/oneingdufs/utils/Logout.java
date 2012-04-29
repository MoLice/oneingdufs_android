package com.molice.oneingdufs.utils;

import org.apache.http.client.HttpClient;
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
import android.util.Log;
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
				new HttpConnectionUtils(new ConnectionHandler(context), context).post(
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
	
	// 必须用内部类是因为Logout不是Activity的子类，所以假如用HttpConnectionHandler handler = new HttpConnectionHandler(context){}的形式创建handler，
	// 则此处传入的context是还未初始化的context，所以其实传入的是null，这就会导致当未被重写的方法使用context时会出现NullPoint异常
	class ConnectionHandler extends HttpConnectionHandler {

		public ConnectionHandler(Context context) {
			super(context);
		}
		
		private void progressLogout() {
			// 注销成功，清除用户数据
			storager.del("sessionid").set("isLogin", false).save();
			// 关闭后台Service
			if (SettingsActivity.getNotificationEnabled(context))
				new ServiceManager(context).stopService();
			// 跳转到首页
			context.startActivity(new Intent(context.getApplicationContext(),
					MainActivity.class));
		}
		
		@Override
		protected void onStart(HttpRequestBase method) {
			Log.d("看看Context", "Logout#connectionHandler#onStart, context=" + context);
			// 因为已经存在一个对话框，所以这里需要重写onStart，否则context出错
			logouting = ProgressDialog.show(context, "退出中", "请等待...", true);
		}
		@Override
		protected void onSucceed(JSONObject result) {
			Toast.makeText(context, result.optString("resultMsg"),
					Toast.LENGTH_SHORT).show();
			progressLogout();
		}
		@Override
		protected void onFailed(JSONObject result) {
			Toast.makeText(context, "已退出", Toast.LENGTH_SHORT).show();
			progressLogout();
		}
		@Override
		protected void onComplete(HttpClient httpClient) {
			super.onComplete(httpClient);
			if(logouting != null && logouting.isShowing()) {
				logouting.dismiss();
			}
		}
	}
}
