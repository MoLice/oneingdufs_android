package com.molice.oneingdufs.activities;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.layouts.ActionBarController;
import com.molice.oneingdufs.layouts.AppMenu;
import com.molice.oneingdufs.utils.SharedPreferencesStorager;

public class UserHomeActivity extends Activity {
	private TextView username;
	private TextView studentId;
	private SharedPreferencesStorager storager;
	private AppMenu appMenu;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_home);
        
        // 设置标题
        ActionBarController.setTitle(this, R.string.user_home_title);
        
        // 设置用户名和学号
        username = (TextView) findViewById(R.id.user_home_username);
        studentId = (TextView) findViewById(R.id.user_home_studentId);
        username.setText(storager.get("username", ""));
        studentId.setText(storager.get("studentId", ""));
        
        storager = new SharedPreferencesStorager(this);
        appMenu = new AppMenu(this);
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
}
