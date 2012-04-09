package com.molice.oneingdufs.activities;

import java.util.Calendar;

import org.json.JSONObject;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.layouts.ActionBarController;
import com.molice.oneingdufs.layouts.AppMenu;
import com.molice.oneingdufs.layouts.TimePickerControllerForLifeWater;
import com.molice.oneingdufs.utils.Lifer;
import com.molice.oneingdufs.utils.SharedPreferencesStorager;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 校园生活-订水<br/>
 * R.layout.life_water
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-3-25
 */
public class LifeWaterActivity extends Activity {
	private TextView roomAddress;
	private Spinner number;
	private EditText time;
	private TextView time_label;
	private Button cancel;
	private Button submit;
	
	// 当前系统时间
	private int currentHour;
	// 每天10点上班
	private final int START_HOUR = 10;
	// 每天18点下班，17点的预约就顺延到明天了
	private final int END_HOUR = 17;
	
	private Lifer lifer;
	private SharedPreferencesStorager storager;
	private AppMenu appMenu;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.life_water);
		
		// 设置标题
		ActionBarController.setTitle(this, R.string.life_water_title);
		
		roomAddress = (TextView) findViewById(R.id.life_water_roomAddress);
		number = (Spinner) findViewById(R.id.life_water_number);
		time = (EditText) findViewById(R.id.life_water_time);
		time_label = (TextView) findViewById(R.id.life_water_time_label);
		cancel = (Button) findViewById(R.id.life_water_cancel);
		submit = (Button) findViewById(R.id.life_water_submit);
		
		lifer = new Lifer(this);
		storager = new SharedPreferencesStorager(this);
		appMenu = new AppMenu(this);
		
		// 设置当前小时
		currentHour = getCurrentHour();
		// 根据当前时间设置默认的文本框值
		setFirstHourToEditText();
		// 根据当前时间设置默认的time值
		lifer.initTimeDialog(time, new TimePickerControllerForLifeWater(this));
		
		// 判断是否已经填写宿舍地址
		lifer.checkRoomAddress(roomAddress);
		
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 发送到服务器，由服务器发送通知给对应账号
				JSONObject data = new JSONObject();
				try {
					data.putOpt("number", number.getSelectedItem().toString().substring(0, 1));
					data.putOpt("time", time.getText().toString());
					Toast.makeText(LifeWaterActivity.this, "已成功订水" + data.optString("number") + "桶\n将于" + data.optString("time") + "期间送到，谢谢！", Toast.LENGTH_LONG).show();
					Log.d("提交表单", "LifeWaterActivity#submit, data=" + data.toString());
				} catch (Exception e) {
					Log.d("JSON错误", "LifeWaterActivity#submit, data=" + data.toString() + ", e=" + e.toString());
				}
				finish();
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 处理{@link UserRoomAddressActivity}返回的数据
		lifer.onActivityResult(roomAddress, requestCode, resultCode, data);
	}
	
	private int getCurrentHour() {
		return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
	}
	
	private int setTime(int hour) {
		time.setText(new StringBuilder()
			.append(hour)
			.append(":00-")
			.append(hour + 1)
			.append(":00"));
		return hour;
	}
	
	private int setFirstHourToEditText() {
		int minute = Calendar.getInstance().get(Calendar.MINUTE);
		if(currentHour < START_HOUR - 1) {
			// 太早，设置为最早时段
			return setTime(10);
		}
		if(currentHour > END_HOUR) {
			// 太晚，设置为最早时段并设置说明
			time_label.setText(getResources().getString(R.string.life_water_time_info));
			time_label.setTextColor(getResources().getColor(R.color.form_warning));
			return setTime(10);
		}
		if(currentHour == END_HOUR && minute <= 15) {
			// 17:15前还可以订水
			return setTime(17);
		}
		if(currentHour == END_HOUR && minute > 15) {
			// 17:15后就不能再订水了
			return setTime(10);
		}
		if(minute < 15) {
			return setTime(currentHour);
		}
		return setTime(currentHour + 1);
	}

}
