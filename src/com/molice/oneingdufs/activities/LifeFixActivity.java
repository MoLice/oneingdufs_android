package com.molice.oneingdufs.activities;

import org.json.JSONArray;
import org.json.JSONObject;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.layouts.AppMenu;
import com.molice.oneingdufs.layouts.TimePickerController_Fix;
import com.molice.oneingdufs.utils.FormValidator;
import com.molice.oneingdufs.utils.SharedPreferencesStorager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LifeFixActivity extends Activity{
	private TextView roomAddress;
	private EditText time;
	private EditText phone;
	private Button cancel;
	private Button submit;
	
	// ��ǰʱ����һ��Сʱֵ
	private int hour1;
	// ��ǰʱ���ڶ���Сʱֵ
	private int hour2;
	private TimePickerController_Fix timer;
	
	private AppMenu appMenu;
	private SharedPreferencesStorager storager;
	private FormValidator validator;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.life_fix);
        
        roomAddress = (TextView) findViewById(R.id.life_fix_roomAddress);
        time = (EditText) findViewById(R.id.life_fix_time);
        phone = (EditText) findViewById(R.id.life_fix_phone);
        cancel = (Button) findViewById(R.id.life_fix_cancel);
        submit = (Button) findViewById(R.id.life_fix_submit);
        
        appMenu = new AppMenu(this);
        storager = new SharedPreferencesStorager(this);
        
        //����ֻ�����
        setPhoneNumber();
     	// ���ݵ�ǰʱ������Ĭ�ϵ��ı���ֵ
     	setTime(15, 20);
     	// Ϊʱ���ı�����ӵ�����������¼�
     	initTimeDialog();
        
        JSONArray form = new JSONArray();
        form.put(FormValidator.createInputData(R.id.life_fix_content, "content", R.id.life_fix_content_label, "^.{3,}$", R.string.life_fix_content_label, R.string.life_fix_content_error));
        form.put(FormValidator.createInputData(R.id.life_fix_time, "time", R.id.life_fix_time_label, "^.{11}$", R.string.life_fix_time_label, R.string.life_fix_time_error));
        form.put(FormValidator.createInputData(R.id.life_fix_phone, "phone", R.id.life_fix_phone_label, "^\\d{11}$|^6\\d{2,5}$", R.string.life_fix_phone_label, R.string.life_fix_phone_error));
        validator = new FormValidator(this, form);
     	
     	if(storager.get("user_building", "") != ""
     		&& storager.get("user_room", "") != "") {
     		// ����Ѿ��д洢¥�š�¥�㡢����ţ���ֱ����ʾ
     		roomAddress.setText(roomAddress.getText() + storager.get("user_building", "") + "��" + storager.get("user_room", ""));
     	} else {
     		// ��û�д洢��ַ��������ʾ����ת���û�������д����
     		Builder builder = new Builder(this);
     		builder.setTitle("�˹������޷�ʹ��")
     			.setMessage("��Ҫ����д������Ϣ����ʹ�ñ��޹��ܣ�������д��")
     			.setPositiveButton("�õ�", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// ��ת�����û�����-��У��ء�������д��Ϻ󷵻�
						Intent intent = new Intent(LifeFixActivity.this, UserAtSchoolActivity.class);
						intent.putExtra("roomAddress required", true);
						startActivityForResult(intent, 0);
						dialog.dismiss();
					}
				}).setNegativeButton("�뿪", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						finish();
					}
				})
				.show();
     	}
     	
     	cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				validator.checkBackIfFormModified();
			}
		});
     	submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(validator.isFormModified()) {
					if(validator.isFormCorrect()) {
						// ���͵����������ɷ���������֪ͨ����Ӧ�˺�
						JSONObject input = validator.getInput();
						Toast.makeText(LifeFixActivity.this, "��ı����������ύ��лл��", Toast.LENGTH_LONG).show();
						Log.d("�ύ��", "LifeFixActivity#submit, data=" + input.toString());
						finish();
					}
				} else {
					Toast.makeText(LifeFixActivity.this, "δ�޸�", Toast.LENGTH_SHORT).show();
				}

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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if(keyCode == KeyEvent.KEYCODE_BACK) {
    		validator.checkBackIfFormModified();
    	}
    	return super.onKeyDown(keyCode, event);
    }
    
    private void initTimeDialog() {
    	// �������ʱ��ѡ���ѡ����ʱ�κ�����䵽�ı�����
    	time.setOnTouchListener(new OnTouchListener() {
    		@Override
			public boolean onTouch(View v, MotionEvent event) {
    			if(event.getAction() == MotionEvent.ACTION_DOWN) {
					View timepicker = LayoutInflater.from(LifeFixActivity.this).inflate(R.layout.timepicker, null);
					Builder builder = new AlertDialog.Builder(LifeFixActivity.this)
						.setTitle("��ѡ��ʱ���")
						.setView(timepicker)
						.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								time.setText(timer.getTime1() + "-" + timer.getTime2());
								// ���±���
								hour1 = new Integer(timer.getTime1().subSequence(0, 2).toString());
								hour2 = new Integer(timer.getTime2().subSequence(0, 2).toString());
							}
						})
						.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// �رնԻ���
								dialog.cancel();
							}
						});
					AlertDialog dialog = builder.create();
					timer = new TimePickerController_Fix(hour1, hour2, timepicker);
					dialog.show();
    			}
				return true;
			}
		});
    }
    
	private void setTime(int hour1, int hour2) {
		time.setText(new StringBuilder()
			.append(hour1)
			.append(":00-")
			.append(hour2)
			.append(":00"));
		this.hour1 = hour1;
		this.hour2 = hour2;
	}
	
	private void setPhoneNumber() {
		if(storager.get("phoneNumber", "") != "") {
			// �޷���ȡ�������룬����phone_label������
			phone.setText(storager.get("phoneNumber", ""));
			TextView phone_label = (TextView) findViewById(R.id.life_fix_phone_label);
			phone_label.setText(R.string.life_fix_phone_info);
			phone_label.setTextColor(getResources().getColor(R.color.form_warning));
		}
	}
}
