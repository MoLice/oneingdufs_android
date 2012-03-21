package com.molice.oneingdufs.interfaces;

import org.json.JSONObject;

/**
 * �ͻ���������֮���HTTPͨѶ�ķ�װ�࣬�������ֳ��÷���
 * @author MoLice
 * @method get��post��setOnRequestListener��cookiesToMap��getCsrfToken
 */
public interface IClientToServer {
	/**
	 * �����˷���GET����
	 * @param url Ŀ��url��ַ������ʹ����Ե�ַ����Ե�ַ��ͷ��β��Ҫ��/�����磬��/home/login/������/home/login��home/login/
	 * @param data Ҫ���͵Ĳ�ѯ�ַ�������Map�����洢��key-value��ʽ
	 * @param requestCode ��־��������������룬������OnHttpRequestListener�����ֲ�ͬ����Դ�
	 */
	public void get(String url, JSONObject data, int requestCode);
	/**
	 * �����˷���POST����
	 * @param url Ŀ��url��ַ������ʹ����Ե�ַ����Ե�ַ��ͷ��β��Ҫ��/�����磬��/home/login/������/home/login��home/login/
	 * @param data Ҫ���͵Ĳ�ѯ�ַ�������Map�����洢��key-value��ʽ
	 * @param requestCode ��־��������������룬������OnHttpRequestListener�����ֲ�ͬ����Դ�
	 */
	public void post(String url, JSONObject data, int requestCode);
	/**
	 * ��������¼�����
	 * @param listener ��Ҫʵ�ֵ�{@link OnHttpRequestListener}�ӿ�
	 */
	public void setOnRequestListener(OnHttpRequestListener listener);
	/**
	 * ������˷��ص�cookies�ַ�����ȡ��������key-value�洢
	 * @param cookies ����˷��ص�Set-Cookie�ַ���
	 * @return �洢cookies��Map����
	 */
	public JSONObject cookiesToJSON(String cookies);
	/**
	 * ��鱾�ش洢���Ƿ���csrftoken���������ֱ����ȡ�����أ����û������GET /getcsrftoken/���ӷ���˷��ص�Set-Cookieͷ������ȡ��csrftokenֵ<br />
	 * ����ڴ˹����г����򷵻�""����˱���Է��ص�ֵ����!=""�ж�
	 * @return csrftokenֵ
	 */
	public String getCsrfToken();
}
