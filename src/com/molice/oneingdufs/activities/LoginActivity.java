package com.molice.oneingdufs.activities;

import org.json.JSONObject;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.layouts.ActionBarController;
import com.molice.oneingdufs.utils.HttpConnectionHandler;
import com.molice.oneingdufs.utils.HttpConnectionUtils;
import com.molice.oneingdufs.utils.ProjectConstants;
import com.molice.oneingdufs.utils.SharedPreferencesStorager;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
		
		// 如果本地已存储过，则自动填充用户名
		if(storager.has("username")) {
			login_username.setText(storager.get("username", ""));
		}
		
		// 设置登录按钮的点击动作，进行登录
		login_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				JSONObject data = new JSONObject();
				try {
					data.putOpt("username", login_username.getText().toString());
					data.putOpt("password", login_password.getText().toString());
				} catch (Exception e) {
					Log.d("JSON异常", "LoginActivity#submit, e=" + e.toString());
				}
				new HttpConnectionUtils(connectionHandler, LoginActivity.this).post(ProjectConstants.URL.login, data);
			}
		});
		
		// 设置注册按钮的点击动作，跳转到@{link RegisterActivity}并finish()@{link LoginActivity}
		login_register.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
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
		} else {
			//因为MainActivity的launchMode="singleTop"，所以不能再重新启动一个，否则会生成两个MainActivity实例
			//startActivity(new Intent(getApplicationContext(), MainActivity.class));
		}
		finish();
	}
	
	private HttpConnectionHandler connectionHandler = new HttpConnectionHandler(this) {
		@Override
		protected void onSucceed(JSONObject result) {
			// TODO 登录成功，将返回的所有用户数据存储到sqlite，以便应用在切换Activity时不用每次都从服务器请求最新数据
			super.onSucceed(result);
			JSONObject user_info = result.optJSONObject("user_info");
			JSONObject user_roomaddress = result.optJSONObject("user_roomaddress");
			storager
			.set("isLogin", true)
			.set("sessionid", result.optString("sessionid"))
			.set("username", result.optString("username"))
			.set("studentId", result.optString("studentId"))
			.set("user_info_email", user_info.optString("email", ""))
			.set("user_info_truename", user_info.optString("truename", ""))
			.set("user_info_phone", user_info.optString("phone", ""))
			.set("user_info_cornet", user_info.optString("cornet", ""))
			.set("user_info_qq", user_info.optString("qq", ""))
			.set("user_roomaddress_building", user_roomaddress.optString("building", ""))
			.set("user_roomaddress_room", user_roomaddress.optString("room", ""))
			.save();
			// 返回到某个Activity
			callActivityAfterLogin("success");
		}
	};
}