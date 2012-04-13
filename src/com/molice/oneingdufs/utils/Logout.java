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
				new HttpConnectionUtils(connectionHandler, storager).get(
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

	private HttpConnectionHandler connectionHandler = new HttpConnectionHandler(
			context) {
		@Override
		protected void onStart(HttpRequestBase method) {
			// ��Ϊ�Ѿ�����һ���Ի�������������Ҫ��дonStart������context����
			logouting = ProgressDialog.show(context, "�˳���", "��ȴ�...", true);
		}

		@Override
		protected void onSucceed(JSONObject result) {
			// ע���ɹ�������û�����
			storager.del("sessionid").set("isLogin", false).save();
			// �رպ�̨Service
			if (SettingsActivity.getNotification(context))
				new ServiceManager(context).stopService();
			Toast.makeText(context, result.optString("resultMsg"),
					Toast.LENGTH_SHORT).show();
			logouting.dismiss();
			// ��ת����ҳ
			context.startActivity(new Intent(context.getApplicationContext(),
					MainActivity.class));
		}
	};
}
