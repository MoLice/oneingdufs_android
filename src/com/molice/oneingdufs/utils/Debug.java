package com.molice.oneingdufs.utils;

import android.content.Context;
import android.util.Log;

public class Debug {
	public Debug() {
		
	}
	
	public static void clearAllUserData(Context context) {
		SharedPreferencesStorager storager = new SharedPreferencesStorager(context);
		storager.preferences.edit().clear().commit();
		Log.d("Debug", "clearAllUserData(), �����ǰ���ش洢��Ŀ:" + String.valueOf(storager.preferences.getAll().size()));
	}
}
