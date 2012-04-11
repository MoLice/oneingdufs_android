package com.molice.oneingdufs.activities;

import org.json.JSONArray;
import org.json.JSONObject;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.layouts.ActionBarController;
import com.molice.oneingdufs.utils.FormValidator;
import com.molice.oneingdufs.utils.HttpConnectionHandler;
import com.molice.oneingdufs.utils.HttpConnectionUtils;
import com.molice.oneingdufs.utils.ProjectConstants;
import com.molice.oneingdufs.utils.SharedPreferencesStorager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * ��д�û�������סַ�ı���ͼ��Ψһ���Ϊ��ˮ�����޽���<br/>
 * R.layout.user_roomaddress
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-3-27
 */
public class UserRoomAddressActivity extends Activity {
	private Spinner building;
	private Spinner layer;
	private Spinner room;
	private Button cancel;
	private Button submit;
	
	private FormValidator validator;
	private JSONArray form;
	
	private SharedPreferencesStorager storager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_roomaddress);
		
		// ���ñ���
		ActionBarController.setTitle(this, R.string.user_roomaddress_title);
		
		building = (Spinner) findViewById(R.id.user_roomaddress_building);
		layer = (Spinner) findViewById(R.id.user_roomaddress_layer);
		room = (Spinner) findViewById(R.id.user_roomaddress_room);
		cancel = (Button) findViewById(R.id.user_roomaddress_cancel);
		submit = (Button) findViewById(R.id.user_roomaddress_submit);
		
		storager = new SharedPreferencesStorager(this);
		
		// ����֤
		form = new JSONArray();
		form.put(FormValidator.createInputData(R.id.user_roomaddress_building, "building", R.id.user_roomaddress_building_label, "^\\d{1,2}$", R.string.user_roomaddress_building_label, R.string.user_roomaddress_building_error));
		form.put(FormValidator.createInputData(R.id.user_roomaddress_layer, "layer", R.id.user_roomaddress_layer_label, "^\\d{1,2}$", R.string.user_roomaddress_building_label, R.string.user_roomaddress_building_error));
		form.put(FormValidator.createInputData(R.id.user_roomaddress_room, "room", R.id.user_roomaddress_room_label, "^\\d{3}$", R.string.user_roomaddress_room_label, R.string.user_roomaddress_room_error));
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
						Toast.makeText(UserRoomAddressActivity.this, "����ѡ��¥�ź�¥��", Toast.LENGTH_SHORT).show();
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
						JSONObject data = new JSONObject();
						try {
							// ����layer����Ϊ��ֵ�Ѱ�����room��
							data.putOpt("building", building.getSelectedItem().toString());
							data.putOpt("room", room.getSelectedItem().toString());
						} catch (Exception e) {
							Log.d("JSON�쳣", "UserRoomAddressActivity#submit#onclick, e=" + e.toString());
						}
						// �������ݵ������
						new HttpConnectionUtils(connectionHandler, storager).post(ProjectConstants.URL_LIFE_ROOMADDRESS, data);
					}
				} else {
					Toast.makeText(UserRoomAddressActivity.this, "���޸�", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
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
		String layer = UserRoomAddressActivity.this.layer.getSelectedItem().toString();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(UserRoomAddressActivity.this, android.R.layout.simple_spinner_item);
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
			new AlertDialog.Builder(UserRoomAddressActivity.this)
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
	
	private HttpConnectionHandler connectionHandler = new HttpConnectionHandler(this) {
		@Override
		protected void onSucceed(JSONObject result) {
			super.onSucceed(result);
			Toast.makeText(UserRoomAddressActivity.this, "�Ѹ��������ַ�����������", Toast.LENGTH_SHORT).show();
			storager.set("user_roomaddress_building", building.getSelectedItem().toString())
				.set("user_roomaddress_room", room.getSelectedItem().toString())
				.save();
			// �ж��Ƿ��ForResult���ã������˳���ǰ��ͼ���������ݣ�����ͣ���ڵ�ǰ������validator�ı�ԭʼ���룬�Ա���ȷ��Ӧ�û��ٴε��޸�
			if(isForResult()) {
				Bundle bundle = new Bundle();
				bundle.putString("user_roomaddress_building", building.getSelectedItem().toString());
				bundle.putString("user_roomaddress_room", room.getSelectedItem().toString());
				Intent intent = new Intent();
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();
			} else {
				// ����ǰѡ�е�item������ӵ�Tag�ڣ�����FormValidator�ж��Ƿ��и���
				building.setTag(building.getSelectedItemPosition());
				layer.setTag(layer.getSelectedItemPosition());
				room.setTag(room.getSelectedItemPosition());
				validator.updateOriInputsValue();
			}
		}
	};
}
