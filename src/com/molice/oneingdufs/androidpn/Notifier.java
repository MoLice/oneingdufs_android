/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.molice.oneingdufs.androidpn;

import java.util.Random;

import org.json.JSONObject;

import com.molice.oneingdufs.activities.MessageDetailActivity;
import com.molice.oneingdufs.activities.SettingsActivity;
import com.molice.oneingdufs.utils.SharedPreferencesStorager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/** 
 * This class is to notify the user of messages with NotificationManager.<br/>
 * 由{@link NotificationReceiver#onReceive(Context, Intent) onReceive}调用，将收到的消息发送为android notification
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class Notifier {

    private static final String LOGTAG = LogUtil.makeLogTag(Notifier.class);

    private static final Random random = new Random(System.currentTimeMillis());

    private Context context;

    private SharedPreferencesStorager sharedPrefs;

    private NotificationManager notificationManager;

    public Notifier(Context context) {
        this.context = context;
        this.sharedPrefs = new SharedPreferencesStorager(context);
        this.notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void notify(String notificationId, String apiKey, String title,
            String message, String uri) {
        Log.d(LOGTAG, "notify()...");

        Log.d(LOGTAG, "notificationId=" + notificationId);
        Log.d(LOGTAG, "notificationApiKey=" + apiKey);
        Log.d(LOGTAG, "notificationTitle=" + title);
        Log.d(LOGTAG, "notificationMessage=" + message);
        Log.d(LOGTAG, "notificationUri=" + uri);

        if (SettingsActivity.getNotificationEnabled(context)) {
            // Show the toast
//            if (isNotificationToastEnabled()) {
//                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
//            }

            // Notification
            Notification notification = new Notification();
            notification.icon = getNotificationIcon();
//            notification.defaults = Notification.DEFAULT_LIGHTS;
            if (SettingsActivity.getNotificationSound(context)) {
                notification.defaults |= Notification.DEFAULT_SOUND;
            }
            if (SettingsActivity.getNotificationVibrate(context)) {
                notification.defaults |= Notification.DEFAULT_VIBRATE;
            }
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.when = System.currentTimeMillis();
            notification.tickerText = message;

            //            Intent intent;
            //            if (uri != null
            //                    && uri.length() > 0
            //                    && (uri.startsWith("http:") || uri.startsWith("https:")
            //                            || uri.startsWith("tel:") || uri.startsWith("geo:"))) {
            //                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            //            } else {
            //                String callbackActivityPackageName = sharedPrefs.getString(
            //                        Constants.CALLBACK_ACTIVITY_PACKAGE_NAME, "");
            //                String callbackActivityClassName = sharedPrefs.getString(
            //                        Constants.CALLBACK_ACTIVITY_CLASS_NAME, "");
            //                intent = new Intent().setClassName(callbackActivityPackageName,
            //                        callbackActivityClassName);
            //                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            //            }

            Intent intent = new Intent(context,
                    MessageDetailActivity.class);
            JSONObject data = formatMetaFromTitle(title);
            try {
				data.putOpt("id", notificationId);
				data.putOpt("content", message);
			} catch (Exception e) {
				Log.d("JSON异常", "Notifier#notify, e=" + e.toString());
			}
            intent.putExtra("data", data.toString());
            intent.putExtra("fromNotification", true);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);

            notification.setLatestEventInfo(context, data.optString("title"), message,
                    contentIntent);
            notificationManager.notify(random.nextInt(), notification);

            //            Intent clickIntent = new Intent(
            //                    Constants.ACTION_NOTIFICATION_CLICKED);
            //            clickIntent.putExtra(Constants.NOTIFICATION_ID, notificationId);
            //            clickIntent.putExtra(Constants.NOTIFICATION_API_KEY, apiKey);
            //            clickIntent.putExtra(Constants.NOTIFICATION_TITLE, title);
            //            clickIntent.putExtra(Constants.NOTIFICATION_MESSAGE, message);
            //            clickIntent.putExtra(Constants.NOTIFICATION_URI, uri);
            //            //        positiveIntent.setData(Uri.parse((new StringBuilder(
            //            //                "notif://notification.adroidpn.org/")).append(apiKey).append(
            //            //                "/").append(System.currentTimeMillis()).toString()));
            //            PendingIntent clickPendingIntent = PendingIntent.getBroadcast(
            //                    context, 0, clickIntent, 0);
            //
            //            notification.setLatestEventInfo(context, title, message,
            //                    clickPendingIntent);
            //
            //            Intent clearIntent = new Intent(
            //                    Constants.ACTION_NOTIFICATION_CLEARED);
            //            clearIntent.putExtra(Constants.NOTIFICATION_ID, notificationId);
            //            clearIntent.putExtra(Constants.NOTIFICATION_API_KEY, apiKey);
            //            //        negativeIntent.setData(Uri.parse((new StringBuilder(
            //            //                "notif://notification.adroidpn.org/")).append(apiKey).append(
            //            //                "/").append(System.currentTimeMillis()).toString()));
            //            PendingIntent clearPendingIntent = PendingIntent.getBroadcast(
            //                    context, 0, clearIntent, 0);
            //            notification.deleteIntent = clearPendingIntent;
            //
            //            notificationManager.notify(random.nextInt(), notification);

        } else {
            Log.w(LOGTAG, "Notificaitons disabled.");
        }
    }

    private int getNotificationIcon() {
        return sharedPrefs.get(Constants.NOTIFICATION_ICON, 0);
    }
    
    /**
	 * 消息的META数据被存放在title字段内，从title字段中提取key-value并存储为JSONObject对象<br/>
	 * <pre>{
	 * "title": "",// 消息标题
	 * "from": "",// 发送方昵称
	 * "date": "",// 发送时间
	 * "type": "msg|no",// 消息类型[消息|通知]
	 * }</pre>
	 * @param title 从Notification获取的title值，示例："title=打个招呼;from=MoLice;date=2012-4-24 22:57;type=msg;"
	 * @return JSONObject对象，数据结构如上
	 */
	public JSONObject formatMetaFromTitle(String title) {
		JSONObject data = new JSONObject();
		String[] meta = title.split(";");
		for(int i=0; i<meta.length; i++) {
			String[] kv = meta[i].split("=");
			try {
				data.putOpt(kv[0], kv[1]);
			} catch (Exception e) {
				Log.d("JSON异常", "MessageDetailActivity#formatMetaFromTitle, e=" + e.toString());
			}
		}
		return data;
	}
}
