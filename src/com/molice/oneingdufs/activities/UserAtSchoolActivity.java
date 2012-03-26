package com.molice.oneingdufs.activities;

import org.json.JSONArray;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.layouts.ActionBarController;
import com.molice.oneingdufs.layouts.AppMenu;
import com.molice.oneingdufs.utils.FormValidator;
import com.molice.oneingdufs.utils.SharedPreferencesStorager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class UserAtSchoolActivity extends Activity {
	private Spinner building;
	private Spinner layer;
	private Spinner room;
	private Button cancel;
	private Button submit;
	
	private FormValidator validator;
	private JSONArray form;
	
	private SharedPreferencesStorager storager;
	private AppMenu appMenu;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_atschool);
		
		// 设置标题
		ActionBarController.setTitle(this, R.string.user_atschool_title);
		
		building = (Spinner) findViewById(R.id.user_atschool_building);
		layer = (Spinner) findViewById(R.id.user_atschool_layer);
		room = (Spinner) findViewById(R.id.user_atschool_room);
		cancel = (Button) findViewById(R.id.user_atschool_cancel);
		submit = (Button) findViewById(R.id.user_atschool_submit);
		
		storager = new SharedPreferencesStorager(this);
		appMenu = new AppMenu(this);
		
		// 表单验证
		form = new JSONArray();
		form.put(FormValidator.createInputData(R.id.user_atschool_building, "building", R.id.user_atschool_building_label, "^\\d{1,2}$", R.string.user_atschool_building_label, R.string.user_atschool_building_error));
		form.put(FormValidator.createInputData(R.id.user_atschool_layer, "layer", R.id.user_atschool_layer_label, "^\\d{1,2}$", R.string.user_atschool_building_label, R.string.user_atschool_building_error));
		form.put(FormValidator.createInputData(R.id.user_atschool_room, "room", R.id.user_atschool_room_label, "^\\d{3}$", R.string.user_atschool_room_label, R.string.user_atschool_room_error));
		validator = new FormValidator(this, form);
		
		room.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO 发送请求，获取对应宿舍楼楼层的宿舍号列表
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					if(building.getSelectedItem().toString().matches("[\\u00b7\\u4e00-\\u9fa5]{2}")
						|| layer.getSelectedItem().toString().matches("[\\u00b7\\u4e00-\\u9fa5]{2}")) {
						// 还有没选择的
						Log.d("!!!", "还有没选择的");
						Toast.makeText(UserAtSchoolActivity.this, "请先选择楼号和楼层", Toast.LENGTH_SHORT).show();
						// 返回true，不触发click
						return true;
					} else {
						Log.d("!!!", "调用setRoomSpinnerAdapter()");
						setRoomSpinnerAdapter();
					}
				} else {
					Log.d("!!!", "!!!ACTION_DOWN");
				}
				return false;
			}
		});
		
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				checkBack();
			}
		});
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(validator.isFormModified()) {
					if(validator.isFormCorrect()) {
						// TODO 发送表单到后台，成功则将填写的信息保存到本地，不存储layer，因为已包含在room里
						storager.set("user_building", building.getSelectedItem().toString())
							.set("user_room", room.getSelectedItem().toString())
							.save();
						building.setTag(building.getSelectedItemPosition());
						layer.setTag(layer.getSelectedItemPosition());
						room.setTag(room.getSelectedItemPosition());
						// 判断是否从ForResult调用，是则退出当前视图并返回数据，否则停留在当前并更新validator的表单原始输入，以便正确响应用户再次的修改
						if(isForResult()) {
							Bundle bundle = new Bundle();
							bundle.putString("building", building.getSelectedItem().toString());
							bundle.putString("room", room.getSelectedItem().toString());
							Intent intent = new Intent();
							intent.putExtras(bundle);
							setResult(RESULT_OK, intent);
							finish();
						} else {
							validator.updateOriInputsValue();
						}
					}
				} else {
					Toast.makeText(UserAtSchoolActivity.this, "无修改", Toast.LENGTH_SHORT).show();
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
		checkBack();
    	return false;
	}
	
	/**
	 * 发送后台请求，获取对应的楼层宿舍号最大值和最小值，动态生成Adapter并添加到下拉框中
	 */
	private void setRoomSpinnerAdapter() {
		// 将得到的宿舍号列表更新到下拉框
		String layer = UserAtSchoolActivity.this.layer.getSelectedItem().toString();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(UserAtSchoolActivity.this, android.R.layout.simple_spinner_item);
		for(int i=1; i<60; i++) {
			if(i<10)
				adapter.add(layer + "0" + String.valueOf(i));
			else
				adapter.add(layer + String.valueOf(i));
		}
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapter.notifyDataSetChanged();
		room.setAdapter(adapter);
		
	}
	/**
	 * 在用户发起视图返回操作（按表单上的cancel键或按系统的Back键）时进行判断处理
	 */
	private void checkBack() {
		// 判断是否有更改
		if(validator.isFormModified()) {
			new AlertDialog.Builder(UserAtSchoolActivity.this)
				.setMessage("若返回将会丢失已填数据，确定返回吗？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						processBackIntent();
						dialog.dismiss();
						finish();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				})
				.show();
		} else {
			processBackIntent();
			finish();
		}
	}
	/**
	 * 由{@link #checkBack()}调用，判断是否通过ForResult类型调用
	 */
	private void processBackIntent() {
		if(isForResult()) {
			// 从LifeWaterActivity调用，则返回处理结果：失败
			Intent intent = new Intent();
			setResult(RESULT_CANCELED, intent);
		}
	}
	/**
	 * 由可能离开的当前视图的方法调用，用于判断当前视图是否通过ForResult的方式调用的
	 * @return
	 */
	private boolean isForResult() {
		Bundle bundle = getIntent().getExtras();
		if(bundle != null)
			return true;
		return false;
	}
}
