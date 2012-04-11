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
 * 用于存储项目常量，包括url
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-3-24
 */
public class ProjectConstants {
	// url，除了URL_DOMAIN，其他地址开头结尾均要添加斜杠/
	//public final static String URL_HOST = "http://oneingdufs.sinaapp.com/api";
	public final static String URL_HOST = "http://10.0.2.2:8000/api";
	//public final static String URL_HOST = "http://192.168.0.11:8000/api";
	// global 全局
	/** 获取csrftoken*/
	public final static String URL_GETCSRFTOKEN = "/getcsrftoken/";
	/** 发送消息到另外一个用户*/
	public final static String URL_SENDNOTIFICATION = "/sendnotification/";
	/** 更新XMPP用户名到应用服务器，在登录方法中使用，主要是因为会有重连情况出现*/
	public final static String URL_UPDATEAPNUSERNAME = "/updateapnusername/";
	public final static String IMAGE_PATH = "image/*";
	// home 用户中心
	/** 登录*/
	public final static String URL_LOGIN = "/home/login/";
	/** 注册*/
	public final static String URL_REGISTER = "/home/register/";
	/** 退出登陆*/
	public final static String URL_LOGOUT = "/home/logout/";
	
	// 在校生活
	public final static String URL_LIFE_ROOMADDRESS = "/life/roomaddress/";
	public final static String URL_LIFE_CARD = "/life/card/";
	
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
		Log.d("单位换算","ProjectConstants#getPxFromDip, dip=" + String.valueOf(dip) + ", px=" + String.valueOf(dip * densityDpi / 160));
		return (int) (dip * densityDpi / 160);
	}
}
