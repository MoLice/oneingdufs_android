package com.molice.oneingdufs.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * ��ȡ��������״̬�����͵���
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
		// ��������,�˴���ȡ����networkInfo=null
		this.networkInfo = manager.getActiveNetworkInfo();
		Log.d("��������״̬", "NetworkStateUtils, networkInfo=" + String.valueOf(this.networkInfo));
	}
	
	public boolean isNetworkAvailable() {
		if(networkInfo != null)
			return networkInfo.isAvailable();
		return false;
	}
	
	/**
	 * ��ȡ��ǰ������������
	 * @return gprs�򷵻�{@link NetworkStateUtils#GPRS GPRS}��
	 * wifi�򷵻�{@link NetworkStateUtils#WIFI WIFI}��
	 * �����������򷵻�{@link NetworkStateUtils#NONE NONE}��
	 * �����л�����������򷵻�{@link NetworkStateUtils#OTHER OTHER}
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
	 * ��ת��������������
	 */
	public void intentWirelessSetting() {
		context.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
	}
	
	/**
	 * ��ת��wifi����
	 */
	public void intentWifiSetting() {
		context.startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
	}
}
