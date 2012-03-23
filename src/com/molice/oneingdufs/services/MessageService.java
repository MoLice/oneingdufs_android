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
	 
    //��ȡ��Ϣ�߳�
    private MessageThread messageThread = null;
 
    //����鿴
    private Intent messageIntent = null;
    private PendingIntent messagePendingIntent = null;
 
    //֪ͨ����Ϣ
    private int messageNotificationID = 1000;
    private Notification messageNotification = null;
    private NotificationManager messageNotificatioManager = null;
 
    public IBinder onBind(Intent intent) {
        return null;
    }
 
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //��ʼ��
        messageNotification = new Notification();
        messageNotification.icon = R.drawable.ic_launcher;
        messageNotification.tickerText = "����Ϣ";
        messageNotification.defaults = Notification.DEFAULT_SOUND;
        messageNotificatioManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
 
        messageIntent = new Intent(this, LifeActivity.class);
        messagePendingIntent = PendingIntent.getActivity(this,0,messageIntent,0);
 
        //�����߳�
        messageThread = new MessageThread();
        messageThread.isRunning = true;
        messageThread.start();
 
        return super.onStartCommand(intent, flags, startId); 
    }
     
    /**
     * �ӷ������˻�ȡ��Ϣ
     *
     */
    class MessageThread extends Thread{
        //����״̬����һ�����д���
        public boolean isRunning = true;
        public void run() {
            while(isRunning){
                try {
                    //��Ϣ10����
                    Thread.sleep(10000);
                    //��ȡ��������Ϣ
                    String serverMessage = getServerMessage();
                    if(serverMessage!=null&&!"".equals(serverMessage)){
                        //����֪ͨ��
                        messageNotification.setLatestEventInfo(MessageService.this,"����Ϣ","�°�������,�������ֵܹ���!"+serverMessage,messagePendingIntent);
                        messageNotificatioManager.notify(messageNotificationID, messageNotification);
                        //ÿ��֪ͨ�֪꣬ͨID����һ�£�������Ϣ���ǵ�
                        messageNotificationID++;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
 
    /**
     * �����Դ˷���Ϊ������Demo������ʾ��
     * @return ���ط�����Ҫ���͵���Ϣ���������Ϊ�յĻ���������
     */
    public String getServerMessage(){
        return "YES!";
    }
}