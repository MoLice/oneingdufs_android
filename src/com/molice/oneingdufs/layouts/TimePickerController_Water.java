package com.molice.oneingdufs.layouts;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.activities.LifeWaterActivity;

import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * 用于{@link LifeWaterActivity}的时间控件控制器，会自动根据当前时间设置初始值<br/>
 * 若当前是10:15，则默认值是10:00-11:00<br/>
 * 若当前是10:16，则默认值是11:00-12:00
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-3-26
 */
public class TimePickerController_Water {
	private Button plus1;
	private Button plus2;
	private Button sub1;
	private Button sub2;
	private EditText time1;
	private EditText time2;
	
	public TimePickerController_Water(int firstHour, View view) {
		this.plus1 = (Button) view.findViewById(R.id.timepicker_plus1);
		this.plus2 = (Button) view.findViewById(R.id.timepicker_plus2);
		this.sub1 = (Button) view.findViewById(R.id.timepicker_sub1);
		this.sub2 = (Button) view.findViewById(R.id.timepicker_sub2);
		this.time1 = (EditText) view.findViewById(R.id.timepicker_time1);
		this.time2 = (EditText) view.findViewById(R.id.timepicker_time2);
		
		time1.setText(String.valueOf(firstHour) + ":00");
		time2.setText(String.valueOf(firstHour + 1) + ":00");
		
		// 设置+/-按键是否可用
		changeButtonEnabled(firstHour);
		// 监听按键事件，将其与对应的文本框关联起来
		addEventListener();
	}
	
	private void addEventListener() {
		plus1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Editable text = time1.getText();
				int value = new Integer(text.toString().substring(0, 2));
				if(value > 9 && value < 17) {
					time1.setText(String.valueOf(value + 1) + ":00");
					time2.setText(String.valueOf(value + 2) + ":00");
				} else if(value == 17) {
					// 灰掉按钮
					plus1.setEnabled(false);
				}
				changeButtonEnabled(value + 1);
			}
		});
		plus2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Editable text = time2.getText();
				int value = new Integer(text.toString().substring(0, 2));
				if(value > 10 && value < 18) {
					time1.setText(String.valueOf(value) + ":00");
					time2.setText(String.valueOf(value + 1) + ":00");
				}
				changeButtonEnabled(value);
			}
		});
		sub1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Editable text = time1.getText();
				int value = new Integer(text.toString().substring(0, 2));
				if(value > 10 && value < 18) {
					time1.setText(String.valueOf(value - 1) + ":00");
					time2.setText(String.valueOf(value) + ":00");
				}
				changeButtonEnabled(value - 1);
			}
		});
		sub2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Editable text = time2.getText();
				int value = new Integer(text.toString().substring(0, 2));
				if(value > 11 && value < 19) {
					time1.setText(String.valueOf(value - 2) + ":00");
					time2.setText(String.valueOf(value - 1) + ":00");
				}
				changeButtonEnabled(value - 2);
			}
		});
	}
	
	public Editable getTime1() {
		return time1.getText();
	}
	public Editable getTime2() {
		return time2.getText();
	}
	
	/**
	 * 根据当前输入值的不同，将不同按钮设置为不可用
	 * @param value 第一个小时框的的小时值
	 */
	private void changeButtonEnabled(int value) {
		if(value == 10) {
			// 灰掉两个sub
			sub1.setEnabled(false);
			sub2.setEnabled(false);
			plus1.setEnabled(true);
			plus2.setEnabled(true);
		} else if(value == 17) {
			// 灰掉两个plus
			sub1.setEnabled(true);
			sub2.setEnabled(true);
			plus1.setEnabled(false);
			plus2.setEnabled(false);
		} else {
			// 恢复所有
			sub1.setEnabled(true);
			sub2.setEnabled(true);
			plus1.setEnabled(true);
			plus2.setEnabled(true);
		}
	}
}
