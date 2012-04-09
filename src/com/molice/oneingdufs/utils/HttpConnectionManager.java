package com.molice.oneingdufs.utils;

import java.util.ArrayList;

/**
 * Http������п�����
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-4-9
 */
public class HttpConnectionManager {
	public static final int MAX_CONNECTIONS = 5;
	
	private ArrayList<Runnable> active = new ArrayList<Runnable>();
	private ArrayList<Runnable> queue = new ArrayList<Runnable>();
	
	private static HttpConnectionManager instance;
	
	/**
	 * ��ȡ{@link HttpConnectionManager}ʵ��<br/>
	 * ʹ�ô˷�ʽ��ȷ��ȫ��ֻ��һ��{@link HttpConnectionManager}ʵ��
	 * @return {@link HttpConnectionManager}ʵ��
	 */
	public static HttpConnectionManager getInstance() {
		if(instance == null)
			instance = new HttpConnectionManager();
		return instance;
	}
	
	/**
	 * ����ǰ{@link HttpConnectionUtils}ʵ������������У��������а��������������������ʲô������������Ϊ��ǰ������е�һ���һ���̲߳�����
	 * @param runnable {@link HttpConnectionUtils}ʵ��
	 */
	public void push(Runnable runnable) {
		queue.add(runnable);
		if(active.size() < MAX_CONNECTIONS)
			startNext();
	}
	
	private void startNext() {
		if(!queue.isEmpty()) {
			Runnable next = queue.get(0);
			queue.remove(0);
			active.add(next);
			
			Thread thread = new Thread(next);
			thread.start();
		}
	}
	
	/**
	 * �������ʱ�ӻ�������Ƴ�������
	 * @param runnable {@link HttpConnectionUtils}ʵ��
	 */
	public void didComplete(Runnable runnable) {
		active.remove(runnable);
		startNext();
	}
}
