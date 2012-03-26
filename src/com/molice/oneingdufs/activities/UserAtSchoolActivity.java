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
		
		// ���ñ���
		ActionBarController.setTitle(this, R.string.user_atschool_title);
		
		building = (Spinner) findViewById(R.id.user_atschool_building);
		layer = (Spinner) findViewById(R.id.user_atschool_layer);
		room = (Spinner) findViewById(R.id.user_atschool_room);
		cancel = (Button) findViewById(R.id.user_atschool_cancel);
		submit = (Button) findViewById(R.id.user_atschool_submit);
		
		storager = new SharedPreferencesStorager(this);
		appMenu = new AppMenu(this);
		
		// ����֤
		form = new JSONArray();
		form.put(FormValidator.createInputData(R.id.user_atschool_building, "building", R.id.user_atschool_building_label, "^\\d{1,2}$", R.string.user_atschool_building_label, R.string.user_atschool_building_error));
		form.put(FormValidator.createInputData(R.id.user_atschool_layer, "layer", R.id.user_atschool_layer_label, "^\\d{1,2}$", R.string.user_atschool_building_label, R.string.user_atschool_building_error));
		form.put(FormValidator.createInputData(R.id.user_atschool_room, "room", R.id.user_atschool_room_label, "^\\d{3}$", R.string.user_atschool_room_label, R.string.user_atschool_room_error));
		validator = new FormValidator(this, form);
		
		room.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO �������󣬻�ȡ��Ӧ����¥¥���������б�
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					if(building.getSelectedItem().toString().matches("[\\u00b7\\u4e00-\\u9fa5]{2}")
						|| layer.getSelectedItem().toString().matches("[\\u00b7\\u4e00-\\u9fa5]{2}")) {
						// ����ûѡ���
						Log.d("!!!", "����ûѡ���");
						Toast.makeText(UserAtSchoolActivity.this, "����ѡ��¥�ź�¥��", Toast.LENGTH_SHORT).show();
						// ����true��������click
						return true;
					} else {
						Log.d("!!!", "����setRoomSpinnerAdapter()");
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
						// TODO ���ͱ�����̨���ɹ�����д����Ϣ���浽���أ����洢layer����Ϊ�Ѱ�����room��
						storager.set("user_building", building.getSelectedItem().toString())
							.set("user_room", room.getSelectedItem().toString())
							.save();
						building.setTag(building.getSelectedItemPosition());
						layer.setTag(layer.getSelectedItemPosition());
						room.setTag(room.getSelectedItemPosition());
						// �ж��Ƿ��ForResult���ã������˳���ǰ��ͼ���������ݣ�����ͣ���ڵ�ǰ������validator�ı�ԭʼ���룬�Ա���ȷ��Ӧ�û��ٴε��޸�
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
					Toast.makeText(UserAtSchoolActivity.this, "���޸�", Toast.LENGTH_SHORT).show();
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
		checkBack();
    	return false;
	}
	
	/**
	 * ���ͺ�̨���󣬻�ȡ��Ӧ��¥����������ֵ����Сֵ����̬����Adapter����ӵ���������
	 */
	private void setRoomSpinnerAdapter() {
		// ���õ���������б���µ�������
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
	 * ���û�������ͼ���ز����������ϵ�cancel����ϵͳ��Back����ʱ�����жϴ���
	 */
	private void checkBack() {
		// �ж��Ƿ��и���
		if(validator.isFormModified()) {
			new AlertDialog.Builder(UserAtSchoolActivity.this)
				.setMessage("�����ؽ��ᶪʧ�������ݣ�ȷ��������")
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						processBackIntent();
						dialog.dismiss();
						finish();
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
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
	 * ��{@link #checkBack()}���ã��ж��Ƿ�ͨ��ForResult���͵���
	 */
	private void processBackIntent() {
		if(isForResult()) {
			// ��LifeWaterActivity���ã��򷵻ش�������ʧ��
			Intent intent = new Intent();
			setResult(RESULT_CANCELED, intent);
		}
	}
	/**
	 * �ɿ����뿪�ĵ�ǰ��ͼ�ķ������ã������жϵ�ǰ��ͼ�Ƿ�ͨ��ForResult�ķ�ʽ���õ�
	 * @return
	 */
	private boolean isForResult() {
		Bundle bundle = getIntent().getExtras();
		if(bundle != null)
			return true;
		return false;
	}
}
