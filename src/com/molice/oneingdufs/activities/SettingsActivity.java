package com.molice.oneingdufs.activities;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.androidpn.NotificationService;
import com.molice.oneingdufs.androidpn.ServiceManager;
import com.molice.oneingdufs.utils.ProjectConstants;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * 软件设置视图<br/>
 * R.xml.settings
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-4-12
 */
public class SettingsActivity extends PreferenceActivity {
	private Preference notification_vibrate;
	private Preference notification_sound;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		
		notification_vibrate = findPreference("settings_notification_vibrate");
		notification_sound = findPreference("settings_notification_sound");
		
		onNotificationSettingChange(SettingsActivity.getNotificationEnabled(this));
		// 根据选择的平台的不同，显示不同的提示文字
//		onHostSettingChange(findPreference("settings_debug_host"), SettingsActivity.getHostUrl(this));
		
		findPreference("settings_notification_enabled").setOnPreferenceChangeListener(changeListener);
		findPreference("settings_debug_host").setOnPreferenceChangeListener(changeListener);
	}
	
	private OnPreferenceChangeListener changeListener = new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			String key = preference.getKey();
			if(key.equals("settings_notification_enabled")) {
				return onNotificationSettingChange((Boolean) newValue);
			} else if(key.equals("settings_debug_host")) {
//				return onHostSettingChange(preference, String.valueOf(newValue));
			}
			return true;
		}
	};
	
	private boolean onNotificationSettingChange(boolean newValue) {
		// 是否接受通知
		if(newValue) {
			if(ProjectConstants.isServiceRunning(this, NotificationService.SERVICE_NAME)) {
				notification_vibrate.setEnabled(true);
				notification_sound.setEnabled(true);
				return false;
			} else {
				ServiceManager serviceManager = new ServiceManager(SettingsActivity.this);
				serviceManager.setNotificationIcon(R.drawable.icon);
				serviceManager.startService();
				notification_vibrate.setEnabled(true);
				notification_sound.setEnabled(true);
				return true;
			}
		} else {
			if(ProjectConstants.isServiceRunning(this, NotificationService.SERVICE_NAME)) {
				new ServiceManager(SettingsActivity.this).stopService();
				notification_vibrate.setEnabled(false);
				notification_sound.setEnabled(false);
				return true;
			} else {
				notification_vibrate.setEnabled(false);
				notification_sound.setEnabled(false);
				return false;
			}
		}
	}
	
//	/**
//	 * 根据当前值来改变显示的提示文字
//	 * @param host
//	 * @param value
//	 */
//	private boolean onHostSettingChange(Preference host, String value) {
//		if(value.contains("10.0.2.2")) {
//			// 模拟器
//			host.setSummary("连接到10.0.2.2:8000");
//		} else if(value.contains("192.168.0.11")) {
//			// 真机（局域网）
//			host.setSummary("连接到192.168.0.11:8000");
//		} else if(value.contains("sinaapp")) {
//			// 真机（公网）
//			host.setSummary("连接到oneingdufs.sinaapp.com");
//		}
//		return true;
//	}
	
	@Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }
	
	/**
	 * 获取“是否接收通知”的设置值
	 * @param context
	 * @return 接收通知则返回true，否则false
	 */
	public static boolean getNotificationEnabled(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("settings_notification_enabled", false);
	}
	
	/**
	 * 获取“收到消息时是否振动提醒”
	 * @param context
	 * @return
	 */
	public static boolean getNotificationVibrate(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("settings_notification_vibrate", true);
	}
	
	/**
	 * 获取“收到消息时是否播放铃声”
	 * @param context
	 * @return
	 */
	public static boolean getNotificationSound(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("settings_notification_sound", false);
	}
	
	/**
	 * 获取网络连接的域名
	 * @param context
	 * @return http://?/api，默认值http://10.0.2.2:8000/api
	 */
	public static String getHostUrl(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString("settings_debug_host", "http://10.0.2.2:8000/api");
	}
	/**
	 * 获取消息推送服务器连接域名
	 * @param context
	 * @return 不包含http://协议头，也不包含端口号。默认值"10.0.2.2"
	 */
	public static String getApnHostUrl(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString("settings_debug_apn", "10.0.2.2");
	}
}