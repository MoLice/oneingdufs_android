package com.molice.oneingdufs.activities;

import java.util.Calendar;

import org.json.JSONObject;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.layouts.ActionBarController;
import com.molice.oneingdufs.layouts.AppMenu;
import com.molice.oneingdufs.layouts.TimePickerController_Water;
import com.molice.oneingdufs.utils.SharedPreferencesStorager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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
	
	private TimePickerController_Water timer;
	
	// 当前系统时间
	private int currentHour;
	// 当前时间框第一个小时值
	private int firstHour;
	// 每天10点上班
	private final int START_HOUR = 10;
	// 每天18点下班，17点的预约就顺延到明天了
	private final int END_HOUR = 17;
	
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
		
		storager = new SharedPreferencesStorager(this);
		appMenu = new AppMenu(this);
		
		// 设置当前小时
		currentHour = getCurrentHour();
		// 根据当前时间设置默认的文本框值
		firstHour = setFirstHourToEditText();
		// 根据当前时间设置默认的time值
		initTimeDialog();
		
		if(storager.get("user_building", "") != ""
			&& storager.get("user_room", "") != "") {
			// 如果已经有存储楼号、楼层、宿舍号，则直接显示
			roomAddress.setText(roomAddress.getText() + storager.get("user_building", "") + "栋" + storager.get("user_room", ""));
		} else {
			// 还没有存储住址，弹出提示并跳转到用户中心
			Builder builder = new Builder(this);
			builder.setTitle("此功能暂无法使用")
				.setMessage("需要先填写宿舍信息才能使用订水功能，现在填写吗？")
				.setPositiveButton("好的", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 跳转到“用户中心-在校相关”表单，填写完毕后返回
						Intent intent = new Intent(LifeWaterActivity.this, UserAtSchoolActivity.class);
						intent.putExtra("roomAddress required", true);
						startActivityForResult(intent, 0);
						dialog.dismiss();
					}
				})
				.setNegativeButton("离开", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				})
				.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						finish();
					}
				})
				.show();
		}
		
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
		if(requestCode == 0) {
			if(resultCode == RESULT_CANCELED) {
				// 直接finish掉
				finish();
			} else if(resultCode == RESULT_OK) {
				Bundle result = data.getExtras();
				if(result != null) {
					StringBuilder address = new StringBuilder();
					address.append(result.getString("building"))
						.append("栋")
						.append(result.getString("room"));
					// 显示宿舍号
					roomAddress.setText(roomAddress.getText() + address.toString());
				}
			}
		}
	}
	
	private void initTimeDialog() {
		// 点击弹出时间选择框，选择了时段后将其填充到文本框内
		time.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					View timepicker = LayoutInflater.from(LifeWaterActivity.this).inflate(R.layout.timepicker, null);
					Builder builder = new AlertDialog.Builder(LifeWaterActivity.this)
						.setTitle("请选择时间段")
						.setView(timepicker)
						.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// 更新选择的时段到表单
								time.setText(timer.getTime1() + "-" + timer.getTime2());
								// 更新变量，以便重新打开对话框时生效
								firstHour = new Integer(time.getText().subSequence(0, 2).toString());
								dialog.dismiss();
							}
						})
						.setNegativeButton("取消", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// 关闭对话框
								dialog.cancel();
							}
						});
					AlertDialog dialog = builder.create();
					timer = new TimePickerController_Water(firstHour, timepicker);
					dialog.show();
				}
				return true;
			}
		});
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
