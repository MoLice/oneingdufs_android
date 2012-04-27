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
 * ����APN������������Ϣ����ʾ���飬����{@link Constants.NOTIFICATION_MESSAGE}��JSON���ݸ�ʽ���£�<br/>
 * 'title': 'from=MoLice;date=Ӧ�÷�������ǰʱ��;type=�����typeֵ;title=��Ϣ����;', # ��Щ�ֶ���Android�ͻ��˽��н���<br/>
 * 'message': '��Ϣ����'<br/>
 * <br/>
 * ���ӱ�Activity��Back������ʱ����ǿ�Ʒ��ص�������Ϣ�б�<br/>
 * ���δ��¼״̬�յ���Ϣ���򲻽��д洢
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
        
        Log.d("����", "getCallingActivity=" + getCallingActivity());
        
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
				Log.d("JSON�쳣", "MessageDetailActivity#createDataFromBundle, e=" + e.toString());
			}
        	title.setText(data.optString("title"));
        	from.setText("���ԣ�" + data.optString("from"));
        	date.setText(data.optString("date"));
        	content.setText(data.optString("content"));
        	
        	if(new SharedPreferencesStorager(this).get("isLogin", false) && getIntent().getBooleanExtra("fromNotification", false)) {
	        	// �ѱ�����Ϣ�������ݿ�
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
		// ����Back��ʱ���жϵ�ǰActivity�Ƿ���б���ã��������ʲô��������������ǣ�Ҳ���Ǵ�Notification���ã�����BackӦ�÷���message�б�
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			// �����״̬����Notification�������������Ϊnull
			if(getCallingActivity() == null && new SharedPreferencesStorager(this).get("isLogin", false)) {
				// ������finish()��������MessageActivity�������޷�����MessageActivity��singleTopЧ��
				finish();
				startActivity(new Intent(getApplicationContext(), MessageActivity.class));
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
