package com.molice.oneingdufs.services;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.activities.LifeActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class MessageService extends Service {
	 
    //获取消息线程
    private MessageThread messageThread = null;
 
    //点击查看
    private Intent messageIntent = null;
    private PendingIntent messagePendingIntent = null;
 
    //通知栏消息
    private int messageNotificationID = 1000;
    private Notification messageNotification = null;
    private NotificationManager messageNotificatioManager = null;
 
    public IBinder onBind(Intent intent) {
        return null;
    }
 
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //初始化
        messageNotification = new Notification();
        messageNotification.icon = R.drawable.ic_launcher;
        messageNotification.tickerText = "新消息";
        messageNotification.defaults = Notification.DEFAULT_SOUND;
        messageNotificatioManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
 
        messageIntent = new Intent(this, LifeActivity.class);
        messagePendingIntent = PendingIntent.getActivity(this,0,messageIntent,0);
 
        //开启线程
        messageThread = new MessageThread();
        messageThread.isRunning = true;
        messageThread.start();
 
        return super.onStartCommand(intent, flags, startId); 
    }
     
    /**
     * 从服务器端获取消息
     *
     */
    class MessageThread extends Thread{
        //运行状态，下一步骤有大用
        public boolean isRunning = true;
        public void run() {
            while(isRunning){
                try {
                    //休息10分钟
                    Thread.sleep(10000);
                    //获取服务器消息
                    String serverMessage = getServerMessage();
                    if(serverMessage!=null&&!"".equals(serverMessage)){
                        //更新通知栏
                        messageNotification.setLatestEventInfo(MessageService.this,"新消息","奥巴马宣布,本拉登兄弟挂了!"+serverMessage,messagePendingIntent);
                        messageNotificatioManager.notify(messageNotificationID, messageNotification);
                        //每次通知完，通知ID递增一下，避免消息覆盖掉
                        messageNotificationID++;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
 
    /**
     * 这里以此方法为服务器Demo，仅作示例
     * @return 返回服务器要推送的消息，否则如果为空的话，不推送
     */
    public String getServerMessage(){
        return "YES!";
    }
}