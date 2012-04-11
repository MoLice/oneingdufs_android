package com.molice.oneingdufs.utils;

import org.json.JSONObject;

import com.molice.oneingdufs.activities.MainActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;

/**
 * �������磬����{@link #logout()}����ע����¼��ע���ɹ���������ش洢��sessionid������isLogin=false��Ȼ�󷵻�{@link MainActivity}
 * TODO ��AndroidPN��ʲô��������ע��AndroidPN�������ĵ�¼
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
	 * ע���ɹ��򷵻ص�{@link MainActivity}
	 */
	public void logout() {
		sureLogout = new AlertDialog.Builder(context)
			.setTitle("ȷ���˳���¼��")
			.setPositiveButton("�˳�", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					new HttpConnectionUtils(connectionHandler, storager).post(ProjectConstants.URL_LOGOUT, new JSONObject());
				}
			})
			.setNegativeButton("ȡ��", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.show();
	}
	private HttpConnectionHandler connectionHandler = new HttpConnectionHandler(context) {
		@Override
		protected void onSucceed(JSONObject result) {
			// ע���ɹ�������û����ݣ��رպ�̨Service
			storager.del("sessionid")
				.set("isLogin", false)
				.save();
			// �رնԻ���
			sureLogout.dismiss();
			// ��ת����ҳ
			context.startActivity(new Intent(context.getApplicationContext(), MainActivity.class));
		}
		@Override
		protected void onFailed(JSONObject result) {
			super.onFailed(result);
			sureLogout.setMessage(result.optString("resultMsg") + "��������");
		}
	};
}
