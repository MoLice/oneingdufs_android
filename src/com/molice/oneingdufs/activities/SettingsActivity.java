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
 * ���������ͼ<br/>
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
		
		// ����ѡ���ƽ̨�Ĳ�ͬ����ʾ��ͬ����ʾ����
		setHostUrlSummaryByValue(findPreference("settings_debug_host"), SettingsActivity.getHostUrl(this));
	}
	
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		// ���������ѡ��ĵ���¼�
		Log.d("����", "SettingsActivity#onPreferenceTreeClick, preferenceScreen=" + preferenceScreen.toString() + ", preference=" + preference.getKey());
		
		// ����ѡ���ƽ̨��ͬ���ı�˵������
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
	 * ���ݵ�ǰֵ���ı���ʾ����ʾ����
	 * @param host
	 * @param value
	 */
	private void setHostUrlSummaryByValue(Preference host, String value) {
		if(value.contains("10.0.2.2")) {
			// ģ����
			host.setSummary("���ӵ�10.0.2.2:8000");
		} else if(value.contains("192.168.0.11")) {
			// �������������
			host.setSummary("���ӵ�192.168.0.11:8000");
		} else if(value.contains("sinaapp")) {
			// �����������
			host.setSummary("���ӵ�oneingdufs.sinaapp.com");
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
