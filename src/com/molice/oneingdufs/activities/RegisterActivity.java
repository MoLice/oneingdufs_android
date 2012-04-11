package com.molice.oneingdufs.activities;

import org.json.JSONArray;
import org.json.JSONObject;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.androidpn.Constants;
import com.molice.oneingdufs.layouts.ActionBarController;
import com.molice.oneingdufs.layouts.AppMenu;
import com.molice.oneingdufs.utils.FormValidator;
import com.molice.oneingdufs.utils.HttpConnectionHandler;
import com.molice.oneingdufs.utils.HttpConnectionUtils;
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
        
		// 如果当前已登录，则屏蔽LoginActivity
		storager = new SharedPreferencesStorager(this);
		if(storager.get("isLogin", false)) {
			Toast.makeText(this, "已登录", Toast.LENGTH_SHORT).show();
			finish();
		}
		
        setContentView(R.layout.register);
        
        // 设置标题栏
        ActionBarController.setTitle(this, R.string.register_title);
		
		register_back = (Button) findViewById(R.id.register_back);
		register_submit = (Button) findViewById(R.id.register_submit);
		
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
						new HttpConnectionUtils(connectionHandler, storager).post(ProjectConstants.URL_REGISTER, input);
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
    private HttpConnectionHandler connectionHandler = new HttpConnectionHandler(this) {
    	@Override
    	protected void onSucceed(JSONObject result) {
    		super.onSucceed(result);
    		// TODO 这里将直接使用LoginActivity登录成功时的代码
			storager
			.set("username", result.optString("username"))
			.set("sessionid", result.optString("sessionid"))
			.set("isLogin", true)
			.save();
			// 跳转到MainActivity
			startActivity(new Intent(getApplicationContext(), MainActivity.class));
			Toast.makeText(RegisterActivity.this, "欢迎你的到来，" + result.optString("username"), Toast.LENGTH_SHORT).show();
			finish();
    	}
    	@Override
    	protected void onFailed(JSONObject result) {
    		super.onFailed(result);
    		if(result.optString("resultMsg", "").matches("^\\d$")) {
    			// 错误请求状态码
    			Toast.makeText(RegisterActivity.this, result.optString("resultMsg", "") + "错误，请重试", Toast.LENGTH_SHORT).show();
    		} else if(result.has("formErrors")) {
				// 表单验证失败
				JSONObject formErrors = result.optJSONObject("formErrors");
				// 调用FormValidator实例将表单错误信息显示到label上
				validator.updateFormMessageFromServer(formErrors);
			} else {
				ProjectConstants.alertDialog(RegisterActivity.this, "注册失败", result.optString("resultMsg"), true);
			}
    	}
    };
}