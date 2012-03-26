package com.molice.oneingdufs.utils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * ���ڴ洢��Ŀ����������url
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-3-24
 */
public class ProjectConstants {
	// url������URL_DOMAIN��������ַ��ͷ��β��Ҫ���б��/
	public final static String URL_HOST = "http://oneingdufs.sinaapp.com/api";
	// global ȫ��
	/** ��ȡcsrftoken*/
	public final static String URL_GETCSRFTOKEN = "/getcsrftoken/";
	/** ������Ϣ������һ���û�*/
	public final static String URL_SENDNOTIFICATION = "/sendnotification/";
	/** ����XMPP�û�����Ӧ�÷��������ڵ�¼������ʹ�ã���Ҫ����Ϊ���������������*/
	public final static String URL_UPDATEAPNUSERNAME = "/updateapnusername/";
	// home �û�����
	/** ��¼*/
	public final static String URL_LOGIN = "/home/login/";
	/** ע��*/
	public final static String URL_REGISTER = "/home/register/";
	/** �˳���½*/
	public final static String URL_LOGOUT = "/home/logout/";
	
	// ������õľ�̬����
	/**
	 * �����Ի���
	 * @param context ������
	 * @param title �Ի�����⣬��Ϊnull������Ϊ"����"
	 * @param message �Ի������ݣ���Ϊnull������
	 * @param oneButton ���Ϊtrue����ʾȷ����ť�����Ϊfalse����ʾȷ����ȡ����ť
	 * @return Builder���󣬷����һ�������û�����
	 */
	public static Builder alertDialog(Context context, String title, String message, boolean oneButton) {
		Builder dialog = new AlertDialog.Builder(context);
		if(title == null)
			title = "����";
		if(message != null)
			dialog.setMessage(message);
		dialog.setTitle(title);
		dialog.setPositiveButton("ȷ��", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		if(!oneButton) {
			dialog.setNegativeButton("ȡ��", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
		}
		return dialog;
	}
	/**
	 * ��ȡ�������룬���޷���ȡ���򷵻ؿ��ַ���""
	 * @param context
	 * @return ��ȡ�����ֻ����룬���߿��ַ���
	 */
	public static String getPhoneNumber(Context context) {
		String phone = null;
		TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		phone = manager.getLine1Number();
		Log.d("��������", "ProjectConstants#getPhoneNumber, �������룺" + phone);
		return phone == null ? "" : phone;
	}
}
