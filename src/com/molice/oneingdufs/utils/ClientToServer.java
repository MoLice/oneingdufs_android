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
 * 负责客户端和服务端所有通讯的类，包括常用的get、post方法<br />
 * HTTP默认的设置也包含在类中，例如请求的url基础网址、连接超时设置、Http头部信息等<br />
 * get/post方法均会自动在Cookie头部里添加csrftoken和sessionid(如果有sessionid的话)<br />
 * <strong>对请求的设置、返回数据的处理，均在事件监听器内进行，因此必须先调用setOnRequestListener()设置监听，再进行http请求</strong>
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
	 * @param context 因为要生成SharedPreferencesStorager对象，所以需要传入Context
	 */
	public ClientToServer(Context context) {
		this.client = new DefaultHttpClient();
		this.requestListener = null;
		this.httpParams = new BasicHttpParams();
		this.storager = new SharedPreferencesStorager(context);
		// 设置连接参数
		HttpConnectionParams.setConnectionTimeout(httpParams, 10*1000);
		HttpConnectionParams.setSoTimeout(httpParams, 20 * 1000);
		HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);
		HttpProtocolParams.setUserAgent(httpParams, this.storager.get("baseInfo", "app_version=OneInGDUFS"));
		
		this.csrftoken = getCsrfToken();
	}
	
	/**
	 * 为HTTP请求格式化要附带的参数，将所有JSON数据放在data字段内
	 * 如果是get请求则返回String实体
	 * 如果是post请求则返回UrlEncodedFormEntity实体，utf-8编码
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
	 * 在beforeSend()后调用，检查本次存储和请求头部，根据需要添加sessionid和csrftoken到Cookie中。如果头部和本地存储均存在同一字段，则头部的值优先权更高
	 * @param method HttpGet、HttpPost
	 */
	private void checkCookie(HttpRequestBase method) {
		// 如果请求头部已经在beforeSend里被添加了Cookie了，则检查该Cookie内是否存在sessionid或csrftoken
		if(method.containsHeader("Cookie")) {
			String cookie = method.getHeaders("Cookie")[0].getValue();
			if(cookie.indexOf("csrftoken") == -1) {
				// 设置了Cookie但没添加csrftoken，则将csrftoken添加上去
				cookie = cookie + "csrftoken=" + csrftoken + ";";
				method.setHeader("X-CSRFToken", csrftoken);
			}
			if(storager.isExist("sessionid") && cookie.indexOf("sessionid") == -1) {
				// 如果本地存储里存在sessionid且在beforeSend里添加的Cookie不包含sessionid，则将sessionid添加到Cookie内
				cookie = cookie + "sessionid=" + storager.get("sessionid", "") + ";";
			}
			method.setHeader("Cookie", cookie);
		} else {
			// 在beforeSend里并没有主动添加Cookie，则将本地存储里的sessionid和csrftoken添加到Cookie
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
		// 如果是相对地址，自动添加上域名
		if(url.length() < 4 || url.substring(0, 4) != "http") {
			url = ClientToServer.URL_SERVER + url;
		}
		// 如果需要传参，则将参数添加到url尾部
		if(data != null) {
			url = url + requestDataFormatter("get", data);
		}
		HttpGet httpGet = new HttpGet(url);
		httpGet.setParams(httpParams);
		// 发送前，用于设置参数
		if(requestListener != null) {
			requestListener.beforeSend(requestCode, this, httpGet, client);
		}
		
		checkCookie(httpGet);
		
		try {
			HttpResponse response = client.execute(httpGet);
			if(response.getStatusLine().getStatusCode() == 200) {
				// 获取服务器返回的数据
				HttpEntity entity = response.getEntity();
				String result = null;
				if(entity != null) {
					result = EntityUtils.toString(entity);
				}
				// 请求成功，调用事件监听函数
				if(requestListener != null) {
					requestListener.onSuccess(requestCode, this, httpGet, response, new JSONObject(result));
				}
			} else {
				// 请求失败，调用事件监听函数
				if(requestListener != null) {
					requestListener.onFailed(requestCode, this, httpGet, response);
				}
			}
			// 请求结束，调用事件监听函数
			if(requestListener != null) {
				requestListener.afterSend(requestCode, this, httpGet, client);
			}
		} catch (ConnectTimeoutException e) {
			// 超时，请求失败，调用事件监听函数
			if(requestListener != null) {
				requestListener.onTimeout(requestCode, this, httpGet, e);
			}
		} catch (Exception e) {
			// 出现异常，请求失败，调用事件监听函数
			if(requestListener != null) {
				requestListener.onException(requestCode, this, httpGet, e);
			}
		} finally {
			// 请求结束，调用事件监听函数
			if(requestListener != null) {
				requestListener.afterSend(requestCode, this, httpGet, client);
			}
		}
	}

	@Override
	public void post(String url, JSONObject data, int requestCode) {
		// 如果是相对地址，自动添加上域名
		if(url.length() < 4 || url.substring(0, 4) != "http") {
			url = ClientToServer.URL_SERVER + url;
		}
		HttpPost httpPost = new HttpPost(url);
		UrlEncodedFormEntity postData = (UrlEncodedFormEntity) requestDataFormatter("post", data);
		httpPost.setHeader("Content-Type", "application/json");
		// TODO 了解究竟为什么只要加上Content-Length就会抛出clientprotocolexception异常（协议出错）
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
				// 获取服务器返回的数据
				HttpEntity entity = response.getEntity();
				String result = null;
				if(entity != null) {
					result = EntityUtils.toString(entity);
				}
				// 请求成功，调用事件监听函数
				if(requestListener != null) {
					requestListener.onSuccess(requestCode, this, httpPost, response, new JSONObject(result));
				}
			} else {
				if(requestListener != null) {
					requestListener.onFailed(requestCode, this, httpPost, response);
				}
			}
		} catch (ConnectTimeoutException e) {
			// 超时，请求失败，调用事件监听函数
			if(requestListener != null) {
				requestListener.onTimeout(requestCode, this, httpPost, e);
			}
		} catch (Exception e) {
			// 出现异常，请求失败，调用事件监听函数
			if(requestListener != null) {
				requestListener.onException(requestCode, this, httpPost, e);
			}
		} finally {
			// 请求结束，调用事件监听函数
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
			// 存储格式化后的cookie
			JSONObject cookie_json = new JSONObject();
		
			String[] cookie_array = cookies.split(";");
			for(String cookie : cookie_array) {
				cookie = cookie.trim();
				int beginIndex = cookie.indexOf("=");
				try {
					cookie_json.put(cookie.substring(0, beginIndex), cookie.substring(beginIndex + 1, cookie.length()));
				} catch (Exception e) {
					Log.d("JSON错误", e.toString());
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
