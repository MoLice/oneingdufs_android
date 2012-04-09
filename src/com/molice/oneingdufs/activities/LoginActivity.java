package com.molice.oneingdufs.activities;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.interfaces.OnHttpRequestListener;
import com.molice.oneingdufs.layouts.ActionBarController;
import com.molice.oneingdufs.layouts.AppMenu;
import com.molice.oneingdufs.utils.ClientToServer;
import com.molice.oneingdufs.utils.ProjectConstants;
import com.molice.oneingdufs.utils.SharedPreferencesStorager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 登录Activity<br />
 * 用intent.putExtra("onLoginSuccessActivity", Activity.class)的方式设置onLoginSuccessActivity|onLoginFailedActivity时的跳转Activity<br />
 * 登录成功并跳转后，登录Activity将会被finish()
 * TODO 虽然正常情况下不会在登录状态中还调用到登录界面，但为了以防万一，必须在onCreate()和onStart()方法中添加登录状态的判断，假如已登录则跳转到“账号管理”界面，提示当前已登录，是否要注销并用另外一个账号登陆。
 */
public class LoginActivity extends Activity {
	// 用户名输入框
	private EditText login_username;
	// 密码输入框
	private EditText login_password;
	// 登录按钮
	private Button login_submit;
	// 注册按钮
	private Button login_register;
	// SharedPreferences
	private SharedPreferencesStorager storager;
	// 选项菜单Menu
	private AppMenu appMenu;
	// 登录结束（不管成功失败）后处理Activity跳转的Intent
	private Intent intent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// 如果当前已登录，则屏蔽LoginActivity
		storager = new SharedPreferencesStorager(this);
		if(storager.get("isLogin", false)) {
			Toast.makeText(this, "已登录", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		setContentView(R.layout.login);
		
		// 设置标题栏
		ActionBarController.setTitle(this, R.string.login_title);
		
		// 初始化成员变量
		login_username = (EditText) findViewById(R.id.login_username);
		login_password = (EditText) findViewById(R.id.login_password);
		login_submit = (Button) findViewById(R.id.login_submit);
		login_register = (Button) findViewById(R.id.login_register);
		
		appMenu = new AppMenu(this);
		
		// 如果本地已存储过，则自动填充用户名
		if(storager.isExist("username")) {
			login_username.setText(storager.get("username", ""));
		}
		
		// 设置登录按钮的点击动作，进行登录
		login_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String username = login_username.getText().toString();
				String password = login_password.getText().toString();
				if(!username.equals("") && username.equals(storager.get("username", "")) && password.equals(storager.get("password", ""))) {
					// 登录成功
					storager.set("isLogin", true).save();
					Toast.makeText(LoginActivity.this, "欢迎回来，" + storager.get("username", ""), Toast.LENGTH_LONG).show();
					// 返回到某个Activity
					callActivityAfterLogin("success");
				} else {
					// 登录失败
					Toast.makeText(LoginActivity.this, "登录失败，请检查用户名、密码，或者重新注册", Toast.LENGTH_LONG).show();
				}
				/*
				// 发起服务器连接，准备登录
				ClientToServer client = new ClientToServer(LoginActivity.this);
				// 添加http请求监听器，在请求的不同阶段进行操作
				client.setOnRequestListener(httpRequestListener);
				// 将用户名、密码添加到http请求
				JSONObject postData = new JSONObject();
				try {
					postData.put("username", login_username.getText().toString());
					postData.put("password", login_password.getText().toString());
				} catch (Exception e) {
					Log.d("JSON错误", "LoginActivity, e=" + e.toString());
				}
				// 发起post登录请求，请求标志位为0
				client.post(ProjectConstants.URL_LOGIN, postData, 0);
				*/
			}
		});
		
		// 设置注册按钮的点击动作，跳转到@{link RegisterActivity}并finish()@{link LoginActivity}
		login_register.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
				startActivity(intent);
				finish();
			}
		});
		
		// 添加文本框内容变化的事件监听，只有用户名和密码都不为空时，登录按钮才能接受点击
		login_username.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				onInputChanged(s, start, before, count);
			}
		});
		
		login_password.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				onInputChanged(s, start, before, count);
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
	
	/**
	 * 用于响应用户名、密码这两个文本框的TextChange事件 如果两个文本框同时不为空则启用登录按钮，否则登录按钮不可用
	 */
	private void onInputChanged(CharSequence s, int start, int before, int count) {
		if (login_username.getText().length() < 4
				|| login_password.getText().length() == 0)
			login_submit.setEnabled(false);
		else
			login_submit.setEnabled(true);
	}
	
	/**
	 * 处理登录结束后的Activity跳转，根据调用{@link LoginActivity}时指定的登录成功或失败后的跳转Activity进行跳转，如没有，则默认跳转到{@link MainActivity}
	 * 调用{@link LoginActivity}的Activity需用intent.putExtra("onLoginSuccessActivity", Activity.class)的方式设置onLoginSuccessActivity|onLoginFailedActivity时的跳转Activity
	 * @param whichStatus success|failed，标志要跳到success还是failed，若不是这两个值，则跳转到{@link MainActivity}
	 */
	private void callActivityAfterLogin(String whichStatus) {
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			if(whichStatus.equals("success") && extras.get("onLoginSuccessActivity") != null) {
				intent = new Intent(getApplicationContext(), (Class<?>) extras.get("onLoginSuccessActivity"));
				startActivity(intent);
			} else if(whichStatus.equals("failed") && extras.get("onLoginFailedActivity") != null) {
				intent = new Intent(getApplicationContext(), (Class<?>) extras.get("onLoginFailedActivity"));
				startActivity(intent);
			}
		}
		intent = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(intent);
		finish();
	}
	
	private OnHttpRequestListener httpRequestListener = new OnHttpRequestListener() {
		
		@Override
		public void onTimeout(int requestCode, ClientToServer target, HttpRequestBase method, ConnectTimeoutException e) {
			Log.d("HTTP-TimeOut", "Timeout");
		}
		
		@Override
		public void onSuccess(int requestCode, ClientToServer target, HttpRequestBase method, HttpResponse response,
				JSONObject result) {
			switch(requestCode) {
			case 0:
				// 登录POST /home/login/
				if(result.optBoolean("success")) {
					// TODO 登录成功，将返回的所有用户数据存储到sqlite，以便应用在切换Activity时不用每次都从服务器请求最新数据
					storager
					.set("username", result.optString("username"))
					.set("sessionid", result.optString("sessionid"))
					.set("isLogin", true)
					.save();
					// 返回到某个Activity
					callActivityAfterLogin("success");
				} else {
					// 登录失败
					new AlertDialog.Builder(LoginActivity.this)
					.setTitle("登录失败")
					.setMessage(result.optString("resultMsg"))
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					})
					.show();
				}
				break;
			}
		}
		
		@Override
		public void onFailed(int requestCode, ClientToServer target, HttpRequestBase method, HttpResponse response) {
			Log.d("HTTP请求失败", "错误代码：" + String.valueOf(response.getStatusLine().getStatusCode()));
			// 请求失败，中断连接
			method.abort();
		}
		
		@Override
		public void onException(int requestCode, ClientToServer target, HttpRequestBase method, Exception e) {
			Log.d("HTTP抛出异常", e.toString());
		}
		
		@Override
		public void beforeSend(int requestCode, ClientToServer target, HttpRequestBase method, HttpClient client) {
		}
		
		@Override
		public void afterSend(int requestCode, ClientToServer target, HttpRequestBase method, HttpClient client) {
			// 关闭连接
			client.getConnectionManager().shutdown();
		}
	};
}