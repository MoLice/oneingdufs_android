package com.molice.oneingdufs.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * 获取网络连接状态、类型的类
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-4-10
 */
public class NetworkStateUtils {
	public static final int GPRS = 0;
	public static final int WIFI = 1;
	public static final int OTHER = 2;
	public static final int NONE = -1;
	
	private Context context;
	private ConnectivityManager manager;
	private NetworkInfo networkInfo;
	
	public NetworkStateUtils(Context context) {
		this.context = context;
		this.manager = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
		// 若无连接,此处获取到的networkInfo=null
		this.networkInfo = manager.getActiveNetworkInfo();
		Log.d("网络连接状态", "NetworkStateUtils, networkInfo=" + String.valueOf(this.networkInfo));
	}
	
	public boolean isNetworkAvailable() {
		if(networkInfo != null)
			return networkInfo.isAvailable();
		return false;
	}
	
	/**
	 * 获取当前网络连接类型
	 * @return gprs则返回{@link NetworkStateUtils#GPRS GPRS}，
	 * wifi则返回{@link NetworkStateUtils#WIFI WIFI}，
	 * 无网络连接则返回{@link NetworkStateUtils#NONE NONE}，
	 * 连接中或其他情况，则返回{@link NetworkStateUtils#OTHER OTHER}
	 */
	public int getNetworkType() {
		if(!isNetworkAvailable())
			return NONE;
		if(networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
			return GPRS;
		if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
			return WIFI;
		return OTHER;
	}
	
	/**
	 * 跳转到无线网络设置
	 */
	public void intentWirelessSetting() {
		context.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
	}
	
	/**
	 * 跳转到wifi设置
	 */
	public void intentWifiSetting() {
		context.startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
	}
}
