package com.molice.oneingdufs.utils;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * ����һ��Http����<br/>
 * ʹ��{@link HttpConnectionHandler}������Ĳ�ͬ�׶ν��п���<br/>
 * ʹ��{@link HttpConnectionManager}��������н��п���
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-4-9
 */
public class HttpConnectionUtils implements Runnable {
	private static final String TAG = HttpConnectionUtils.class.getSimpleName();
	public static final int STATUS_START = 0;
	public static final int STATUS_SUCCEED = 1;
	public static final int STATUS_FAILED = 2;
	public static final int STATUS_TIMEOUT = 3;
	public static final int STATUS_ERROR = 4;
	public static final int STATUS_COMPLETE = 5;

	private static final int GET = 0;
	private static final int POST = 1;
	private static final int PUT = 2;
	private static final int DELETE = 3;
	private static final int BITMAP = 4;

	private String url;
	private int method;
	private Handler handler;
	private JSONObject data;

	private HttpClient httpClient;
	private SharedPreferencesStorager storager;
	private String csrftoken;

	public HttpConnectionUtils(Handler handler, SharedPreferencesStorager storager) {
		this.handler = handler;
		this.storager = storager;
		this.csrftoken = getCsrfToken();
	}

	public void create(int method, String url, JSONObject data) {
		Log.d(TAG, TAG + "method=" + method + ", url=" + url + ", data=" + String.valueOf(data));
		this.method = method;
		this.url = url;
		this.data = data;
		HttpConnectionManager.getInstance().push(this);
	}

	public void get(String url, JSONObject data) {
		create(GET, url, data);
	}

	public void post(String url, JSONObject data) {
		create(POST, url, data);
	}

	public void put(String url, JSONObject data) {
		create(PUT, url, data);
	}

	public void delete(String url, JSONObject data) {
		create(DELETE, url, data);
	}

	public void bitmap(String url, JSONObject data) {
		create(BITMAP, url, data);
	}

	@Override
	public void run() {
		httpClient = new DefaultHttpClient();
		// �������Ӳ���
		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 6000);
		HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10000);
		HttpProtocolParams.setContentCharset(httpClient.getParams(), HTTP.UTF_8);
		HttpProtocolParams.setUserAgent(httpClient.getParams(), storager.get("baseInfo", "app_version=OneInGDUFS"));
		
		// ���url��վ�ڵ�ַ����վ���ַ������http://��ͷ����վ���ַ��ֱ�ӷ��أ�������վ�ڵ�ַ���Զ�����http://+����
		if(!url.startsWith("http://"))
			url = ProjectConstants.URL_HOST + url;
		
		try {
			HttpResponse response = null;
			switch (method) {
			case GET:
				url += setRequestData(method);
				HttpGet get = new HttpGet(url);
				// �㲥������ʼ���������������Message��
				handler.sendMessage(Message.obtain(handler, HttpConnectionUtils.STATUS_START, get));
				// ���Cookie
				checkCookie(get);
				// ��������
				response = httpClient.execute(get);
				break;
			case POST:
				HttpPost post = new HttpPost(url);
				post.setEntity((UrlEncodedFormEntity) setRequestData(method));
				post.setHeader("Content-Type", "application/json");
				handler.sendMessage(Message.obtain(handler, HttpConnectionUtils.STATUS_START, post));
				checkCookie(post);
				response = httpClient.execute(post);
				break;
			case PUT:
				HttpPut put = new HttpPut(url);
				put.setEntity((UrlEncodedFormEntity) setRequestData(method));
				Log.d("test", "test");
				put.setHeader("Content-Type", "application/json");
				handler.sendMessage(Message.obtain(handler, HttpConnectionUtils.STATUS_START, put));
				checkCookie(put);
				response = httpClient.execute(put);
				break;
			case DELETE:
				url += setRequestData(method);
				HttpDelete delete = new HttpDelete(url);
				handler.sendMessage(Message.obtain(handler, HttpConnectionUtils.STATUS_START, delete));
				checkCookie(delete);
				response = httpClient.execute(delete);
				break;
			case BITMAP:
				HttpGet getBitmap = new HttpGet(url);
				checkCookie(getBitmap);
				response = httpClient.execute(getBitmap);
				processBitmapResponse(response);
				break;
			}
			if (method != BITMAP)
				processResponse(response);
		} catch (ConnectTimeoutException e) {
			// �㲥������ʱ��
			handler.sendMessage(Message.obtain(handler, HttpConnectionUtils.STATUS_TIMEOUT));
		} catch (Exception e) {
			// �㲥���������
			handler.sendMessage(Message.obtain(handler, HttpConnectionUtils.STATUS_ERROR, e));
		}
		HttpConnectionManager.getInstance().didComplete(this);
		// �㲥�����������
		handler.sendMessage(Message.obtain(handler, HttpConnectionUtils.STATUS_COMPLETE, httpClient));
	}

	/**
	 * �������������㲥����״̬<br/>
	 * �������200��success=true����㲥SUCCEED<br/>
	 * ���������״̬�룬��success=false����㲥FAILED<br/>
	 * @param response ������Ӧ����
	 */
	private void processResponse(HttpResponse response) {
		JSONObject result = null;
		if(response.getStatusLine().getStatusCode() == 200) {
			try {
				result = new JSONObject(EntityUtils.toString(response.getEntity()));
			} catch (Exception e) {
				Log.d("���ؽ�������쳣", TAG + ", processResponse, e=" + e.toString());
			}
			if(result.optBoolean("success")) {
				// �㲥������ɹ���
				handler.sendMessage(Message.obtain(handler, STATUS_SUCCEED, result));
			} else {
				// �㲥��������ʧ�ܡ�
				handler.sendMessage(Message.obtain(handler, STATUS_FAILED, result));
			}
		} else {
			result = new JSONObject();
			try {
			result.putOpt("success", false);
			result.putOpt("resultMsg", String.valueOf(response.getStatusLine().getStatusCode()));
			} catch(Exception e) {
				Log.d("JSON�쳣", TAG + ", processResponse, e=" + e.toString());
			}
			handler.sendMessage(Message.obtain(handler, STATUS_FAILED, result));
		}
	}

	/**
	 * ���ɹ��յ�Bitmap�����յ���Bitmap����ͨ��Message���͸�Handler��������ʧ�ܣ���null���͸�Hanlder<br/>
	 * <strong>��������ɹ���񣬾��㲥������ɹ���</strong>
	 * 
	 * @param response
	 * @throws IOException
	 */
	private void processBitmapResponse(HttpResponse response) throws IOException {
		if(response.getStatusLine().getStatusCode() == 200) {
			BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(response.getEntity());
			Bitmap bm = BitmapFactory.decodeStream(bufHttpEntity.getContent());
			handler.sendMessage(Message.obtain(handler, STATUS_SUCCEED, bm));
		} else {
			handler.sendMessage(Message.obtain(handler, STATUS_SUCCEED, null));
		}
	}
	
	/**
	 * ΪHTTP�����ʽ��Ҫ�����Ĳ�����������JSON���ݷ���data�ֶ���
	 * @param method ��������
	 * @return �����get�����򷵻��ַ���"?data=����"<br/>
	 * �����post�����򷵻�UrlEncodedFormEntityʵ�壬utf-8����
	 */
	private Object setRequestData(int method) {
		if(data != null) {
			if(method == GET) {
				return "?data=" + URLEncoder.encode(data.toString());
			} else {
				List<NameValuePair> result = new ArrayList<NameValuePair>();
				result.add(new BasicNameValuePair("data", data.toString()));
				try {
					UrlEncodedFormEntity entity = new UrlEncodedFormEntity(result, HTTP.UTF_8);
					return entity;
				} catch (Exception e) {
					Log.d("UrlEncodedFormEntity�쳣", TAG + ", requestDataFormatter, e=" + e.toString());
				}
			}
		}
		return null;
	}
	
	/**
	 * ������˷��ص�cookies�ַ�����ȡ��������key-value�洢
	 * @param cookies ����˷��ص�Set-Cookie�ַ���
	 * @return �洢cookies��Map����
	 */
	private void checkCookie(HttpRequestBase method) {
		// �������ͷ���Ѿ���beforeSend�ﱻ�����Cookie�ˣ������Cookie���Ƿ����sessionid��csrftoken
		if(method.containsHeader("Cookie")) {
			StringBuilder cookie = new StringBuilder(method.getHeaders("Cookie")[0].getValue());
			if(cookie.indexOf("csrftoken") == -1) {
				// ������Cookie��û���csrftoken����csrftoken�����ȥ
				cookie.append("csrftoken=").append(csrftoken).append(";");
				method.setHeader("X-CSRFToken", csrftoken);
			}
			if(storager.isExist("sessionid") && cookie.indexOf("sessionid") == -1) {
				// ������ش洢�����sessionid����beforeSend����ӵ�Cookie������sessionid����sessionid��ӵ�Cookie��
				cookie.append("sessionid=").append(storager.get("sessionid", "")).append(";");
			}
			method.setHeader("Cookie", cookie.toString());
		} else {
			// ��beforeSend�ﲢû���������Cookie���򽫱��ش洢���sessionid��csrftoken��ӵ�Cookie
			StringBuilder cookie = new StringBuilder("csrftoken=").append(csrftoken).append(";");
			method.setHeader("X-CSRFToken", csrftoken);
			if(storager.isExist("sessionid")) {
				cookie.append("sessionid=").append(storager.get("sessionid", "")).append(";");
			}
			method.setHeader("Cookie", cookie.toString());
		}
	}
	
	/**
	 * ��鱾�ش洢���Ƿ���csrftoken���������ֱ����ȡ�����أ����û������GET /getcsrftoken/���ӷ���˷��ص�Set-Cookieͷ������ȡ��csrftokenֵ<br />
	 * ����ڴ˹����г����򷵻�""����˱���Է��ص�ֵ����!=""�ж�
	 * @return csrftokenֵ
	 */
	public String getCsrfToken() {
		if(!storager.isExist("csrftoken")) {
			HttpGet httpGet = new HttpGet(ProjectConstants.URL_HOST + ProjectConstants.URL_GETCSRFTOKEN);
			try {
				HttpResponse response = new DefaultHttpClient().execute(httpGet);
				if(response.getStatusLine().getStatusCode() == 200) {
					csrftoken = cookiesToJSON(response.getHeaders("Set-Cookie")[0].getValue()).optString("csrftoken", "");
					storager.set("csrftoken", csrftoken).save();
				} else {
					Log.d("status", String.valueOf(response.getStatusLine().getStatusCode()));
					return "";
				}
			} catch (Exception e) {
				Log.d("�����쳣", TAG + ", getCsrfToken, e=" + e.toString());
				return "";
			}
		}

		return storager.get("csrftoken", "");
	}
	
	/**
	 * ������˷��ص�cookies�ַ�����ȡ��������key-value�洢
	 * @param cookies ����˷��ص�Set-Cookie�ַ���
	 * @return �洢cookies��Map����
	 */
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
					Log.d("JSON����", TAG + ", e=" + e.toString());
				}
			}
			return cookie_json;
		}
		return null;
	}
}
