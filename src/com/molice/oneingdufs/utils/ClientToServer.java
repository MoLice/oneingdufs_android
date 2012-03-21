package com.molice.oneingdufs.utils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.molice.oneingdufs.interfaces.IClientToServer;
import com.molice.oneingdufs.interfaces.OnHttpRequestListener;

/**
 * ����ͻ��˺ͷ��������ͨѶ���࣬�������õ�get��post����<br />
 * HTTPĬ�ϵ�����Ҳ���������У����������url������ַ�����ӳ�ʱ���á�Httpͷ����Ϣ��<br />
 * get/post���������Զ���Cookieͷ�������csrftoken��sessionid(�����sessionid�Ļ�)<br />
 * <strong>����������á��������ݵĴ��������¼��������ڽ��У���˱����ȵ���setOnRequestListener()���ü������ٽ���http����</strong>
 * @author MoLice
 */
public class ClientToServer implements IClientToServer {
	
	public final static String URL_SERVER = "http://10.0.2.2:8000/api";
	public final static String URL_LOGIN = "/home/login/";
	public final static String URL_REGISTER = "/home/register/";
	public final static String URL_LOGOUT = "/home/logout/";
	
	public DefaultHttpClient client;
	private HttpParams httpParams;
	private OnHttpRequestListener requestListener;
	private SharedPreferencesStorager storager;
	private String csrftoken;
	
	/**
	 * @param context ��ΪҪ����SharedPreferencesStorager����������Ҫ����Context
	 */
	public ClientToServer(Context context) {
		this.client = new DefaultHttpClient();
		this.requestListener = null;
		this.httpParams = new BasicHttpParams();
		this.storager = new SharedPreferencesStorager(context);
		// �������Ӳ���
		HttpConnectionParams.setConnectionTimeout(httpParams, 10*1000);
		HttpConnectionParams.setSoTimeout(httpParams, 20 * 1000);
		HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);
		HttpProtocolParams.setUserAgent(httpParams, this.storager.get("baseInfo", "app_version=OneInGDUFS"));
		
		this.csrftoken = getCsrfToken();
	}
	
	/**
	 * ΪHTTP�����ʽ��Ҫ�����Ĳ�����������JSON���ݷ���data�ֶ���
	 * �����get�����򷵻�Stringʵ��
	 * �����post�����򷵻�UrlEncodedFormEntityʵ�壬utf-8����
	 * @param type
	 * @param data
	 * @return
	 */
	private Object requestDataFormatter(String type, JSONObject data) {
		if(type == "get") {
			return "?data=" + URLEncoder.encode(data.toString());
		} else {
			List<NameValuePair> result = new ArrayList<NameValuePair>();
			result.add(new BasicNameValuePair("data", data.toString()));
			try {
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(result, HTTP.UTF_8);
				return entity;
			} catch (Exception e) {
			}
		}
		return null;
	}
	
	/**
	 * ��beforeSend()����ã���鱾�δ洢������ͷ����������Ҫ���sessionid��csrftoken��Cookie�С����ͷ���ͱ��ش洢������ͬһ�ֶΣ���ͷ����ֵ����Ȩ����
	 * @param method HttpGet��HttpPost
	 */
	private void checkCookie(HttpRequestBase method) {
		// �������ͷ���Ѿ���beforeSend�ﱻ�����Cookie�ˣ������Cookie���Ƿ����sessionid��csrftoken
		if(method.containsHeader("Cookie")) {
			String cookie = method.getHeaders("Cookie")[0].getValue();
			if(cookie.indexOf("csrftoken") == -1) {
				// ������Cookie��û���csrftoken����csrftoken�����ȥ
				cookie = cookie + "csrftoken=" + csrftoken + ";";
				method.setHeader("X-CSRFToken", csrftoken);
			}
			if(storager.isExist("sessionid") && cookie.indexOf("sessionid") == -1) {
				// ������ش洢�����sessionid����beforeSend����ӵ�Cookie������sessionid����sessionid��ӵ�Cookie��
				cookie = cookie + "sessionid=" + storager.get("sessionid", "") + ";";
			}
			method.setHeader("Cookie", cookie);
		} else {
			// ��beforeSend�ﲢû���������Cookie���򽫱��ش洢���sessionid��csrftoken��ӵ�Cookie
			String cookie = "csrftoken=" + csrftoken + ";";
			method.setHeader("X-CSRFToken", csrftoken);
			if(storager.isExist("sessionid")) {
				cookie = cookie + "sessionid=" + storager.get("sessionid", "") + ";";
			}
			method.setHeader("Cookie", cookie);
		}
	}
	
	@Override
	public void get(String url, JSONObject data, int requestCode) {
		// �������Ե�ַ���Զ����������
		if(url.length() < 4 || url.substring(0, 4) != "http") {
			url = ClientToServer.URL_SERVER + url;
		}
		// �����Ҫ���Σ��򽫲�����ӵ�urlβ��
		if(data != null) {
			url = url + requestDataFormatter("get", data);
		}
		HttpGet httpGet = new HttpGet(url);
		httpGet.setParams(httpParams);
		// ����ǰ���������ò���
		if(requestListener != null) {
			requestListener.beforeSend(requestCode, this, httpGet, client);
		}
		
		checkCookie(httpGet);
		
		try {
			HttpResponse response = client.execute(httpGet);
			if(response.getStatusLine().getStatusCode() == 200) {
				// ��ȡ���������ص�����
				HttpEntity entity = response.getEntity();
				String result = null;
				if(entity != null) {
					result = EntityUtils.toString(entity);
				}
				// ����ɹ��������¼���������
				if(requestListener != null) {
					requestListener.onSuccess(requestCode, this, httpGet, response, new JSONObject(result));
				}
			} else {
				// ����ʧ�ܣ������¼���������
				if(requestListener != null) {
					requestListener.onFailed(requestCode, this, httpGet, response);
				}
			}
			// ��������������¼���������
			if(requestListener != null) {
				requestListener.afterSend(requestCode, this, httpGet, client);
			}
		} catch (ConnectTimeoutException e) {
			// ��ʱ������ʧ�ܣ������¼���������
			if(requestListener != null) {
				requestListener.onTimeout(requestCode, this, httpGet, e);
			}
		} catch (Exception e) {
			// �����쳣������ʧ�ܣ������¼���������
			if(requestListener != null) {
				requestListener.onException(requestCode, this, httpGet, e);
			}
		} finally {
			// ��������������¼���������
			if(requestListener != null) {
				requestListener.afterSend(requestCode, this, httpGet, client);
			}
		}
	}

	@Override
	public void post(String url, JSONObject data, int requestCode) {
		// �������Ե�ַ���Զ����������
		if(url.length() < 4 || url.substring(0, 4) != "http") {
			url = ClientToServer.URL_SERVER + url;
		}
		HttpPost httpPost = new HttpPost(url);
		UrlEncodedFormEntity postData = (UrlEncodedFormEntity) requestDataFormatter("post", data);
		httpPost.setHeader("Content-Type", "application/json");
		// TODO �˽⾿��ΪʲôֻҪ����Content-Length�ͻ��׳�clientprotocolexception�쳣��Э�����
		//httpPost.setHeader("Content-Length", String.valueOf(postData.getContentLength()));
		
		if(requestListener != null) {
			requestListener.beforeSend(requestCode, this, httpPost, client);
		}
		
		checkCookie(httpPost);
		
		httpPost.setParams(httpParams);
		httpPost.setEntity(postData);
		try {
			HttpResponse response = client.execute(httpPost);
			if(response.getStatusLine().getStatusCode() == 200) {
				// ��ȡ���������ص�����
				HttpEntity entity = response.getEntity();
				String result = null;
				if(entity != null) {
					result = EntityUtils.toString(entity);
				}
				// ����ɹ��������¼���������
				if(requestListener != null) {
					requestListener.onSuccess(requestCode, this, httpPost, response, new JSONObject(result));
				}
			} else {
				if(requestListener != null) {
					requestListener.onFailed(requestCode, this, httpPost, response);
				}
			}
		} catch (ConnectTimeoutException e) {
			// ��ʱ������ʧ�ܣ������¼���������
			if(requestListener != null) {
				requestListener.onTimeout(requestCode, this, httpPost, e);
			}
		} catch (Exception e) {
			// �����쳣������ʧ�ܣ������¼���������
			if(requestListener != null) {
				requestListener.onException(requestCode, this, httpPost, e);
			}
		} finally {
			// ��������������¼���������
			if(requestListener != null) {
				requestListener.afterSend(requestCode, this, httpPost, client);
			}
		}
	}

	@Override
	public void setOnRequestListener(OnHttpRequestListener listener) {
		this.requestListener = listener;
	}

	@Override
	public JSONObject cookiesToJSON(String cookies) {
		if(cookies != null && cookies != "") {
			// �洢��ʽ�����cookie
			JSONObject cookie_json = new JSONObject();
		
			String[] cookie_array = cookies.split(";");
			for(String cookie : cookie_array) {
				cookie = cookie.trim();
				int beginIndex = cookie.indexOf("=");
				try {
					cookie_json.put(cookie.substring(0, beginIndex), cookie.substring(beginIndex + 1, cookie.length()));
				} catch (Exception e) {
					Log.d("JSON����", e.toString());
				}
			}
			return cookie_json;
		}
		return null;
	}
	
	@Override
	public String getCsrfToken() {
		if(!storager.isExist("csrftoken")) {
			HttpGet httpGet = new HttpGet(ClientToServer.URL_SERVER + "/api/getcsrftoken/");
			try {
				HttpResponse response = client.execute(httpGet);
				if(response.getStatusLine().getStatusCode() == 200) {
					csrftoken = cookiesToJSON(response.getHeaders("Set-Cookie")[0].getValue()).optString("csrftoken", "");
					storager.set("csrftoken", csrftoken).save();
				} else {
					return "";
				}
			} catch (Exception e) {
				return "";
			}
		}

		return storager.get("csrftoken", "");
	}
}
