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
 * ���������ͼ<br/>
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
		// ����ѡ���ƽ̨�Ĳ�ͬ����ʾ��ͬ����ʾ����
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
		// �Ƿ����֪ͨ
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
//	 * ���ݵ�ǰֵ���ı���ʾ����ʾ����
//	 * @param host
//	 * @param value
//	 */
//	private boolean onHostSettingChange(Preference host, String value) {
//		if(value.contains("10.0.2.2")) {
//			// ģ����
//			host.setSummary("���ӵ�10.0.2.2:8000");
//		} else if(value.contains("192.168.0.11")) {
//			// �������������
//			host.setSummary("���ӵ�192.168.0.11:8000");
//		} else if(value.contains("sinaapp")) {
//			// �����������
//			host.setSummary("���ӵ�oneingdufs.sinaapp.com");
//		}
//		return true;
//	}
	
	@Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }
	
	/**
	 * ��ȡ���Ƿ����֪ͨ��������ֵ
	 * @param context
	 * @return ����֪ͨ�򷵻�true������false
	 */
	public static boolean getNotificationEnabled(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("settings_notification_enabled", false);
	}
	
	/**
	 * ��ȡ���յ���Ϣʱ�Ƿ������ѡ�
	 * @param context
	 * @return
	 */
	public static boolean getNotificationVibrate(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("settings_notification_vibrate", true);
	}
	
	/**
	 * ��ȡ���յ���Ϣʱ�Ƿ񲥷�������
	 * @param context
	 * @return
	 */
	public static boolean getNotificationSound(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("settings_notification_sound", false);
	}
	
	/**
	 * ��ȡ�������ӵ�����
	 * @param context
	 * @return http://?/api��Ĭ��ֵhttp://10.0.2.2:8000/api
	 */
	public static String getHostUrl(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString("settings_debug_host", "http://10.0.2.2:8000/api");
	}
	/**
	 * ��ȡ��Ϣ���ͷ�������������
	 * @param context
	 * @return ������http://Э��ͷ��Ҳ�������˿ںš�Ĭ��ֵ"10.0.2.2"
	 */
	public static String getApnHostUrl(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString("settings_debug_apn", "10.0.2.2");
	}
}