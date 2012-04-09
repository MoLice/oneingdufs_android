package com.molice.oneingdufs.layouts;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

public class TimePickerControllerForLifeWater extends BaseTimePickerController {

	public TimePickerControllerForLifeWater(Activity activity) {
		super(activity);
	}

	@Override
	public void initTimer() {
		super.initTimer();
		setButtonEnabledByHour();
		addEventListener();
	}
	
	/**
	 * 根据时间改变按钮的可用状态<br/>
	 * 因为两个框间隔固定为1小时，所以这里只判断一个框即可
	 */
	private void setButtonEnabledByHour() {
		int hour1 = getHour1();
		if(hour1 == 10) {
			// 灰掉两个sub
			getSub1().setEnabled(false);
			getSub2().setEnabled(false);
			getPlus1().setEnabled(true);
			getPlus2().setEnabled(true);
		} else if(hour1 == 17) {
			// 灰掉两个plus
			getSub1().setEnabled(true);
			getSub2().setEnabled(true);
			getPlus1().setEnabled(false);
			getPlus2().setEnabled(false);
		} else {
			// 恢复所有
			getSub1().setEnabled(true);
			getSub2().setEnabled(true);
			getPlus1().setEnabled(true);
			getPlus2().setEnabled(true);
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
				setHour2(getHour2() + 1);
				setButtonEnabledByHour();
			}
		});
		getPlus2().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setHour1(getHour1() + 1);
				setHour2(getHour2() + 1);
				setButtonEnabledByHour();
			}
		});
		getSub1().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setHour1(getHour1() - 1);
				setHour2(getHour2() - 1);
				setButtonEnabledByHour();
			}
		});
		getSub2().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setHour1(getHour1() - 1);
				setHour2(getHour2() - 1);
				setButtonEnabledByHour();
			}
		});
	}
}
