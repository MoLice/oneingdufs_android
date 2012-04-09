package com.molice.oneingdufs.layouts;

import com.molice.oneingdufs.R;

import android.app.Activity;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * TimePickerController基类，生成类对象后，先设置hour1、hour2，然后调用init()
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-3-27
 */
public class BaseTimePickerController {
	/** 第一个时间框的小时值 */
	public int hour1;
	/** 第二个时间框的小时值 */
	public int hour2;

	public View view;

	private Button plus1;
	private Button plus2;
	private Button sub1;
	private Button sub2;
	private EditText time1;
	private EditText time2;
	
	public BaseTimePickerController(Activity activity) {
		this.view = LayoutInflater.from(activity).inflate(R.layout.timepicker, null);;
		this.plus1 = (Button) view.findViewById(R.id.timepicker_plus1);
		this.plus2 = (Button) view.findViewById(R.id.timepicker_plus2);
		this.sub1 = (Button) view.findViewById(R.id.timepicker_sub1);
		this.sub2 = (Button) view.findViewById(R.id.timepicker_sub2);
		this.time1 = (EditText) view.findViewById(R.id.timepicker_time1);
		this.time2 = (EditText) view.findViewById(R.id.timepicker_time2);
	}

	public void initTimer() {
		// 将时间设置到文本框
		setEditTextFromHour(getHour1(), this.time1);
		setEditTextFromHour(getHour2(), this.time2);
	}
	
	/**
	 * 将hour转换为字符串时间，并设置到text中，例如setEditTextFromHour(10, text1)将text1内容设置为"10:00"
	 * @param hour
	 * @param text 要设置的文本框
	 */
	public void setEditTextFromHour(int hour, EditText text) {
		text.setText(String.valueOf(hour) + ":00");
	}
	
	public int getHour1() {
		return this.hour1;
	}

	public void setHour1(int hour1) {
		this.hour1 = hour1;
		setEditTextFromHour(hour1, this.time1);
	}

	public int getHour2() {
		return this.hour2;
	}

	public void setHour2(int hour2) {
		this.hour2 = hour2;
		setEditTextFromHour(hour2, this.time2);
	}

	protected Button getPlus1() {
		return plus1;
	}

	protected void setPlus1(Button plus1) {
		this.plus1 = plus1;
	}

	protected Button getPlus2() {
		return plus2;
	}

	protected void setPlus2(Button plus2) {
		this.plus2 = plus2;
	}

	protected Button getSub1() {
		return sub1;
	}

	protected void setSub1(Button sub1) {
		this.sub1 = sub1;
	}

	protected Button getSub2() {
		return sub2;
	}

	protected void setSub2(Button sub2) {
		this.sub2 = sub2;
	}

	public Editable getTime1() {
		return time1.getEditableText();
	}

	public Editable getTime2() {
		return time2.getEditableText();
	}

}
