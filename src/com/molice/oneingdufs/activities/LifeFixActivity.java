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
	
	// 当前时间框第一个小时值
	private int hour1;
	// 当前时间框第二个小时值
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
        
        //填充手机号码
        setPhoneNumber();
     	// 根据当前时间设置默认的文本框值
     	setTime(15, 20);
     	// 为时间文本框添加点击弹出窗口事件
     	initTimeDialog();
        
        JSONArray form = new JSONArray();
        form.put(FormValidator.createInputData(R.id.life_fix_content, "content", R.id.life_fix_content_label, "^.{3,}$", R.string.life_fix_content_label, R.string.life_fix_content_error));
        form.put(FormValidator.createInputData(R.id.life_fix_time, "time", R.id.life_fix_time_label, "^.{11}$", R.string.life_fix_time_label, R.string.life_fix_time_error));
        form.put(FormValidator.createInputData(R.id.life_fix_phone, "phone", R.id.life_fix_phone_label, "^\\d{11}$|^6\\d{2,5}$", R.string.life_fix_phone_label, R.string.life_fix_phone_error));
        validator = new FormValidator(this, form);
     	
     	if(storager.get("user_building", "") != ""
     		&& storager.get("user_room", "") != "") {
     		// 如果已经有存储楼号、楼层、宿舍号，则直接显示
     		roomAddress.setText(roomAddress.getText() + storager.get("user_building", "") + "栋" + storager.get("user_room", ""));
     	} else {
     		// 还没有存储地址，弹出提示并跳转到用户中心填写资料
     		Builder builder = new Builder(this);
     		builder.setTitle("此功能暂无法使用")
     			.setMessage("需要先填写宿舍信息才能使用报修功能，现在填写吗？")
     			.setPositiveButton("好的", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 跳转到“用户中心-在校相关”表单，填写完毕后返回
						Intent intent = new Intent(LifeFixActivity.this, UserAtSchoolActivity.class);
						intent.putExtra("roomAddress required", true);
						startActivityForResult(intent, 0);
						dialog.dismiss();
					}
				}).setNegativeButton("离开", new DialogInterface.OnClickListener() {
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
						// 发送到服务器，由服务器发送通知给对应账号
						JSONObject input = validator.getInput();
						Toast.makeText(LifeFixActivity.this, "你的报修请求已提交，谢谢！", Toast.LENGTH_LONG).show();
						Log.d("提交表单", "LifeFixActivity#submit, data=" + input.toString());
						finish();
					}
				} else {
					Toast.makeText(LifeFixActivity.this, "未修改", Toast.LENGTH_SHORT).show();
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
    	Log.d("MainActivity", "onOptionsItemSelected被调用");
    	return appMenu.onOptionsItemSelected(item);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	if(storager.get("isLogin", false)) {
    		// 显示登录组，隐藏未登录组
    		menu.setGroupVisible(AppMenu.NOTLOGIN, false);
    		menu.setGroupVisible(AppMenu.ISLOGIN, true);
    	} else {
    		// 显示未登录组，隐藏登录组
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
    	// 点击弹出时间选择框，选择了时段后将其填充到文本框内
    	time.setOnTouchListener(new OnTouchListener() {
    		@Override
			public boolean onTouch(View v, MotionEvent event) {
    			if(event.getAction() == MotionEvent.ACTION_DOWN) {
					View timepicker = LayoutInflater.from(LifeFixActivity.this).inflate(R.layout.timepicker, null);
					Builder builder = new AlertDialog.Builder(LifeFixActivity.this)
						.setTitle("请选择时间段")
						.setView(timepicker)
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								time.setText(timer.getTime1() + "-" + timer.getTime2());
								// 更新变量
								hour1 = new Integer(timer.getTime1().subSequence(0, 2).toString());
								hour2 = new Integer(timer.getTime2().subSequence(0, 2).toString());
							}
						})
						.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// 关闭对话框
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
			// 无法获取本机号码，更改phone_label的文字
			phone.setText(storager.get("phoneNumber", ""));
			TextView phone_label = (TextView) findViewById(R.id.life_fix_phone_label);
			phone_label.setText(R.string.life_fix_phone_info);
			phone_label.setTextColor(getResources().getColor(R.color.form_warning));
		}
	}
}
