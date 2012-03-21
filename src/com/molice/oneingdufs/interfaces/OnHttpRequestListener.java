package com.molice.oneingdufs.interfaces;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;

import com.molice.oneingdufs.utils.ClientToServer;

/**
 * HTTP�����¼�������
 * @author MoLice
 * @method beforeSend()��afterSend()��onSuccess()��onFailed��onTimeout()��onError()
 */
public interface OnHttpRequestListener {
	/**
	 * ��������ǰ
	 * @param method HttpGetʵ����HttpPostʵ��
	 */
	public void beforeSend(int requestCode, ClientToServer target, HttpRequestBase method, HttpClient client);
	/**
	 * ������������󣬰����ɹ����׳��쳣��<br />
	 * ���������ӵĹرգ��Է������ݵĴ���Ӧ�÷���onSuccess<br />
	 * ���ӹرշ�����client.getConnectionManager().shutdown();
	 * @param method HttpGetʵ����HttpPostʵ��
	 */
	public void afterSend(int requestCode, ClientToServer target, HttpRequestBase method, HttpClient client);
	/**
	 * ��������ɹ�ʱ����<strong>status == 200</strong>״̬
	 * @param method HttpGetʵ����HttpPostʵ��
	 * @param response HttpResponseʵ�������ڴ˵õ�����˷��ص���������
	 * @param result ����˷��ص�����ʵ����ַ���
	 */
	public void onSuccess(int requestCode, ClientToServer target, HttpRequestBase method, HttpResponse response, JSONObject result);
	/**
	 * ��������ʧ��ʱ����<strong>status != 200</strong>����404��
	 * @param method HttpGetʵ����HttpPostʵ��
	 * @param response HttpResponseʵ�������ڴ˵õ�����˷��ص���������
	 */
	public void onFailed(int requestCode, ClientToServer target, HttpRequestBase method, HttpResponse response);
	/**
	 * �׳����ӳ�ʱ�쳣ʱ���ã�Ĭ�����ӳ�ʱΪ<strong>10s</strong>��Socket��ʱΪ<strong>20s</strong>
	 * @param method HttpGetʵ����HttpPostʵ��
	 * @param e ��ʱ�쳣ʵ��
	 */
	public void onTimeout(int requestCode, ClientToServer target, HttpRequestBase method, ConnectTimeoutException e);
	/**
	 * �׳������쳣ʱ����
	 * @param method HttpGetʵ����HttpPostʵ��
	 * @param e �쳣ʵ�� 
	 */
	public void onException(int requestCode, ClientToServer target, HttpRequestBase method, Exception e);
}
