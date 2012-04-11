package com.molice.oneingdufs.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.molice.oneingdufs.interfaces.IDataStorager;

/**
 * ��Ӧ���ڲ�ʹ�õ�SharedPreferences�����࣬�ṩ�����Ķ�д���ܣ����ڱ���ÿ�ζ��趨SharedPreferences�ļ���
 */
public class SharedPreferencesStorager implements IDataStorager {
	// SharedPreferencesʵ��
	public SharedPreferences preferences;
	// SharedPreferences�༭��
	private Editor editor;
	// �趨SharedPreferences�ļ���
	private final String FILENAME = "oneingdufs_storager";
	
	public SharedPreferencesStorager(Context context) {
		preferences = context.getSharedPreferences(FILENAME, 0);
		editor = preferences.edit();
	}
	
	@Override
	public String get(String key, String defValue) {
		if(preferences.contains(key)) {
			return preferences.getString(key, defValue);
		}
		return defValue;
	}
	@Override
	public Boolean get(String key, Boolean defValue) {
		if(preferences.contains(key)) {
			return preferences.getBoolean(key, defValue);
		}
		return defValue;
	}
	@Override
	public int get(String key, int defValue) {
		if(preferences.contains(key)) {
			return preferences.getInt(key, defValue);
		}
		return defValue;
	}
	
	@Override
	public IDataStorager set(String key, String value) {
		editor.putString(key, value);
		return this;
	}
	@Override
	public IDataStorager set(String key, Boolean value) {
		editor.putBoolean(key, value);
		return this;
	}
	@Override
	public IDataStorager set(String key, int value) {
		editor.putInt(key, value);
		return this;
	}

	@Override
	public Boolean has(String key) {
		Log.d("SharedPreferencesStorager#isExist(), key=" + key, String.valueOf(preferences.contains(key)));
		return preferences.contains(key);
	}

	@Override
	public void save() {
		editor.commit();
	}

	@Override
	public IDataStorager del(String key) {
		if(preferences.contains(key)) {
			editor.remove(key);
		}
		Log.d("SharedPreferencesStorager#del,contains(" + key + ")", String.valueOf(preferences.contains(key)));
		return this;
	}
}
