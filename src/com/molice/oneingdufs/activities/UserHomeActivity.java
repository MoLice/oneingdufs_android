package com.molice.oneingdufs.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.layouts.ActionBarController;
import com.molice.oneingdufs.utils.SharedPreferencesStorager;

/**
 * 个人中心首页，提供到个人信息、消息中心、我的日程、账号关联的链接<br/>
 * R.layout.user_home
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-3-28
 */
public class UserHomeActivity extends Activity {
	private final static int BTN_INFO = 0;
	private final static int BTN_MESSAGE = 1;
	private final static int BTN_TODO = 2;
	
	private Button user_info;
	private Button user_message;
	private Button user_todo;
	
	private TextView username;
	private TextView studentId;
	private SharedPreferencesStorager storager;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_home);
        
        // 设置标题
        ActionBarController.setTitle(this, R.string.user_home_title);
        
        storager = new SharedPreferencesStorager(this);
        
        user_info = (Button) findViewById(R.id.user_home_info);
        user_info.setTag(BTN_INFO);
        user_message = (Button) findViewById(R.id.user_home_message);
        user_message.setTag(BTN_MESSAGE);
        user_todo = (Button) findViewById(R.id.user_home_todo);
        user_todo.setTag(BTN_TODO);
        // 设置按钮的点击跳转
        user_info.setOnClickListener(listener);
        user_message.setOnClickListener(listener);
        user_todo.setOnClickListener(listener);
        
        // 设置用户名和学号
        username = (TextView) findViewById(R.id.user_username);
        studentId = (TextView) findViewById(R.id.user_studentId);
        username.setText(storager.get("username", ""));
        studentId.setText(storager.get("studentId", ""));
	}
    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }
    
    private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(v.getTag() != null) {
				int tag = Integer.parseInt(String.valueOf(v.getTag()));
				switch (tag) {
				case BTN_INFO:
					startActivity(new Intent(getApplicationContext(), UserInfoActivity.class));
					break;
				case BTN_MESSAGE:
					startActivity(new Intent(getApplicationContext(), MessageActivity.class));
					break;
				case BTN_TODO:
					break;
				default:
					break;
				}
			}
		}
	};
}
