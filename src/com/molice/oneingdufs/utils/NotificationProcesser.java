package com.molice.oneingdufs.utils;

import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.molice.oneingdufs.activities.MainActivity;
import com.molice.oneingdufs.activities.SettingsActivity;
import com.molice.oneingdufs.androidpn.Constants;
import com.molice.oneingdufs.androidpn.NotificationReceiver;

/**
 * 被{@link NotificationReceiver}调用，用于处理由服务端发来的消息，进行显示并设置响应动作
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-4-15
 */
public class NotificationProcesser {
//	private Context context;
//	private SharedPreferencesStorager storager;
//	private NotificationManager notificationManager;
//	
//	private static final Random RANDOM = new Random(System.currentTimeMillis());
//	
//	public NotificationProcesser(Context context) {
//		this.context = context;
//		this.storager = new SharedPreferencesStorager(context);
//		this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//	}
//	public void process(String notificationId, String apiKey, String title, String message, String uri) {
//		if(SettingsActivity.getNotificationEnabled(context)) {
//			Notification notification = new Notification();
//			notification.icon = getNotificationIcon();
//			if(SettingsActivity.getNotificationEnabled(context)) {
//				if(SettingsActivity.getNotificationVibrate(context)) {
//					notification.defaults |= Notification.DEFAULT_VIBRATE;
//				}
//				if(SettingsActivity.getNotificationSound(context)) {
//					notification.defaults |= Notification.DEFAULT_SOUND;
//				}
//			}
//			notification.flags |= Notification.FLAG_AUTO_CANCEL;
//			notification.when = System.currentTimeMillis();
//			notification.tickerText = message;
//			
//			Intent intent = new Intent(context, MainActivity.class);
//			intent.putExtra(Constants.NOTIFICATION_ID, notificationId);
//			intent.putExtra(Constants.NOTIFICATION_API_KEY, apiKey);
//            intent.putExtra(Constants.NOTIFICATION_TITLE, title);
//            intent.putExtra(Constants.NOTIFICATION_MESSAGE, message);
//            intent.putExtra(Constants.NOTIFICATION_URI, uri);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            // 启动响应视图 
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            notification.setLatestEventInfo(context, title, message, pendingIntent);
//            notificationManager.notify(RANDOM.nextInt(), notification);
//		}
//	}
//	private int getNotificationIcon() {
//		return storager.get(Constants.NOTIFICATION_ICON, 0);
//	}
}
