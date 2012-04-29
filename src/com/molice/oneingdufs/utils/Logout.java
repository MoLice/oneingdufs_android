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
 * �������磬����{@link #logout()}����ע����¼��ע���ɹ���������ش洢��sessionid������isLogin=false��Ȼ�󷵻�
 * {@link MainActivity} TODO ��AndroidPN��ʲô��������ע��AndroidPN�������ĵ�¼
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
	 * ע���ɹ��򷵻ص�{@link MainActivity}
	 */
	public void logout() {
		sureLogout = new AlertDialog.Builder(context).setTitle("ȷ���˳���¼��")
				.create();
		sureLogout.setCancelable(true);
		sureLogout.setButton("�˳�", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				new HttpConnectionUtils(new ConnectionHandler(context), context).post(
						ProjectConstants.URL.logout, null);
			}
		});
		sureLogout.setButton2("ȡ��", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		sureLogout.show();
	}
	
	// �������ڲ�������ΪLogout����Activity�����࣬���Լ�����HttpConnectionHandler handler = new HttpConnectionHandler(context){}����ʽ����handler��
	// ��˴������context�ǻ�δ��ʼ����context��������ʵ�������null����ͻᵼ�µ�δ����д�ķ���ʹ��contextʱ�����NullPoint�쳣
	class ConnectionHandler extends HttpConnectionHandler {

		public ConnectionHandler(Context context) {
			super(context);
		}
		
		private void progressLogout() {
			// ע���ɹ�������û�����
			storager.del("sessionid").set("isLogin", false).save();
			// �رպ�̨Service
			if (SettingsActivity.getNotificationEnabled(context))
				new ServiceManager(context).stopService();
			// ��ת����ҳ
			context.startActivity(new Intent(context.getApplicationContext(),
					MainActivity.class));
		}
		
		@Override
		protected void onStart(HttpRequestBase method) {
			Log.d("����Context", "Logout#connectionHandler#onStart, context=" + context);
			// ��Ϊ�Ѿ�����һ���Ի�������������Ҫ��дonStart������context����
			logouting = ProgressDialog.show(context, "�˳���", "��ȴ�...", true);
		}
		@Override
		protected void onSucceed(JSONObject result) {
			Toast.makeText(context, result.optString("resultMsg"),
					Toast.LENGTH_SHORT).show();
			progressLogout();
		}
		@Override
		protected void onFailed(JSONObject result) {
			Toast.makeText(context, "���˳�", Toast.LENGTH_SHORT).show();
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
