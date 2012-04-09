package com.molice.oneingdufs.layouts;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

public class TimePickerControllerForLifeFix extends BaseTimePickerController {

	public TimePickerControllerForLifeFix(Activity activity) {
		super(activity);
	}

	@Override
	public void initTimer() {
		super.initTimer();
		setButtonEnabledByHour();
		addEventListener();
	}
	
	/**
	 * 根据时间改变按钮的可用状态
	 */
	private void setButtonEnabledByHour() {
		int hour1 = getHour1();
		int hour2 = getHour2();
		if(hour1 == 10) {
			getSub1().setEnabled(false);
			getSub2().setEnabled(true);
		} else if(hour1 == 21) {
			getSub1().setEnabled(true);
			getPlus1().setEnabled(false);
		} else {
			if(hour1 + 1 == hour2) {
				getSub1().setEnabled(true);
				getPlus1().setEnabled(false);
			} else {
				getSub1().setEnabled(true);
				getPlus1().setEnabled(true);
			}
		}
		if(hour2 == 11) {
			getSub2().setEnabled(false);
			getPlus2().setEnabled(true);
		} else if(hour2 == 22) {
			getSub2().setEnabled(true);
			getPlus2().setEnabled(false);
		} else {
			if(hour1 + 1 == hour2) {
				getSub2().setEnabled(false);
				getPlus2().setEnabled(true);
			} else {
				getSub2().setEnabled(true);
				getPlus2().setEnabled(true);
			}
		}
	}
	/**
	 * 添加按钮的点击事件，在时间中改变文本框和hour1、hour2的值
	 */
	private void addEventListener() {
		getPlus1().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setHour1(getHour1() + 1);
				setButtonEnabledByHour();
			}
		});
		getPlus2().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setHour2(getHour2() + 1);
				setButtonEnabledByHour();
			}
		});
		getSub1().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setHour1(getHour1() - 1);
				setButtonEnabledByHour();
			}
		});
		getSub2().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setHour2(getHour2() -1);
				setButtonEnabledByHour();
			}
		});
	}
}

