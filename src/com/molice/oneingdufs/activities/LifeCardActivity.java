package com.molice.oneingdufs.activities;

import java.util.Iterator;

import org.json.JSONObject;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.layouts.ActionBarController;
import com.molice.oneingdufs.layouts.AppMenu;
import com.molice.oneingdufs.utils.HttpConnectionHandler;
import com.molice.oneingdufs.utils.HttpConnectionUtils;
import com.molice.oneingdufs.utils.ProjectConstants;
import com.molice.oneingdufs.utils.SharedPreferencesStorager;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
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
	
	private JSONObject items;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.life_card);
        
        // ���ñ���
        ActionBarController.setTitle(this, R.string.life_card_title);
        // ����ActionBar��ˢ�°�ť
        ActionBarController.setActionBarButtons(this, new int[]	{R.layout.actionbar_buttons_refresh});
        
        ImageButton refresh = (ImageButton) findViewById(R.id.actionbar_refresh);
        
        appMenu = new AppMenu(this);
        storager = new SharedPreferencesStorager(this);
        
        // ��ʼ���ֶ�
        items = new JSONObject();
        try {
			items.putOpt("cardId", (TextView) findViewById(R.id.life_card_cardId));
			items.putOpt("balance", (TextView) findViewById(R.id.life_card_balance));
			items.putOpt("lasttime", (TextView) findViewById(R.id.life_card_lasttime));
			items.putOpt("lastspend", (TextView) findViewById(R.id.life_card_lastspend));
			items.putOpt("lastposition", (TextView) findViewById(R.id.life_card_lastposition));
		} catch (Exception e) {
			Log.d("JSON�쳣", "LifeCardActivity#��ʼ���ֶ�, e=" + e.toString());
		}
        
        
        
        // ����Ƿ���Ҫ�������͸�������
        checkLocalData();
        
        refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// �����������󣬸�������
				new HttpConnectionUtils(connectionHandler, storager).get(ProjectConstants.URL_LIFE_CARD, null);
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
    
    /**
     * ����ÿ������ʱ�ж��Ƿ��Ѿ������ݴ��ڣ����������ȡ�����������������Զ���������������и���
     * @return �����ڱ��������򷵻�false�������򷵻�true
     */
    private boolean checkLocalData() {
    	String[] itemsId = {
            	"life_card_cardId",
            	"life_card_balance",
            	"life_card_lasttime",
            	"life_card_lastspend",
            	"life_card_lastposition",
            };
    	int length = itemsId.length;
    	// �ȼ���Ƿ��в����ڵ��ֶΣ�������������
    	for(int i=0; i<length; i++) {
    		if(!storager.has(itemsId[i])) {
    			new HttpConnectionUtils(connectionHandler, storager).get(ProjectConstants.URL_LIFE_CARD, null);
    			return false;
    		}
    	}
    	// �����ֶξ��Ѵ��ڣ������ȡ�����û������������ʱ�ٷ����������
    	for(int i=0; i<length; i++) {
    		((TextView) items.opt(itemsId[i].substring(10))).setText(storager.get(itemsId[i], ""));
    	}
    	return true;
    }
    
    private HttpConnectionHandler connectionHandler = new HttpConnectionHandler(this) {
    	@Override
    	protected void onSucceed(JSONObject result) {
    		super.onSucceed(result);
    		
    		// �������ؽ������ֵ���µ�ÿ���ֶ���
    		@SuppressWarnings("unchecked")
			Iterator<String> iterator = result.keys();
        	while(iterator.hasNext()) {
        		String key = iterator.next();
        		if(!key.equals("success") && !key.equals("resultMsg")) {
        			((TextView) items.opt(key)).setText(result.optString(key));
        			storager.set("life_card_" + key, result.optString(key));
        		}
        	}
        	storager.save();
    		Toast.makeText(LifeCardActivity.this, "�����Ѹ���", Toast.LENGTH_SHORT).show();
    	}
    };
}