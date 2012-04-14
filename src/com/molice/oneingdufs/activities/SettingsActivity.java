package com.molice.oneingdufs.activities;

import com.molice.oneingdufs.R;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

/**
 * 软件设置视图<br/>
 * R.xml.settings
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-4-12
 */
public class SettingsActivity extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		
		// 根据选择的平台的不同，显示不同的提示文字
		setHostUrlSummaryByValue(findPreference("settings_debug_host"), SettingsActivity.getHostUrl(this));
	}
	
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		// 在这里监听选项的点击事件
		Log.d("测试", "SettingsActivity#onPreferenceTreeClick, preferenceScreen=" + preferenceScreen.toString() + ", preference=" + preference.getKey());
		
		// 根据选择的平台不同，改变说明文字
		if(preference.getKey().equals("settings_debug_host")) {
			preference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					setHostUrlSummaryByValue(preference, String.valueOf(newValue));
					return true;
				}
			});
		}
		return false;
	}
	
	/**
	 * 根据当前值来改变显示的提示文字
	 * @param host
	 * @param value
	 */
	private void setHostUrlSummaryByValue(Preference host, String value) {
		if(value.contains("10.0.2.2")) {
			// 模拟器
			host.setSummary("连接到10.0.2.2:8000");
		} else if(value.contains("192.168.0.11")) {
			// 真机（局域网）
			host.setSummary("连接到192.168.0.11:8000");
		} else if(value.contains("sinaapp")) {
			// 真机（公网）
			host.setSummary("连接到oneingdufs.sinaapp.com");
		}
	}
	
	@Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }
	
	public static boolean getNotification(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("settings_notification", false);
	}
	
	public static String getHostUrl(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString("settings_debug_host", "http://10.0.2.2:8000/api");
	}
}
