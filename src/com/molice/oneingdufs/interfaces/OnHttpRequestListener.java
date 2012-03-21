package com.molice.oneingdufs.interfaces;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;

import com.molice.oneingdufs.utils.ClientToServer;

/**
 * HTTP请求事件监听器
 * @author MoLice
 * @method beforeSend()、afterSend()、onSuccess()、onFailed、onTimeout()、onError()
 */
public interface OnHttpRequestListener {
	/**
	 * 发送请求前
	 * @param method HttpGet实例或HttpPost实例
	 */
	public void beforeSend(int requestCode, ClientToServer target, HttpRequestBase method, HttpClient client);
	/**
	 * 发送请求结束后，包括成功后及抛出异常后。<br />
	 * 常用于连接的关闭，对返回数据的处理应该放在onSuccess<br />
	 * 连接关闭方法：client.getConnectionManager().shutdown();
	 * @param method HttpGet实例或HttpPost实例
	 */
	public void afterSend(int requestCode, ClientToServer target, HttpRequestBase method, HttpClient client);
	/**
	 * 发送请求成功时，即<strong>status == 200</strong>状态
	 * @param method HttpGet实例或HttpPost实例
	 * @param response HttpResponse实例，可在此得到服务端返回的所有数据
	 * @param result 服务端返回的数据实体的字符串
	 */
	public void onSuccess(int requestCode, ClientToServer target, HttpRequestBase method, HttpResponse response, JSONObject result);
	/**
	 * 发送请求失败时，即<strong>status != 200</strong>，如404等
	 * @param method HttpGet实例或HttpPost实例
	 * @param response HttpResponse实例，可在此得到服务端返回的所有数据
	 */
	public void onFailed(int requestCode, ClientToServer target, HttpRequestBase method, HttpResponse response);
	/**
	 * 抛出连接超时异常时调用，默认连接超时为<strong>10s</strong>，Socket超时为<strong>20s</strong>
	 * @param method HttpGet实例或HttpPost实例
	 * @param e 超时异常实体
	 */
	public void onTimeout(int requestCode, ClientToServer target, HttpRequestBase method, ConnectTimeoutException e);
	/**
	 * 抛出其他异常时调用
	 * @param method HttpGet实例或HttpPost实例
	 * @param e 异常实体 
	 */
	public void onException(int requestCode, ClientToServer target, HttpRequestBase method, Exception e);
}
