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
 * �������磬����{@link #logout()}����ע����¼��ע���ɹ���������ش洢��sessionid������isLogin=false��Ȼ�󷵻�{@link MainActivity}
 * TODO ��AndroidPN��ʲô��������ע��AndroidPN�������ĵ�¼
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
	 * ע���ɹ��򷵻ص�{@link MainActivity}
	 */
	public void logout() {
		sureLogout = new AlertDialog.Builder(context)
			.setTitle("ȷ���˳���¼��")
			.setPositiveButton("�˳�", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					/*
					Log.d("�˳���¼", "��ķ�����");
					client.setOnRequestListener(listener);
					client.post(ProjectConstants.URL_LOGOUT, new JSONObject(), 0);
					*/
					storager.del("sessionid")
						.set("isLogin", false)
						.save();
					// �رնԻ���
					sureLogout.dismiss();
					Toast.makeText(context, "�˳��ɹ�", Toast.LENGTH_SHORT).show();
					// ��ת����ҳ
					context.startActivity(new Intent(context.getApplicationContext(), MainActivity.class));
				}
			})
			.setNegativeButton("ȡ��", new OnClickListener() {
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
			Toast.makeText(context, "������ʱ�����Ժ�����", Toast.LENGTH_LONG);
		}
		
		@Override
		public void onSuccess(int requestCode, ClientToServer target,
				HttpRequestBase method, HttpResponse response, JSONObject result) {
			switch(requestCode) {
			case 0:
				// ע����¼
				if(result.optBoolean("success")) {
					// ע���ɹ�������û����ݣ��رպ�̨Service
					storager.del("sessionid")
						.set("isLogin", false)
						.save();
					// �رնԻ���
					sureLogout.dismiss();
					// ��ת����ҳ
					context.startActivity(new Intent(context.getApplicationContext(), MainActivity.class));
				} else {
					sureLogout.setMessage(result.optString("resultMsg") + "��������");
				}
				break;
			}
		}
		
		@Override
		public void onFailed(int requestCode, ClientToServer target,
				HttpRequestBase method, HttpResponse response) {
			sureLogout.setMessage("�˳���¼ʧ�ܣ�������");
			Log.d("Logout", "onFailed, status=" + String.valueOf(response.getStatusLine().getStatusCode()));
			Log.d("Logout", response.getEntity().toString());
		}
		
		@Override
		public void onException(int requestCode, ClientToServer target,
				HttpRequestBase method, Exception e) {
			sureLogout.setMessage("�˳���¼ʧ�ܣ�������");
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
