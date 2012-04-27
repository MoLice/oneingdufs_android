package com.molice.oneingdufs.activities;

import org.json.JSONObject;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.androidpn.Constants;
import com.molice.oneingdufs.layouts.ActionBarController;
import com.molice.oneingdufs.utils.DatabaseHelper;
import com.molice.oneingdufs.utils.DatabaseHelper.DB;
import com.molice.oneingdufs.utils.SharedPreferencesStorager;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

/**
 * 接收APN发来的推送消息并显示详情，其中{@link Constants.NOTIFICATION_MESSAGE}的JSON数据格式如下：<br/>
 * 'title': 'from=MoLice;date=应用服务器当前时间;type=上面的type值;title=消息标题;', # 这些字段在Android客户端进行解析<br/>
 * 'message': '消息正文'<br/>
 * <br/>
 * 当从本Activity按Back键返回时，会强制返回到所有消息列表<br/>
 * 如果未登录状态收到消息，则不进行存储
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-4-24
 */
public class MessageDetailActivity extends Activity {
	private TextView title;
	private TextView from;
	private TextView date;
	private TextView content;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.message_detail);
        
        ActionBarController.setTitle(this, R.string.message_detail);
        
        Log.d("看看", "getCallingActivity=" + getCallingActivity());
        
        title = (TextView) findViewById(R.id.message_detail_title);
        from = (TextView) findViewById(R.id.message_detail_from);
        date = (TextView) findViewById(R.id.message_detail_date);
        content = (TextView) findViewById(R.id.message_detail_content);
	}
	
    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }
    
	@Override
    public void onResume() {
		super.onResume();
		createDataFromBundle();
	}
	
	private void createDataFromBundle() {
		String extra = getIntent().getStringExtra("data");
        if(!extra.equals("")) {
        	JSONObject data = null;
        	try {
        		data = new JSONObject(extra);
			} catch (Exception e) {
				Log.d("JSON异常", "MessageDetailActivity#createDataFromBundle, e=" + e.toString());
			}
        	title.setText(data.optString("title"));
        	from.setText("来自：" + data.optString("from"));
        	date.setText(data.optString("date"));
        	content.setText(data.optString("content"));
        	
        	if(new SharedPreferencesStorager(this).get("isLogin", false) && getIntent().getBooleanExtra("fromNotification", false)) {
	        	// 把本条消息存入数据库
	        	ContentValues message = new ContentValues();
	        	message.put("id", data.optString("id"));
	        	message.put("title", data.optString("title"));
	        	message.put("_from", data.optString("from"));
	        	message.put("date", data.optString("date"));
	        	message.put("content", data.optString("content"));
	        	
	        	DatabaseHelper helper = new DatabaseHelper(this);
	        	if(helper.createTableIfNotExist(DB.MESSAGE)) {
	        		helper.insert(DB.MESSAGE, message);
	        	}
	        	helper.closeAll();
        	}
        }
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 当按Back键时，判断当前Activity是否从列表调用，如果是则什么都不做。如果不是（也即是从Notification调用），则按Back应该返回message列表
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			// 如果从状态栏的Notification启动，则这里会为null
			if(getCallingActivity() == null && new SharedPreferencesStorager(this).get("isLogin", false)) {
				// 必须先finish()，再启动MessageActivity，否则无法利用MessageActivity的singleTop效果
				finish();
				startActivity(new Intent(getApplicationContext(), MessageActivity.class));
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
