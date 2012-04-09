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
 * Life类视图的公共函数
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
	 * 检查是否已填写宿舍号码，若已填写则直接显示；若未填写则跳转到{@link UserRoomAddressActivity}
	 * @param roomTextView 显示宿舍号的TextView
	 */
	public void checkRoomAddress(TextView roomTextView) {
		if (storager.get("user_roomaddress_building", "") != ""
				&& storager.get("user_roomaddress_room", "") != "") {
			// 已经有存储楼号、楼层、宿舍号，则直接显示
			roomTextView.setText(new StringBuilder()
				.append(activity.getResources().getString(R.string.life_roomAddress))
				.append(storager.get("user_roomaddress_building", ""))
				.append("栋")
				.append(storager.get("user_roomaddress_room", "")));
		} else {
			// 还没填写宿舍住址，跳转到{@link UserRoomAddressActivity}填写后返回数据
     		new AlertDialog.Builder(activity)
     			.setTitle("此功能暂无法使用")
     			.setMessage("请先填写宿舍地址，现在填写吗？")
     			.setPositiveButton("好的", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 跳转到“用户中心-在校相关”表单，填写完毕后返回
						Intent intent = new Intent(activity, UserRoomAddressActivity.class);
						intent.putExtra("roomAddress required", true);
						activity.startActivityForResult(intent, 0);
						dialog.dismiss();
					}
				}).setNegativeButton("算了", new DialogInterface.OnClickListener() {
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
	 * 处理从{@link UserRoomAddressActivity}返回的数据，需放在Activity的onActivityResult方法内
	 * @param roomTextView 显示宿舍地址的TextView
	 * @param requestCode 请求码，在{@link #checkRoomAddress(TextView) checkRoomAddress}里定义
	 * @param resultCode
	 * @param data 
	 */
	public void onActivityResult(TextView roomTextView, int requestCode, int resultCode, Intent data) {
		if(requestCode == 0) {
			if(resultCode == Activity.RESULT_CANCELED) {
				// 直接finish掉
				activity.finish();
			} else if(resultCode == Activity.RESULT_OK) {
				Bundle result = data.getExtras();
				if(result != null) {
					// 显示宿舍号
					roomTextView.setText(roomTextView.getText()
						+ new StringBuilder()
						.append(result.getString("user_roomaddress_building"))
						.append("栋")
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
				// 将当前文本框内的时间同步到弹出框内
				timer.setHour1(new Integer(time.getText().subSequence(0, 2).toString()));
				timer.setHour2(new Integer(time.getText().subSequence(6, 8).toString()));
				// 增加弹出框+/-按钮点击事件 
				timer.initTimer();
				dialog.show();
			}
			return true;
		}
	};
	private Builder initDialog() {
		return new AlertDialog.Builder(activity)
		.setTitle("请选择时间段")
		.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 将选择的时间段更新到视图上
				time.setText(timer.getTime1() + "-" + timer.getTime2());
			}
		})
		.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 关闭对话框
				dialog.cancel();
			}
		});
	}
}
