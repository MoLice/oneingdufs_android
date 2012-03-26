package com.molice.oneingdufs.activities;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONObject;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.androidpn.Constants;
import com.molice.oneingdufs.interfaces.OnHttpRequestListener;
import com.molice.oneingdufs.layouts.ActionBarController;
import com.molice.oneingdufs.layouts.AppMenu;
import com.molice.oneingdufs.utils.ClientToServer;
import com.molice.oneingdufs.utils.FormValidator;
import com.molice.oneingdufs.utils.ProjectConstants;
import com.molice.oneingdufs.utils.SharedPreferencesStorager;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * 注册Activity<br />
 * TODO 判断是否已登录，若已登录则给用户弹出选择：要么注销登录然后注册，要么退出注册并返回
 */
public class RegisterActivity extends Activity {
	private Button register_back;
	private Button register_submit;
	
	private JSONArray form;
	private FormValidator validator;
	private SharedPreferencesStorager storager;
	private AppMenu appMenu;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        
        // 设置标题栏
        ActionBarController.setTitle(this, R.string.register_title);
		
		register_back = (Button) findViewById(R.id.register_back);
		register_submit = (Button) findViewById(R.id.register_submit);
		
		storager = new SharedPreferencesStorager(this);
		appMenu = new AppMenu(this);
		
		form = new JSONArray();
		// 添加username
		form.put(FormValidator.createInputData(R.id.register_username, "username", R.id.register_username_label, "^(\\w|\\d){4,20}$", R.string.register_username_label, R.string.register_username_error));
		// 添加password
		form.put(FormValidator.createInputData(R.id.register_password, "password", R.id.register_password_label, "^.{6,}$", R.string.register_password_label, R.string.register_password_error));
		// 添加studentId
		form.put(FormValidator.createInputData(R.id.register_studentId, "studentId", R.id.register_studentId_label, "^20\\d{9}$", R.string.register_studentId_label, R.string.register_studentId_error));
		// 添加mygdufs_pwd
		form.put(FormValidator.createInputData(R.id.register_mygdufs_pwd, "mygdufs_pwd", R.id.register_mygdufs_pwd_label, "^.+$", R.string.register_mygdufs_pwd_label, R.string.register_mygdufs_pwd_error));
		
		validator = new FormValidator(this, form);
		// 开启失去焦点时自动验证
		validator.addOnFocusChangeValidate();
		
		// 响应返回按钮，判断是否已修改并弹出提示
		register_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				validator.checkBackIfFormModified();
			}
		});
		
		// 响应注册按钮，提交注册表单并进行跳转，若注册成功则返回到“账号管理”界面
		register_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(validator.isFormModified()) {
					if(validator.isFormCorrect()) {
						JSONObject input = validator.getInput();
						// 添加XMPP用户名
						try {
							input.putOpt("apn_username", storager.get(Constants.XMPP_USERNAME, ""));
						} catch (Exception e) {
							Log.d("JSON错误", "RegisterActivity, e=" + e.toString());
						}
						// 发起服务器连接，准备注册
						ClientToServer client = new ClientToServer(RegisterActivity.this);
						// 添加http请求监听器，在请求的不同阶段进行操作
						client.setOnRequestListener(httpRequestListener);
						// 将表单数据添加到http请求
						client.post(ProjectConstants.URL_REGISTER, input, 0);
					} else {
						Log.d("表单验证", "失败");
						ProjectConstants.alertDialog(RegisterActivity.this, "输入错误", "请按照提示修改", true);
					}
				} else {
					// 提示没有改动
					Toast.makeText(RegisterActivity.this, "无修改", Toast.LENGTH_SHORT);
				}
			}
		});
    }
    
    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	appMenu.onCreateOptionsMenu(menu);
    	return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Log.d("MainActivity", "onOptionsItemSelected被调用");
    	return appMenu.onOptionsItemSelected(item);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	if(storager.get("isLogin", false)) {
    		// 显示登录组，隐藏未登录组
    		menu.setGroupVisible(AppMenu.NOTLOGIN, false);
    		menu.setGroupVisible(AppMenu.ISLOGIN, true);
    	} else {
    		// 显示未登录组，隐藏登录组
    		menu.setGroupVisible(AppMenu.NOTLOGIN, true);
    		menu.setGroupVisible(AppMenu.ISLOGIN, false);
    	}
    	return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)	{
    	if(keyCode == KeyEvent.KEYCODE_BACK) {
    		validator.checkBackIfFormModified();
    	}
    	return super.onKeyDown(keyCode, event);
    }
    
    private OnHttpRequestListener httpRequestListener = new OnHttpRequestListener() {
		
		@Override
		public void onTimeout(int requestCode, ClientToServer target,
				HttpRequestBase method, ConnectTimeoutException e) {
		}
		
		@Override
		public void onSuccess(int requestCode, ClientToServer target,
				HttpRequestBase method, HttpResponse response, JSONObject result) {
			// TODO 判断服务端返回的信息，若注册成功，则返回sessionid，将sessionid存储起来，然后当前Activity finish，或者跳转到MainActivity
			// TODO 若表单验证失败，则弹出提示框，显示错误信息
			Log.d("请求返回结果", "result=" + result);
			switch(requestCode) {
			case 0:
				if(result.optBoolean("success")) {
					// 成功
					Log.d("请求返回结果,成功, ", "resultMsg:" + result.optString("resultMsg"));
					// TODO 这里将直接使用LoginActivity登录成功时的代码
					storager
					.set("username", result.optString("username"))
					.set("sessionid", result.optString("sessionid"))
					.set("isLogin", true)
					.save();
					// 跳转到MainActivity
					startActivity(new Intent(getApplicationContext(), MainActivity.class));
					finish();
				} else {
					// 失败
					Log.d("请求返回结果,失败, ", "resultMsg:" + result.optString("resultMsg"));
					// 表单验证失败
					if(result.has("formErrors")) {
						JSONObject formErrors = result.optJSONObject("formErrors");
						// 调用FormValidator实例将表单错误信息显示到label上
						validator.updateFormMessageFromServer(formErrors);
					} else {
						ProjectConstants.alertDialog(RegisterActivity.this, "注册失败", result.optString("resultMsg"), true);
					}
				}
				break;
			}
		}
		
		@Override
		public void onFailed(int requestCode, ClientToServer target,
				HttpRequestBase method, HttpResponse response) {
			Log.d("请求失败", "onFailed:" + String.valueOf(response.getStatusLine().getStatusCode()));
		}
		
		@Override
		public void onException(int requestCode, ClientToServer target,
				HttpRequestBase method, Exception e) {
			Log.d("请求失败", "onException");
		}
		
		@Override
		public void beforeSend(int requestCode, ClientToServer target,
				HttpRequestBase method, HttpClient client) {
		}
		
		@Override
		public void afterSend(int requestCode, ClientToServer target,
				HttpRequestBase method, HttpClient client) {
			Log.d("请求结束", "afterSend");
		}
	};
}