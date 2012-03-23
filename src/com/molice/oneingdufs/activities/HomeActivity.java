package com.molice.oneingdufs.activities;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.molice.oneingdufs.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends Activity {
	private TextView text;
	private int REQUEST_CODE = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        
        this.text = (TextView) findViewById(R.id.home_text);
        Button login = (Button) findViewById(R.id.home_life);
        
        login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Intent intent = new Intent(getApplicationContext(), LifeActivity.class);
				//intent.putExtra("fromHomeActivity", "数据来自HomeActivity");
				//startActivityForResult(intent, REQUEST_CODE);
				String jsessionid = "";
				DefaultHttpClient client = new DefaultHttpClient();
				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, 10*1000);
				HttpConnectionParams.setSoTimeout(httpParams, 20 * 1000);
				HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);
				// get
				/*HttpGet get = new HttpGet("http://10.0.2.2:7070/index.do");
				get.setParams(httpParams);
				try {
					HttpResponse response = client.execute(get);
					if(response.getStatusLine().getStatusCode() == 200) {
						HttpEntity entity = response.getEntity();
						String result = null;
						if(entity != null) {
							result = EntityUtils.toString(entity);
							jsessionid = response.getHeaders("Set-Cookie")[0].getValue();
							jsessionid = jsessionid.split(";")[0];
							Log.d("MOLICE", "result = " + result);
							Log.d("MOLICE", jsessionid);
						} else {
							Log.d("MOLICE", "result=null");
						}
					} else {
						Log.d("MOLICE", String.valueOf(response.getStatusLine().getStatusCode()));
					}
				} catch (Exception e) {
					Log.d("MOLICE", "get,e=" + e.toString());
				}*/
				
				// post
				HttpPost post = new HttpPost("http://10.0.2.2:7070/notification.do?action=send");
				// params
				List<NameValuePair> result = new ArrayList<NameValuePair>();
				result.add(new BasicNameValuePair("broadcast", "Y"));
				result.add(new BasicNameValuePair("username", ""));
				result.add(new BasicNameValuePair("title", "Title"));
				result.add(new BasicNameValuePair("message", "hello world"));
				result.add(new BasicNameValuePair("uri", ""));
				UrlEncodedFormEntity formEntity = null;
				try {
					formEntity = new UrlEncodedFormEntity(result);
				} catch (Exception e) {
					Log.d("MOLICE", "e=" + e.toString());
				}
				post.setParams(httpParams);
				post.setEntity(formEntity);
				//post.setHeader("Cookie", jsessionid + ";");
				try {
					HttpResponse response = client.execute(post);
					if(response.getStatusLine().getStatusCode() == 200) {
						HttpEntity entity = response.getEntity();
						String result2 = null;
						if(entity != null) {
							result2 = EntityUtils.toString(entity);
							Log.d("MOLICE", "result2=" + result2);
						} else {
							Log.d("MOLICE", "返回的结果为空");
						}
						
					} else {
						Log.d("MOLICE", "status=" + String.valueOf(response.getStatusLine().getStatusCode()));
					}
				} catch (Exception e) {
					Log.d("MOLICE", "e=" + e.toString());
				}
				
			}
		});
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(requestCode == REQUEST_CODE) {
    		if(resultCode == RESULT_CANCELED) {
    			text.setText("中断或发生错误，未按理想地返回了");
    		} else if(resultCode == RESULT_OK) {
    			Bundle extras = data.getExtras();
    			if(extras != null) {
    				text.setText(extras.getString("fromLifeActivity"));
    			}
    		}
    	}
    }
}