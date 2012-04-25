package com.molice.oneingdufs.activities;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.layouts.ActionBarController;
import com.molice.oneingdufs.utils.DatabaseHelper;
import com.molice.oneingdufs.utils.DatabaseHelper.DB;

public class MessageActivity extends Activity{
	final static int ONBEFORESTART = 0;
	final static int ONSTART = 1;
	final static int ONSUCCESS = 2;
	final static int ONCOMPLETE = 3;
	
	private ListView list;
	private JSONObject[] data;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message);
		
		ActionBarController.setTitle(this, R.string.message);
		
		list = (ListView) findViewById(R.id.message_list);
	}
	@Override
	public void onResume() {
		super.onResume();
		handler.sendMessage(Message.obtain(handler, ONBEFORESTART));
		handler.sendMessage(Message.obtain(handler, ONSTART));
	}
	
    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }
    
    OnItemClickListener listener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent intent = new Intent(getApplicationContext(), MessageDetailActivity.class);
			intent.putExtra("data", ((ViewHolder) view.getTag()).data.toString());
			startActivity(intent);
		}
	};
    
    Handler handler = new Handler() {
    	private ProgressDialog progressDialog;
    	
//    	private void onBeforeStart() {
//        	progressDialog = ProgressDialog.show(MessageActivity.this, null, "请稍等...", true, true);
//        }
    	
    	private void onStart() {
        	if(progressDialog != null && progressDialog.isShowing()) {
        		progressDialog.setMessage("正在读取...");
        	}
        	DatabaseHelper helper = new DatabaseHelper(MessageActivity.this);
        	if(!helper.isTableExist(DB.MESSAGE)) {
        		handler.sendMessage(Message.obtain(handler, ONCOMPLETE));
        		return;
        	}
        	Cursor cursor = helper.getReadableDatabase().query(DB.MESSAGE, null, null, null, null, null, null);
        	int count = cursor.getCount();
        	if(count == 0) {
        		if(progressDialog != null && progressDialog.isShowing()) {
        			progressDialog.dismiss();
        		}
        		return;
        	}
        	
        	data = new JSONObject[count];
        	while(cursor.moveToNext()) {
        		JSONObject message = new JSONObject();
        		try {
        			message.putOpt("id", cursor.getString(0));
					message.putOpt("title", cursor.getString(1));
					message.putOpt("from", cursor.getString(2));
					message.putOpt("content", cursor.getString(3));
					message.putOpt("date", cursor.getString(4));
					data[cursor.getPosition()] = message;
				} catch (Exception e) {
					Log.d("数据库读取异常", "MessageActivity#handler#onStart(), position=" + cursor.getPosition() + ", e=" + e.toString());
					handler.sendMessage(Message.obtain(handler, ONCOMPLETE));
				}
        	}
        	if(data.length > 0) {
        		handler.sendMessage(Message.obtain(handler, ONSUCCESS));
        	}
        	helper.closeAll();
        	cursor.close();
        }
    	
    	private void onSuccess() {
    		if(progressDialog != null && progressDialog.isShowing()) {
    			progressDialog.dismiss();
    		}
    		list.setAdapter(new MessageListAdapter(data));
    		list.setOnItemClickListener(listener);
    	}
    	
    	private void onComplete() {
    		if(progressDialog != null && progressDialog.isShowing()) {
    			progressDialog.dismiss();
    		}
    	}
    	
    	@Override
    	public void handleMessage(Message message) {
    		switch (message.what) {
			case ONBEFORESTART:
				// 开始读取前，弹出onProgressDialog
//				onBeforeStart();
				break;
			case ONSTART:
				// 开始读取
				onStart();
				break;
			case ONSUCCESS:
				onSuccess();
				break;
			case ONCOMPLETE:
				onComplete();
				break;
			}
    	}
    };
	
	class MessageListAdapter extends BaseAdapter {
		private JSONObject[] items;
		private LayoutInflater inflater;
		
		public MessageListAdapter(JSONObject[] items) {
			this.items = items;
			inflater = LayoutInflater.from(MessageActivity.this);
		}

		@Override
		public int getCount() {
			return items.length;
		}

		@Override
		public Object getItem(int position) {
			return items[position];
		}

		@Override
		public long getItemId(int position) {
			return items[position].optInt("id", 0);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.message_item, null);
				holder.up = (LinearLayout) convertView.findViewById(R.id.message_item_up);
				holder.title = (TextView) convertView.findViewById(R.id.message_item_title);
				holder.content = (TextView) convertView.findViewById(R.id.message_item_content);
				holder.time = (TextView) convertView.findViewById(R.id.message_item_time);
				holder.data = items[position];
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.title.setText(items[position].optString("title", "（无标题）"));
			holder.content.setText(items[position].optString("content", "无内容"));
			holder.time.setText(items[position].optString("time", "2012-04-24"));
			
			LayoutParams params1 = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			LayoutParams params2 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT, 1);
			holder.up.setLayoutParams(params1);
			holder.title.setLayoutParams(params2);
			holder.content.setLayoutParams(params2);
			holder.content.setLayoutParams(params1);
			
			return convertView;
		}
	}
	
	static class ViewHolder {
		LinearLayout up;
		TextView title;
		TextView content;
		TextView time;
		JSONObject data;
	}
}
