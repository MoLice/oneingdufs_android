package com.molice.oneingdufs.activities;

import com.molice.oneingdufs.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LifeActivity extends Activity {
	private TextView text;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.life);
        
        text = (TextView) findViewById(R.id.life_text);
        Button button = (Button) findViewById(R.id.life_result);
        
        // 判断是否从startActivityForResult调用
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
        	// 如果是则显示该Activity发送来的消息
        	text.setText(extras.getString("fromHomeActivity"));
        }
        
        button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("fromLifeActivity", "数据来自LifeActivity");
				Intent intent = new Intent();
				intent.putExtras(bundle);
				// 将当前Activity处理结果放在Intent里，返回到调用当前Activity的Activity
				setResult(RESULT_OK, intent);
				finish();
			}
		});
    }
}