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
 * 用于存储项目常量，包括url
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-3-24
 */
public class ProjectConstants {
	public final static String IMAGE_PATH = "image/*";
	
	// 公共消息-校历
	public static final class COMMON {
		public final static int CALENDAR_STARTYEAR = 2012;
		public final static int CALENDAR_STARTMONTH = 1;
		public final static int CALENDAR_STARTDAY = 26;
		public final static int CALENDAR_ENDYEAR = 2012;
		public final static int CALENDAR_ENDMONTH = 6;
		public final static int CALENDAR_ENDDAY = 14;
		public final static String CALENDAR_IMPORTANTDAY = "{\"2\":{\"26\":\"学生报到\",\"27\":\"正式上课\",\"details\":\"2月26日学生报到，2月27日开始上课。\"},\"3\":{\"8\":\"妇女节，下午停课\",\"details\":\"3月8日下午停课，女教工放假半天。\"},\"4\":{\"2\":\"2-4日清明节放假\",\"3\":\"2-4日清明节放假\",\"4\":\"2-4日清明节放假\",\"29\":\"4月29至5月1日，五一放假\",\"30\":\"4月29至5月1日，五一放假\",\"details\":\"2至4日清明节放假调休\"},\"5\":{\"1\":\"4月29至5月1日，五一放假\",\"4\":\"五四青年节，下午停课\",\"7\":\"7-11日毕业班考试\",\"8\":\"7-11日毕业班考试\",\"9\":\"7-11日毕业班考试\",\"10\":\"7-11日毕业班考试\",\"11\":\"7-11日毕业班考试\",\"details\":\"1、4月29至5月1日劳动节放假调休。\\n2、5月4日下午停课，组织活动庆祝青年节。\\n3、5月7日至11日毕业班考试。\"},\"6\":{\"22\":\"22-24日，端午节放假\",\"23\":\"22-24日，端午节放假\",\"24\":\"22-24日，端午节放假\",\"details\":\"1、22至24日端午节放假公休。\\n2、第十八周体育课、全人通识教育选修课、通选课、辅修课期末考试。\\n3、25至28日研究生、本科生毕业典礼暨学位授予仪式。27至30日办理毕业离校手续。\"},\"7\":{\"14\":\"2012年7月14日至8月31日放假，9月2日老生报到，3日开始上课，9月9日新生报到\",\"details\":\"7月1至13日期末复习考试\"}}";
	}
	
	/**
	 * 系统使用到的url，除了host，其他地址开头结尾均要添加斜杠/
	 * 
	 * @author MoLice (sf.molice@gmail.com)
	 * @date 2012-4-12
	 */
	public static final class URL {
		// global 全局
		/** 服务器地址，可在应用设置界面里更改*/
		public static String getHost(Context context) {
			return SettingsActivity.getHostUrl(context);
		}
		/** 获取csrftoken*/
		public final static String getCsrftoken = "/getcsrftoken/";
		/** 发送消息到另外一个用户*/
		public final static String sendNotification = "/sendnotification/";
		/** 更新XMPP用户名到应用服务器，在登录方法中使用，主要是因为会有重连情况出现*/
		public final static String updateApnUsername = "/updateapnusername/";
		
		// home 用户中心
		/** 登录*/
		public final static String login = "/home/login/";
		/** 注册*/
		public final static String register = "/home/register/";
		/** 退出登陆*/
		public final static String logout = "/home/logout/";
		/** 我的资料*/
		public final static String home_info = "/home/info/";
		/** 我的团体*/
		public final static String home_group = "/home/group/";
		
		// 校园生活
		/** 更新宿舍地址*/
		public final static String life_roomAddress = "/life/roomaddress/";
		/** 获取校园卡数据*/
		public final static String life_card = "/life/card/";
		/** 后勤留言*/
		public final static String life_gdufslife = "/life/gdufslife/";
		
		// 在校学习
		/** 获取课表数据*/
		public final static String study_syllabus = "/study/syllabus/";
		
		// 消息
		public final static String message_send = "/message/";
	}
	
	public static class VALUE {
		public final static String USER_INFO_PREFIX = "user_info_";
	}
	
	// 方便调用的静态方法
	/**
	 * 弹出对话框
	 * @param context 上下文
	 * @param title 对话框标题，若为null则设置为"提醒"
	 * @param message 对话框内容，若为null则不设置
	 * @param oneButton 如果为true则显示确定按钮，如果为false则显示确定、取消按钮
	 * @return Builder对象，方便进一步处理用户操作
	 */
	public static Builder alertDialog(Context context, String title, String message, boolean oneButton) {
		Builder dialog = new AlertDialog.Builder(context);
		if(title == null)
			title = "提醒";
		if(message != null)
			dialog.setMessage(message);
		dialog.setTitle(title);
		dialog.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		if(!oneButton) {
			dialog.setNegativeButton("取消", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
		}
		return dialog;
	}
	/**
	 * 获取本机号码，若无法获取，则返回空字符串""
	 * @param context
	 * @return 获取到的手机号码，或者空字符串
	 */
	public static String getPhone(Context context) {
		String phone = null;
		TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		phone = manager.getLine1Number();
		Log.d("本机号码", "ProjectConstants#getPhone, 本机号码：" + phone);
		return phone == null ? "" : phone;
	}
	/**
	 * 若本地存储没有phone number，则调用{@link #getPhone(Context)}并将其存储起来
	 * @param context
	 * @param storager
	 */
	public static void setPhone(Context context, SharedPreferencesStorager storager) {
        // 如果不存在本机号码phoneNumber则重新获取并存储
		Log.d("函数返回值", ProjectConstants.getPhone(context));
        if(!storager.has("user_info_phone")) {
        	storager.set("user_info_phone", ProjectConstants.getPhone(context)).save();
        }
	}
	/**
	 * 将应用版本、Android系统版本、屏幕分辨率等信息存储起来
	 * @param context
	 */
	public static void setBaseInfo(Context context) {
		SharedPreferencesStorager storager = new SharedPreferencesStorager(context);
        try {
        	StringBuilder baseInfo = new StringBuilder(storager.get("baseInfo", ""));
        	// 如果不存在baseInfo，或者存在但应用版本和当前应用版本不一致（可能是升级应用版本了），则重新存储
            if(baseInfo.length() == 0 || !baseInfo.substring(baseInfo.indexOf("app_version=OneInGDUFS/") + 23, baseInfo.indexOf("app_version=OneInGDUFS/") + 27).equals(context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS).versionName)) {
            	baseInfo.append("app_version=OneInGDUFS/").append(context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS).versionName)
            		.append(";system_version=Android/").append(Build.VERSION.RELEASE)
            		.append(";sdk=").append(Build.VERSION.SDK)
            		.append(";display=width:").append(((Activity) context).getWindowManager().getDefaultDisplay().getWidth()).append(",height:").append(((Activity) context).getWindowManager().getDefaultDisplay().getHeight()).append(";");
            	storager.set("baseInfo", baseInfo.toString()).save();
            }
		} catch (Exception e) {
			Log.d("BaseInfo存储错误", e.toString());
		}
	}
	/**
	 * 将dip值转换为px值
	 * @param context
	 * @param dip
	 * @return
	 */
	public static int getPxFromDip(Context context, int dip) {
		int densityDpi = context.getResources().getDisplayMetrics().densityDpi;
		return (int) (dip * densityDpi / 160);
	}
	/**
	 * 判断某个服务是否正在运行
	 * @param context
	 * @param serviceName 服务全称，如oneingdufs.androidpn.service
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
     * 将当前手机的APN用户名更新到服务器，与当前登录用户绑定。前提是已登录且开启消息推送<br/>
     * 在登录、注册、注销时，都会更新apn_username
     * @param context
     */
    public static void updateAPNUsername(Context context) {
    	// 在Thread内再开启一个Handler，需要在方法前添加Looper.prepare()，否则会异常
    	Looper.prepare();
    	SharedPreferencesStorager sharedPrefs = new SharedPreferencesStorager(context);
    	if(sharedPrefs.get("isLogin", false) && SettingsActivity.getNotificationEnabled(context)) {
        	JSONObject data = new JSONObject();
        	try {
    			data.putOpt("username", sharedPrefs.get(Constants.XMPP_USERNAME, ""));
    		} catch (Exception e) {
    			Log.e("异常", "XmppManager.updateAPNUsername, e=" + e.toString());
    		}
        	new HttpConnectionUtils(new HttpConnectionHandler(context) {
            	@Override
            	protected void onSucceed(JSONObject result) {
            		super.onSucceed(result);
            		Log.d("更新APN用户名", "ProjectConstants#updateAPNUsername");
            	}
            }, context).post(ProjectConstants.URL.updateApnUsername, data);
    	} else {
    		Log.d("无需更新APN用户名", "ProjectConstants#updateAPNUsername，未登录或未开启消息功能");
    	}
    }
}
