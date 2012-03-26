package com.molice.oneingdufs.layouts;

import com.molice.oneingdufs.R;

import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class TimePickerController_Fix {
	private Button plus1;
	private Button plus2;
	private Button sub1;
	private Button sub2;
	private EditText time1;
	private EditText time2;
	
	private int hour1;
	private int hour2;
	
	public TimePickerController_Fix(int hour1, int hour2, View view) {
		this.plus1 = (Button) view.findViewById(R.id.timepicker_plus1);
		this.plus2 = (Button) view.findViewById(R.id.timepicker_plus2);
		this.sub1 = (Button) view.findViewById(R.id.timepicker_sub1);
		this.sub2 = (Button) view.findViewById(R.id.timepicker_sub2);
		this.time1 = (EditText) view.findViewById(R.id.timepicker_time1);
		this.time2 = (EditText) view.findViewById(R.id.timepicker_time2);
		this.hour1 = hour1;
		this.hour2 = hour2;
		
		this.time1.setText(String.valueOf(hour1) + ":00");
		this.time2.setText(String.valueOf(hour2) + ":00");
		
		// 设置+/-按键是否可用
		changeButtonEnabled();
		// 监听按键事件，将其与对应的文本框关联起来
		addEventListener();
	}
	
	private void addEventListener() {
		plus1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hour1 = new Integer(time1.getText().toString().substring(0, 2)) + 1;
				time1.setText(String.valueOf(hour1) + ":00");
				changeButtonEnabled();
			}
		});
		plus2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hour2 = new Integer(time2.getText().toString().substring(0, 2)) + 1;
				time2.setText(String.valueOf(hour2) + ":00");
				changeButtonEnabled();
			}
		});
		sub1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hour1 = new Integer(time1.getText().toString().substring(0, 2)) - 1;
				time1.setText(String.valueOf(hour1) + ":00");
				changeButtonEnabled();
			}
		});
		sub2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hour2 = new Integer(time2.getText().toString().substring(0, 2)) - 1;
				time2.setText(String.valueOf(hour2) + ":00");
				changeButtonEnabled();
			}
		});
	}
	
	private void changeButtonEnabled() {
		if(hour1 == 10) {
			sub1.setEnabled(false);
			plus1.setEnabled(true);
		} else if(hour1 == 21) {
			sub1.setEnabled(true);
			plus1.setEnabled(false);
		} else {
			if(hour1 + 1 == hour2) {
				sub1.setEnabled(true);
				plus1.setEnabled(false);
			} else {
				sub1.setEnabled(true);
				plus1.setEnabled(true);
			}
		}
		if(hour2 == 11) {
			sub2.setEnabled(false);
			plus2.setEnabled(true);
		} else if(hour2 == 22) {
			sub2.setEnabled(true);
			plus2.setEnabled(false);
		} else {
			if(hour1 + 1 == hour2) {
				sub2.setEnabled(false);
				plus2.setEnabled(true);
			} else {
				sub2.setEnabled(true);
				plus2.setEnabled(true);
			}
		}
	}
	
	public Editable getTime1() {
		return time1.getText();
	}
	public Editable getTime2() {
		return time2.getText();
	}
}
