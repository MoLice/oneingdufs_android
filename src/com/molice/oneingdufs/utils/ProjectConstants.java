package com.molice.oneingdufs.utils;

import java.util.List;

import org.json.JSONObject;

import com.molice.oneingdufs.activities.SettingsActivity;
import com.molice.oneingdufs.androidpn.Constants;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * ���ڴ洢��Ŀ����������url
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-3-24
 */
public class ProjectConstants {
	public final static String IMAGE_PATH = "image/*";
	
	// ������Ϣ-У��
	public static final class COMMON {
		public final static int CALENDAR_STARTYEAR = 2012;
		public final static int CALENDAR_STARTMONTH = 1;
		public final static int CALENDAR_STARTDAY = 26;
		public final static int CALENDAR_ENDYEAR = 2012;
		public final static int CALENDAR_ENDMONTH = 6;
		public final static int CALENDAR_ENDDAY = 14;
		public final static String CALENDAR_IMPORTANTDAY = "{\"2\":{\"26\":\"ѧ������\",\"27\":\"��ʽ�Ͽ�\",\"details\":\"2��26��ѧ��������2��27�տ�ʼ�ϿΡ�\"},\"3\":{\"8\":\"��Ů�ڣ�����ͣ��\",\"details\":\"3��8������ͣ�Σ�Ů�̹��żٰ��졣\"},\"4\":{\"2\":\"2-4�������ڷż�\",\"3\":\"2-4�������ڷż�\",\"4\":\"2-4�������ڷż�\",\"29\":\"4��29��5��1�գ���һ�ż�\",\"30\":\"4��29��5��1�գ���һ�ż�\",\"details\":\"2��4�������ڷżٵ���\"},\"5\":{\"1\":\"4��29��5��1�գ���һ�ż�\",\"4\":\"��������ڣ�����ͣ��\",\"7\":\"7-11�ձ�ҵ�࿼��\",\"8\":\"7-11�ձ�ҵ�࿼��\",\"9\":\"7-11�ձ�ҵ�࿼��\",\"10\":\"7-11�ձ�ҵ�࿼��\",\"11\":\"7-11�ձ�ҵ�࿼��\",\"details\":\"1��4��29��5��1���Ͷ��ڷżٵ��ݡ�\\n2��5��4������ͣ�Σ���֯���ף����ڡ�\\n3��5��7����11�ձ�ҵ�࿼�ԡ�\"},\"6\":{\"22\":\"22-24�գ�����ڷż�\",\"23\":\"22-24�գ�����ڷż�\",\"24\":\"22-24�գ�����ڷż�\",\"details\":\"1��22��24�ն���ڷżٹ��ݡ�\\n2����ʮ���������Ρ�ȫ��ͨʶ����ѡ�޿Ρ�ͨѡ�Ρ����޿���ĩ���ԡ�\\n3��25��28���о�������������ҵ������ѧλ������ʽ��27��30�հ����ҵ��У������\"},\"7\":{\"14\":\"2012��7��14����8��31�շż٣�9��2������������3�տ�ʼ�ϿΣ�9��9����������\",\"details\":\"7��1��13����ĩ��ϰ����\"}}";
	}
	
	/**
	 * ϵͳʹ�õ���url������host��������ַ��ͷ��β��Ҫ���б��/
	 * 
	 * @author MoLice (sf.molice@gmail.com)
	 * @date 2012-4-12
	 */
	public static final class URL {
		// global ȫ��
		/** ��������ַ������Ӧ�����ý��������*/
		public static String getHost(Context context) {
			return SettingsActivity.getHostUrl(context);
		}
		/** ��ȡcsrftoken*/
		public final static String getCsrftoken = "/getcsrftoken/";
		/** ������Ϣ������һ���û�*/
		public final static String sendNotification = "/sendnotification/";
		/** ����XMPP�û�����Ӧ�÷��������ڵ�¼������ʹ�ã���Ҫ����Ϊ���������������*/
		public final static String updateApnUsername = "/updateapnusername/";
		
		// home �û�����
		/** ��¼*/
		public final static String login = "/home/login/";
		/** ע��*/
		public final static String register = "/home/register/";
		/** �˳���½*/
		public final static String logout = "/home/logout/";
		/** �ҵ�����*/
		public final static String home_info = "/home/info/";
		/** �ҵ�����*/
		public final static String home_group = "/home/group/";
		
		// У԰����
		/** ���������ַ*/
		public final static String life_roomAddress = "/life/roomaddress/";
		/** ��ȡУ԰������*/
		public final static String life_card = "/life/card/";
		/** ��������*/
		public final static String life_gdufslife = "/life/gdufslife/";
		
		// ��Уѧϰ
		/** ��ȡ�α�����*/
		public final static String study_syllabus = "/study/syllabus/";
		
		// ��Ϣ
		public final static String message_send = "/message/";
	}
	
	public static class VALUE {
		public final static String USER_INFO_PREFIX = "user_info_";
	}
	
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
		return (int) (dip * densityDpi / 160);
	}
	/**
	 * �ж�ĳ�������Ƿ���������
	 * @param context
	 * @param serviceName ����ȫ�ƣ���oneingdufs.androidpn.service
	 * @return
	 */
	public static boolean isServiceRunning(Context context, String serviceName) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = manager.getRunningServices(30);
		int length = serviceList.size();
		if(length <= 0)
			return false;
		for(int i=0; i<length; i++) {
			if(serviceList.get(i).service.getClassName().equals(serviceName))
				return true;
		}
		return false;
	}
	
    /**
     * ����ǰ�ֻ���APN�û������µ����������뵱ǰ��¼�û��󶨡�ǰ�����ѵ�¼�ҿ�����Ϣ����<br/>
     * �ڵ�¼��ע�ᡢע��ʱ���������apn_username
     * @param context
     */
    public static void updateAPNUsername(Context context) {
    	// ��Thread���ٿ���һ��Handler����Ҫ�ڷ���ǰ���Looper.prepare()��������쳣
    	Looper.prepare();
    	SharedPreferencesStorager sharedPrefs = new SharedPreferencesStorager(context);
    	if(sharedPrefs.get("isLogin", false) && SettingsActivity.getNotificationEnabled(context)) {
        	JSONObject data = new JSONObject();
        	try {
    			data.putOpt("username", sharedPrefs.get(Constants.XMPP_USERNAME, ""));
    		} catch (Exception e) {
    			Log.e("�쳣", "XmppManager.updateAPNUsername, e=" + e.toString());
    		}
        	new HttpConnectionUtils(new HttpConnectionHandler(context) {
            	@Override
            	protected void onSucceed(JSONObject result) {
            		super.onSucceed(result);
            		Log.d("����APN�û���", "ProjectConstants#updateAPNUsername");
            	}
            }, context).post(ProjectConstants.URL.updateApnUsername, data);
    	} else {
    		Log.d("�������APN�û���", "ProjectConstants#updateAPNUsername��δ��¼��δ������Ϣ����");
    	}
    }
}
