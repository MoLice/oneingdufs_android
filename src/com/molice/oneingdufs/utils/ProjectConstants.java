package com.molice.oneingdufs.utils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
	public final static String URL_HOST = "http://oneingdufs.sinaapp.com/api";
	// global 全局
	/** 获取csrftoken*/
	public final static String URL_GETCSRFTOKEN = "/getcsrftoken/";
	/** 发送消息到另外一个用户*/
	public final static String URL_SENDNOTIFICATION = "/sendnotification/";
	/** 更新XMPP用户名到应用服务器，在登录方法中使用，主要是因为会有重连情况出现*/
	public final static String URL_UPDATEAPNUSERNAME = "/updateapnusername/";
	// home 用户中心
	/** 登录*/
	public final static String URL_LOGIN = "/home/login/";
	/** 注册*/
	public final static String URL_REGISTER = "/home/register/";
	/** 退出登陆*/
	public final static String URL_LOGOUT = "/home/logout/";
	
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
	public static String getPhoneNumber(Context context) {
		String phone = null;
		TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		phone = manager.getLine1Number();
		Log.d("本机号码", "ProjectConstants#getPhoneNumber, 本机号码：" + phone);
		return phone == null ? "" : phone;
	}
}
