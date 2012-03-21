package com.molice.oneingdufs.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.molice.oneingdufs.interfaces.IDataStorager;

/**
 * 本应用内部使用的SharedPreferences管理类，提供基本的读写功能，用于避免每次都设定SharedPreferences文件名
 */
public class SharedPreferencesStorager implements IDataStorager {
	// SharedPreferences实例
	public SharedPreferences preferences;
	// SharedPreferences编辑器
	private Editor editor;
	// 设定SharedPreferences文件名
	private final String FILENAME = "SETTING_Infos";
	
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
	public Boolean isExist(String key) {
		return preferences.contains(key);
	}

	@Override
	public void save() {
		editor.commit();
	}
	
}
