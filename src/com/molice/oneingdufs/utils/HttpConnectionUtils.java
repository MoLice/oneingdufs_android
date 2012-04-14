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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 生成一个Http请求<br/>
 * 使用{@link HttpConnectionHandler}对请求的不同阶段进行控制<br/>
 * 使用{@link HttpConnectionManager}对请求队列进行控制
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-4-9
 */
public class HttpConnectionUtils implements Runnable {
	private static final String TAG = HttpConnectionUtils.class.getSimpleName();
	public static final int TIMEOUT = 8000;
	public static final int TIMEOUT_SOCKET = 10000;
	
	public static final int STATUS_START = 0;
	public static final int STATUS_SUCCEED = 1;
	public static final int STATUS_FAILED = 2;
	public static final int STATUS_TIMEOUT = 3;
	public static final int STATUS_ERROR = 4;
	public static final int STATUS_COMPLETE = 5;
	public static final int STATUS_NOCSRFTOKEN = 6;
	
	public static final int ACTION_SHOWDIALOG = 10;
	public static final int ACTION_CHANGEDIALOG = 11;
	public static final int ACTION_DISMISSDIALOG = 12;

	private static final int GET = 0;
	private static final int POST = 1;
	private static final int PUT = 2;
	private static final int DELETE = 3;
	private static final int BITMAP = 4;

	private String url;
	private int method;
	private Handler handler;
	private Context context;
	private JSONObject data;

	private HttpClient httpClient;
	private SharedPreferencesStorager storager;
	private String csrftoken;

	public HttpConnectionUtils(Handler handler, Context context) {
		this.handler = handler;
		this.context = context;
		this.storager = new SharedPreferencesStorager(context);
		//因为getCsrfToken()方法中也需要弹出窗口，所以需要在多线程下运行，否则因为主UI阻塞所以无法显示弹出窗口
		//因此把this.csrftoken的初始化放在run()开头。
		//this.csrftoken = getCsrfToken();
	}

	public void create(int method, String url, JSONObject data) {
		Log.d(TAG, TAG + "#create, method=" + method + ", url=" + url + ", data=" + String.valueOf(data));
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
		this.csrftoken = getCsrfToken();
		if(csrftoken.equals("")) {
			handler.sendMessage(Message.obtain(handler, STATUS_NOCSRFTOKEN));
			return;
		}
		
		httpClient = new DefaultHttpClient();
		// 设置连接参数
		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpClient.getParams(), TIMEOUT_SOCKET);
		HttpProtocolParams.setContentCharset(httpClient.getParams(), HTTP.UTF_8);
		HttpProtocolParams.setUserAgent(httpClient.getParams(), storager.get("baseInfo", "app_version=OneInGDUFS"));
		
		// 检查url是站内地址或是站外地址，若以http://开头则是站外地址，直接返回，否则是站内地址，自动补充http://+域名
		if(!url.startsWith("http://"))
			url = ProjectConstants.URL.getHost(context) + url;
		
		try {
			HttpResponse response = null;
			switch (method) {
			case GET:
				url += setRequestData(method);
				HttpGet get = new HttpGet(url);
				// 广播“请求开始”，将请求对象传入Message中
				handler.sendMessage(Message.obtain(handler, HttpConnectionUtils.STATUS_START, get));
				// 检查Cookie
				checkCookie(get);
				// 发出请求
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
			// 广播“请求超时”
			handler.sendMessage(Message.obtain(handler, HttpConnectionUtils.STATUS_TIMEOUT));
		} catch (Exception e) {
			// 广播“请求出错”
			handler.sendMessage(Message.obtain(handler, HttpConnectionUtils.STATUS_ERROR, e));
		}
		HttpConnectionManager.getInstance().didComplete(this);
		// 广播“请求结束”
		handler.sendMessage(Message.obtain(handler, HttpConnectionUtils.STATUS_COMPLETE, httpClient));
	}

	/**
	 * 分析处理结果，广播请求状态<br/>
	 * 如果请求200且success=true，则广播SUCCEED<br/>
	 * 如果是其他状态码，或success=false，则广播FAILED<br/>
	 * @param response 请求响应对象
	 */
	private void processResponse(HttpResponse response) {
		JSONObject result = null;
		if(response.getStatusLine().getStatusCode() == 200) {
			try {
				result = new JSONObject(EntityUtils.toString(response.getEntity()));
			} catch (Exception e) {
				Log.d("返回结果处理异常", TAG + ", processResponse, e=" + e.toString());
			}
			if(result != null) {
				Log.d("看result", "HttpConnectionUtils#processResponse, result=" + result.toString());
				if(result.optBoolean("success")) {
					// 广播“请求成功”
					handler.sendMessage(Message.obtain(handler, STATUS_SUCCEED, result));
				} else {
					// 广播“请求处理失败”
					handler.sendMessage(Message.obtain(handler, STATUS_FAILED, result));
				}
			}
		} else {
			result = new JSONObject();
			try {
			result.putOpt("success", false);
			result.putOpt("resultMsg", String.valueOf(response.getStatusLine().getStatusCode()));
			handler.sendMessage(Message.obtain(handler, STATUS_FAILED, result));
			} catch(Exception e) {
				Log.d("JSON异常", TAG + ", processResponse, e=" + e.toString());
			}
		}
	}

	/**
	 * 若成功收到Bitmap，则将收到的Bitmap对象通过Message发送给Handler，若请求失败，则将null发送给Hanlder<br/>
	 * <strong>不管请求成功与否，均广播“请求成功”</strong>
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
	 * 为HTTP请求格式化要附带的参数，将所有JSON数据放在data字段内
	 * @param method 请求类型
	 * @return 如果是get请求则返回字符串"?data=参数"<br/>
	 * 如果是post请求则返回UrlEncodedFormEntity实体，utf-8编码
	 */
	private Object setRequestData(int method) {
		if(data != null) {
			if(method == GET || method == DELETE) {
				return "?data=" + URLEncoder.encode(data.toString());
			} else {
				List<NameValuePair> result = new ArrayList<NameValuePair>();
				result.add(new BasicNameValuePair("data", data.toString()));
				try {
					UrlEncodedFormEntity entity = new UrlEncodedFormEntity(result, HTTP.UTF_8);
					return entity;
				} catch (Exception e) {
					Log.d("UrlEncodedFormEntity异常", TAG + ", requestDataFormatter, e=" + e.toString());
				}
			}
		}
		return (method == GET || method == DELETE) ? "" : null;
	}
	
	/**
	 * 将服务端返回的cookies字符串提取出来，按key-value存储
	 * @param cookies 服务端返回的Set-Cookie字符串
	 * @return 存储cookies的Map容器
	 */
	private void checkCookie(HttpRequestBase method) {
		// 如果请求头部已经在beforeSend里被添加了Cookie了，则检查该Cookie内是否存在sessionid或csrftoken
		if(method.containsHeader("Cookie")) {
			StringBuilder cookie = new StringBuilder(method.getHeaders("Cookie")[0].getValue());
			if(cookie.indexOf("csrftoken") == -1) {
				// 设置了Cookie但没添加csrftoken，则将csrftoken添加上去
				cookie.append("csrftoken=").append(csrftoken).append(";");
				method.setHeader("X-CSRFToken", csrftoken);
			}
			if(storager.has("sessionid") && cookie.indexOf("sessionid") == -1) {
				// 如果本地存储里存在sessionid且在beforeSend里添加的Cookie不包含sessionid，则将sessionid添加到Cookie内
				cookie.append("sessionid=").append(storager.get("sessionid", "")).append(";");
			}
			method.setHeader("Cookie", cookie.toString());
		} else {
			// 在beforeSend里并没有主动添加Cookie，则将本地存储里的sessionid和csrftoken添加到Cookie
			StringBuilder cookie = new StringBuilder("csrftoken=").append(csrftoken).append(";");
			method.setHeader("X-CSRFToken", csrftoken);
			if(storager.has("sessionid")) {
				cookie.append("sessionid=").append(storager.get("sessionid", "")).append(";");
			}
			method.setHeader("Cookie", cookie.toString());
		}
	}
	
	/**
	 * TODO 增加错误提醒，因为在这里发起的HTTP请求的各种状态转换是没有提醒的。
	 * 检查本地存储中是否有csrftoken，如果有则直接提取并返回，如果没有则发送GET /getcsrftoken/，从服务端返回的Set-Cookie头部中提取出csrftoken值<br />
	 * 如果在此过程中出错，则返回""，因此必须对返回的值进行!=""判断
	 * @return csrftoken值
	 */
	public String getCsrfToken() {
		if(!storager.has("csrftoken")) {
			JSONObject msg = new JSONObject();
			try {
				msg.putOpt("title", "配置网络");
				msg.putOpt("msg", "设置参数中...");
			} catch (Exception e) {
				Log.d("JSON异常", "HttpConnectionUtils#getCsrfToken, e=" + e.toString());
			}
			handler.sendMessage(Message.obtain(handler, ACTION_SHOWDIALOG, msg));
			
			HttpClient client = new DefaultHttpClient();
			// 设置连接参数
			HttpConnectionParams.setConnectionTimeout(client.getParams(), TIMEOUT);
			HttpConnectionParams.setSoTimeout(client.getParams(), TIMEOUT_SOCKET);
			HttpProtocolParams.setContentCharset(client.getParams(), HTTP.UTF_8);
			HttpProtocolParams.setUserAgent(client.getParams(), storager.get("baseInfo", "app_version=OneInGDUFS"));
			HttpGet httpGet = new HttpGet(ProjectConstants.URL.getHost(context) + ProjectConstants.URL.getCsrftoken);
			Log.d("看设置是否改变host", "HttpConnectionUtils#getCsrfToken, host=" + ProjectConstants.URL.getHost(context));
			try {
				HttpResponse response = client.execute(httpGet);
				if(response.getStatusLine().getStatusCode() == 200) {
					// 成功
					csrftoken = cookiesToJSON(response.getHeaders("Set-Cookie")[0].getValue()).optString("csrftoken", "");
					storager.set("csrftoken", csrftoken).save();
					handler.sendMessage(Message.obtain(handler, ACTION_DISMISSDIALOG, null));
					return csrftoken;
				} else {
					// 状态码错误
					Log.d("status", String.valueOf(response.getStatusLine().getStatusCode()));
					msg.putOpt("title", "配置网络");
					msg.putOpt("msg", "请求错误，错误码" + String.valueOf(response.getStatusLine().getStatusCode()));
					handler.sendMessage(Message.obtain(handler, ACTION_CHANGEDIALOG, msg));
				}
			} catch (ConnectTimeoutException e) {
				Log.d("请求超时", "HttpConnectionUtils#getCsrfToken, e=" + e.toString());
				handler.sendMessage(Message.obtain(handler, HttpConnectionUtils.STATUS_TIMEOUT, client));
			} catch (Exception e) {
				Log.d("请求异常", TAG + ", getCsrfToken, e=" + e.toString());
				try {
					msg.putOpt("title", "配置网络");
					msg.putOpt("msg", "请求异常，e=" + e.toString());
					handler.sendMessage(Message.obtain(handler, ACTION_CHANGEDIALOG, msg));
				} catch(Exception e2) {
				}
			}
			return "";
			//handler.sendMessage(Message.obtain(handler, ACTION_DISMISSDIALOG, null));
		}

		return storager.get("csrftoken", "");
	}
	
	/**
	 * 将服务端返回的cookies字符串提取出来，按key-value存储
	 * @param cookies 服务端返回的Set-Cookie字符串
	 * @return 存储cookies的Map容器
	 */
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
					Log.d("JSON错误", TAG + ", e=" + e.toString());
				}
			}
			return cookie_json;
		}
		return null;
	}
}
