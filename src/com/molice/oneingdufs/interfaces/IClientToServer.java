package com.molice.oneingdufs.interfaces;

import org.json.JSONObject;

/**
 * 客户端与服务端之间的HTTP通讯的封装类，包含各种常用方法
 * @author MoLice
 * @method get、post、setOnRequestListener、cookiesToMap、getCsrfToken
 */
public interface IClientToServer {
	/**
	 * 向服务端发送GET请求
	 * @param url 目标url地址，可以使用相对地址，相对地址开头结尾均要带/，例如，是/home/login/而不是/home/login或home/login/
	 * @param data 要发送的查询字符串，以Map容器存储的key-value形式
	 * @param requestCode 标志本次请求的请求码，便于在OnHttpRequestListener里区分不同请求对待
	 */
	public void get(String url, JSONObject data, int requestCode);
	/**
	 * 向服务端发送POST请求
	 * @param url 目标url地址，可以使用相对地址，相对地址开头结尾均要带/，例如，是/home/login/而不是/home/login或home/login/
	 * @param data 要发送的查询字符串，以Map容器存储的key-value形式
	 * @param requestCode 标志本次请求的请求码，便于在OnHttpRequestListener里区分不同请求对待
	 */
	public void post(String url, JSONObject data, int requestCode);
	/**
	 * 添加请求事件监听
	 * @param listener 需要实现的{@link OnHttpRequestListener}接口
	 */
	public void setOnRequestListener(OnHttpRequestListener listener);
	/**
	 * 将服务端返回的cookies字符串提取出来，按key-value存储
	 * @param cookies 服务端返回的Set-Cookie字符串
	 * @return 存储cookies的Map容器
	 */
	public JSONObject cookiesToJSON(String cookies);
	/**
	 * 检查本地存储中是否有csrftoken，如果有则直接提取并返回，如果没有则发送GET /getcsrftoken/，从服务端返回的Set-Cookie头部中提取出csrftoken值<br />
	 * 如果在此过程中出错，则返回""，因此必须对返回的值进行!=""判断
	 * @return csrftoken值
	 */
	public String getCsrfToken();
}
