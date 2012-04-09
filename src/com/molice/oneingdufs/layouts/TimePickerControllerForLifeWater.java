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
	 * ����ʱ��ı䰴ť�Ŀ���״̬<br/>
	 * ��Ϊ���������̶�Ϊ1Сʱ����������ֻ�ж�һ���򼴿�
	 */
	private void setButtonEnabledByHour() {
		int hour1 = getHour1();
		if(hour1 == 10) {
			// �ҵ�����sub
			getSub1().setEnabled(false);
			getSub2().setEnabled(false);
			getPlus1().setEnabled(true);
			getPlus2().setEnabled(true);
		} else if(hour1 == 17) {
			// �ҵ�����plus
			getSub1().setEnabled(true);
			getSub2().setEnabled(true);
			getPlus1().setEnabled(false);
			getPlus2().setEnabled(false);
		} else {
			// �ָ�����
			getSub1().setEnabled(true);
			getSub2().setEnabled(true);
			getPlus1().setEnabled(true);
			getPlus2().setEnabled(true);
		}
	}
	/**
	 * ��Ӱ�ť�ĵ���¼�����ʱ���иı��ı����hour1��hour2��ֵ
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
