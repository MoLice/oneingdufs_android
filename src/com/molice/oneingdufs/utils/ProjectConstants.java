package com.molice.oneingdufs.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.os.Build;
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
	//public final static String URL_HOST = "http://oneingdufs.sinaapp.com/api";
	public final static String URL_HOST = "http://10.0.2.2:8000/api";
	//public final static String URL_HOST = "http://192.168.0.11:8000/api";
	// global ȫ��
	/** ��ȡcsrftoken*/
	public final static String URL_GETCSRFTOKEN = "/getcsrftoken/";
	/** ������Ϣ������һ���û�*/
	public final static String URL_SENDNOTIFICATION = "/sendnotification/";
	/** ����XMPP�û�����Ӧ�÷��������ڵ�¼������ʹ�ã���Ҫ����Ϊ���������������*/
	public final static String URL_UPDATEAPNUSERNAME = "/updateapnusername/";
	public final static String IMAGE_PATH = "image/*";
	// home �û�����
	/** ��¼*/
	public final static String URL_LOGIN = "/home/login/";
	/** ע��*/
	public final static String URL_REGISTER = "/home/register/";
	/** �˳���½*/
	public final static String URL_LOGOUT = "/home/logout/";
	
	// ��У����
	public final static String URL_LIFE_ROOMADDRESS = "/life/roomaddress/";
	public final static String URL_LIFE_CARD = "/life/card/";
	
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
	public static String getPhone(Context context) {
		String phone = null;
		TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		phone = manager.getLine1Number();
		Log.d("��������", "ProjectConstants#getPhone, �������룺" + phone);
		return phone == null ? "" : phone;
	}
	/**
	 * �����ش洢û��phone number�������{@link #getPhone(Context)}������洢����
	 * @param context
	 * @param storager
	 */
	public static void setPhone(Context context, SharedPreferencesStorager storager) {
        // ��������ڱ�������phoneNumber�����»�ȡ���洢
		Log.d("��������ֵ", ProjectConstants.getPhone(context));
        if(!storager.has("user_info_phone")) {
        	storager.set("user_info_phone", ProjectConstants.getPhone(context)).save();
        }
	}
	/**
	 * ��Ӧ�ð汾��Androidϵͳ�汾����Ļ�ֱ��ʵ���Ϣ�洢����
	 * @param context
	 */
	public static void setBaseInfo(Context context) {
		SharedPreferencesStorager storager = new SharedPreferencesStorager(context);
        try {
        	StringBuilder baseInfo = new StringBuilder(storager.get("baseInfo", ""));
        	// ���������baseInfo�����ߴ��ڵ�Ӧ�ð汾�͵�ǰӦ�ð汾��һ�£�����������Ӧ�ð汾�ˣ��������´洢
            if(baseInfo.length() == 0 || !baseInfo.substring(baseInfo.indexOf("app_version=OneInGDUFS/") + 23, baseInfo.indexOf("app_version=OneInGDUFS/") + 27).equals(context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS).versionName)) {
            	baseInfo.append("app_version=OneInGDUFS/").append(context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS).versionName)
            		.append(";system_version=Android/").append(Build.VERSION.RELEASE)
            		.append(";sdk=").append(Build.VERSION.SDK)
            		.append(";display=width:").append(((Activity) context).getWindowManager().getDefaultDisplay().getWidth()).append(",height:").append(((Activity) context).getWindowManager().getDefaultDisplay().getHeight()).append(";");
            	storager.set("baseInfo", baseInfo.toString()).save();
            }
		} catch (Exception e) {
			Log.d("BaseInfo�洢����", e.toString());
		}
	}
	/**
	 * ��dipֵת��Ϊpxֵ
	 * @param context
	 * @param dip
	 * @return
	 */
	public static int getPxFromDip(Context context, int dip) {
		int densityDpi = context.getResources().getDisplayMetrics().densityDpi;
		Log.d("��λ����","ProjectConstants#getPxFromDip, dip=" + String.valueOf(dip) + ", px=" + String.valueOf(dip * densityDpi / 160));
		return (int) (dip * densityDpi / 160);
	}
}
