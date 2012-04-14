package com.molice.oneingdufs.activities;

import java.util.Iterator;

import org.json.JSONObject;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.layouts.ActionBarController;
import com.molice.oneingdufs.utils.HttpConnectionHandler;
import com.molice.oneingdufs.utils.HttpConnectionUtils;
import com.molice.oneingdufs.utils.ProjectConstants;
import com.molice.oneingdufs.utils.SharedPreferencesStorager;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 校园生活-校园卡<br/>
 * R.layout.life_card
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-3-26
 */
public class LifeCardActivity extends Activity {
	private SharedPreferencesStorager storager;
	
	private JSONObject items;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.life_card);
        
        // 设置标题
        ActionBarController.setTitle(this, R.string.life_card_title);
        // 设置ActionBar的刷新按钮
        ActionBarController.setActionBarButtons(this, new int[]	{R.layout.actionbar_buttons_refresh});
        
        ImageButton refresh = (ImageButton) findViewById(R.id.actionbar_refresh);
        
        storager = new SharedPreferencesStorager(this);
        
        // 初始化字段
        items = new JSONObject();
        try {
			items.putOpt("cardId", (TextView) findViewById(R.id.life_card_cardId));
			items.putOpt("balance", (TextView) findViewById(R.id.life_card_balance));
			items.putOpt("lasttime", (TextView) findViewById(R.id.life_card_lasttime));
			items.putOpt("lastspend", (TextView) findViewById(R.id.life_card_lastspend));
			items.putOpt("lastposition", (TextView) findViewById(R.id.life_card_lastposition));
		} catch (Exception e) {
			Log.d("JSON异常", "LifeCardActivity#初始化字段, e=" + e.toString());
		}
        
        
        
        // 检查是否需要立即发送更新请求
        checkLocalData();
        
        refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 发送网络请求，更新数据
				new HttpConnectionUtils(connectionHandler, LifeCardActivity.this).get(ProjectConstants.URL.life_card, null);
			}
		});
        
	}
	
    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }
    
    /**
     * 用于每次启动时判断是否已经有数据存在，若存在则读取出来，若不存在则自动发送网络请求进行更新
     * @return 不存在本地数据则返回false，存在则返回true
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
    	// 先检查是否有不存在的字段，若有则发送请求
    	for(int i=0; i<length; i++) {
    		if(!storager.has(itemsId[i])) {
    			new HttpConnectionUtils(connectionHandler, LifeCardActivity.this).get(ProjectConstants.URL.life_card, null);
    			return false;
    		}
    	}
    	// 所有字段均已存在，则将其读取，等用户主动点击更新时再发送请求更新
    	for(int i=0; i<length; i++) {
    		((TextView) items.opt(itemsId[i].substring(10))).setText(storager.get(itemsId[i], ""));
    	}
    	return true;
    }
    
    private HttpConnectionHandler connectionHandler = new HttpConnectionHandler(this) {
    	@Override
    	protected void onSucceed(JSONObject result) {
    		super.onSucceed(result);
    		
    		// 遍历返回结果，将值更新到每个字段中
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
    		Toast.makeText(LifeCardActivity.this, "数据已更新", Toast.LENGTH_SHORT).show();
    	}
    };
}