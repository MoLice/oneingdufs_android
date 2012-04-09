package com.molice.oneingdufs.utils;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.activities.UserRoomAddressActivity;
import com.molice.oneingdufs.layouts.BaseTimePickerController;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Life����ͼ�Ĺ�������
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-3-27
 */
public class Lifer {
	private Activity activity;
	private SharedPreferencesStorager storager;
	
	private BaseTimePickerController timer;
	private EditText time;
	
	private AlertDialog dialog;

	public Lifer(Activity activity) {
		this.activity = activity;
		this.storager = new SharedPreferencesStorager(activity);
	}

	/**
	 * ����Ƿ�����д������룬������д��ֱ����ʾ����δ��д����ת��{@link UserRoomAddressActivity}
	 * @param roomTextView ��ʾ����ŵ�TextView
	 */
	public void checkRoomAddress(TextView roomTextView) {
		if (storager.get("user_roomaddress_building", "") != ""
				&& storager.get("user_roomaddress_room", "") != "") {
			// �Ѿ��д洢¥�š�¥�㡢����ţ���ֱ����ʾ
			roomTextView.setText(new StringBuilder()
				.append(activity.getResources().getString(R.string.life_roomAddress))
				.append(storager.get("user_roomaddress_building", ""))
				.append("��")
				.append(storager.get("user_roomaddress_room", "")));
		} else {
			// ��û��д����סַ����ת��{@link UserRoomAddressActivity}��д�󷵻�����
     		new AlertDialog.Builder(activity)
     			.setTitle("�˹������޷�ʹ��")
     			.setMessage("������д�����ַ��������д��")
     			.setPositiveButton("�õ�", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// ��ת�����û�����-��У��ء�������д��Ϻ󷵻�
						Intent intent = new Intent(activity, UserRoomAddressActivity.class);
						intent.putExtra("roomAddress required", true);
						activity.startActivityForResult(intent, 0);
						dialog.dismiss();
					}
				}).setNegativeButton("����", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				})
				.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						activity.finish();
					}
				})
				.show();
		}
	}
	/**
	 * �����{@link UserRoomAddressActivity}���ص����ݣ������Activity��onActivityResult������
	 * @param roomTextView ��ʾ�����ַ��TextView
	 * @param requestCode �����룬��{@link #checkRoomAddress(TextView) checkRoomAddress}�ﶨ��
	 * @param resultCode
	 * @param data 
	 */
	public void onActivityResult(TextView roomTextView, int requestCode, int resultCode, Intent data) {
		if(requestCode == 0) {
			if(resultCode == Activity.RESULT_CANCELED) {
				// ֱ��finish��
				activity.finish();
			} else if(resultCode == Activity.RESULT_OK) {
				Bundle result = data.getExtras();
				if(result != null) {
					// ��ʾ�����
					roomTextView.setText(roomTextView.getText()
						+ new StringBuilder()
						.append(result.getString("user_roomaddress_building"))
						.append("��")
						.append(result.getString("user_roomaddress_room"))
						.toString());
				}
			}
		}
	}
	
	public void initTimeDialog(EditText time, BaseTimePickerController timer) {
		this.time = time;
		this.timer = timer;
		this.dialog = initDialog().create();
		this.dialog.setView(this.timer.view);
		this.time.setOnTouchListener(TimeEditTextTouchListener);
	}
	
	private OnTouchListener TimeEditTextTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(event.getAction() == MotionEvent.ACTION_DOWN) {
				// ����ǰ�ı����ڵ�ʱ��ͬ������������
				timer.setHour1(new Integer(time.getText().subSequence(0, 2).toString()));
				timer.setHour2(new Integer(time.getText().subSequence(6, 8).toString()));
				// ���ӵ�����+/-��ť����¼� 
				timer.initTimer();
				dialog.show();
			}
			return true;
		}
	};
	private Builder initDialog() {
		return new AlertDialog.Builder(activity)
		.setTitle("��ѡ��ʱ���")
		.setPositiveButton("ȷ��", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// ��ѡ���ʱ��θ��µ���ͼ��
				time.setText(timer.getTime1() + "-" + timer.getTime2());
			}
		})
		.setNegativeButton("ȡ��", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// �رնԻ���
				dialog.cancel();
			}
		});
	}
}
