package com.molice.oneingdufs.utils;

import java.util.ArrayList;

/**
 * Http请求队列控制器
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
	 * 获取{@link HttpConnectionManager}实例<br/>
	 * 使用此方式是确保全局只有一个{@link HttpConnectionManager}实例
	 * @return {@link HttpConnectionManager}实例
	 */
	public static HttpConnectionManager getInstance() {
		if(instance == null)
			instance = new HttpConnectionManager();
		return instance;
	}
	
	/**
	 * 将当前{@link HttpConnectionUtils}实例推入请求队列，假如活动队列包含超过最大连接数，则什么都不做，否则为当前请求队列第一项开启一个线程并运行
	 * @param runnable {@link HttpConnectionUtils}实例
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
	 * 请求结束时从活动队列中移除该请求
	 * @param runnable {@link HttpConnectionUtils}实例
	 */
	public void didComplete(Runnable runnable) {
		active.remove(runnable);
		startNext();
	}
}
