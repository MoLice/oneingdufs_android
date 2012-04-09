package com.molice.oneingdufs.layouts;

import com.molice.oneingdufs.R;

import android.app.Activity;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * TimePickerController���࣬����������������hour1��hour2��Ȼ�����init()
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-3-27
 */
public class BaseTimePickerController {
	/** ��һ��ʱ����Сʱֵ */
	public int hour1;
	/** �ڶ���ʱ����Сʱֵ */
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
		// ��ʱ�����õ��ı���
		setEditTextFromHour(getHour1(), this.time1);
		setEditTextFromHour(getHour2(), this.time2);
	}
	
	/**
	 * ��hourת��Ϊ�ַ���ʱ�䣬�����õ�text�У�����setEditTextFromHour(10, text1)��text1��������Ϊ"10:00"
	 * @param hour
	 * @param text Ҫ���õ��ı���
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
