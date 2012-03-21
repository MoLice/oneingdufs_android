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
        
        // �ж��Ƿ��startActivityForResult����
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
        	// ���������ʾ��Activity����������Ϣ
        	text.setText(extras.getString("fromHomeActivity"));
        }
        
        button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("fromLifeActivity", "��������LifeActivity");
				Intent intent = new Intent();
				intent.putExtras(bundle);
				// ����ǰActivity����������Intent����ص����õ�ǰActivity��Activity
				setResult(RESULT_OK, intent);
				finish();
			}
		});
    }
}