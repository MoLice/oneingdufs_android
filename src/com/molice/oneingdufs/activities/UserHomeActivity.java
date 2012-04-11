package com.molice.oneingdufs.activities;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.layouts.ActionBarController;
import com.molice.oneingdufs.layouts.AppMenu;
import com.molice.oneingdufs.utils.HttpConnectionHandler;
import com.molice.oneingdufs.utils.HttpConnectionUtils;
import com.molice.oneingdufs.utils.SharedPreferencesStorager;

/**
 * ����������ҳ���ṩ��������Ϣ����Ϣ���ġ��ҵ��ճ̡��˺Ź���������<br/>
 * R.layout.user_home
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-3-28
 */
public class UserHomeActivity extends Activity {
	private Button user_info;
	
	private TextView username;
	private TextView studentId;
	private SharedPreferencesStorager storager;
	private AppMenu appMenu;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_home);
        
        // ���ñ���
        ActionBarController.setTitle(this, R.string.user_home_title);
        
        user_info = (Button) findViewById(R.id.user_home_info);
        
        // �����û�����ѧ��
        username = (TextView) findViewById(R.id.user_username);
        studentId = (TextView) findViewById(R.id.user_studentId);
        
        storager = new SharedPreferencesStorager(this);
        appMenu = new AppMenu(this);
        
        username.setText(storager.get("username", ""));
        studentId.setText(storager.get("studentId", ""));
        
        // ���ð�ť�ĵ����ת
        user_info.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//startActivity(new Intent(getApplicationContext(), UserInfoActivity.class));
				new HttpConnectionUtils(connectionHandler, storager).post("/test/", new JSONObject());
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
    	Log.d("MainActivity", "onOptionsItemSelected������");
    	return appMenu.onOptionsItemSelected(item);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	if(storager.get("isLogin", false)) {
    		// ��ʾ��¼�飬����δ��¼��
    		menu.setGroupVisible(AppMenu.NOTLOGIN, false);
    		menu.setGroupVisible(AppMenu.ISLOGIN, true);
    	} else {
    		// ��ʾδ��¼�飬���ص�¼��
    		menu.setGroupVisible(AppMenu.NOTLOGIN, true);
    		menu.setGroupVisible(AppMenu.ISLOGIN, false);
    	}
    	return super.onPrepareOptionsMenu(menu);
    }
    private HttpConnectionHandler connectionHandler = new HttpConnectionHandler(this) {
    	@Override
    	protected void onSucceed(JSONObject result) {
    		super.onSucceed(result);
    	}
    };
}
