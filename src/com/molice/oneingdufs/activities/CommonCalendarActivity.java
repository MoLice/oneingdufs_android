package com.molice.oneingdufs.activities;

import java.util.Calendar;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.layouts.ActionBarController;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

public class CommonCalendarActivity extends Activity {
	private int month;
	private int today;
	private int todayOfWeek;
	private int lastday;
	private int lastdayOfWeek;
	
	private LinearLayout weekColWrapper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_calendar);
		
		// ���ñ���
		ActionBarController.setTitle(this, R.string.common_calendar);
		
		month = Calendar.getInstance().get(Calendar.MONTH);
		today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		todayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		lastday = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.getInstance().get(Calendar.YEAR), month, lastday);
		lastdayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		
		Log.d("�������", "month=" + month + ", today=" + today);
		
		weekColWrapper = (LinearLayout) findViewById(R.id.common_calendar_weekcolwrapper);
	}
	
	private void setCalendar() {
		// ���õ�һ�����ڼ�ȷ����һ�д��ĸ���ʼ
		weekColWrapper.getChildAt(todayOfWeek);
		
	}
	@Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }
}