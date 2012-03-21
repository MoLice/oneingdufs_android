package com.molice.oneingdufs.activities;

import org.json.JSONObject;

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
				Intent intent = new Intent(getApplicationContext(), LifeActivity.class);
				intent.putExtra("fromHomeActivity", "数据来自HomeActivity");
				startActivityForResult(intent, REQUEST_CODE);
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