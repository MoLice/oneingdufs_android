package com.molice.oneingdufs.activities;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.layouts.ActionBarController;
import com.molice.oneingdufs.layouts.AppMenu;
import com.molice.oneingdufs.utils.SharedPreferencesStorager;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * У԰����-У԰��<br/>
 * R.layout.life_card
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-3-26
 */
public class LifeCardActivity extends Activity {
	private AppMenu appMenu;
	private SharedPreferencesStorager storager;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.life_card);
        
        // ���ñ���
        ActionBarController.setTitle(this, R.string.life_card_title);
        // ����ActionBar��ˢ�°�ť
        ActionBarController.setActionBarButtons(this, R.layout.actionbar_buttons_refresh);
        
        Button refresh = (Button) findViewById(R.id.actionbar_refresh);
        
        appMenu = new AppMenu(this);
        storager = new SharedPreferencesStorager(this);
        
        refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(LifeCardActivity.this, "�����Ѹ���", Toast.LENGTH_SHORT).show();
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
}
