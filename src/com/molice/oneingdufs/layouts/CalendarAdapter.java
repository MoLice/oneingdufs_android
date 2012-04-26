package com.molice.oneingdufs.layouts;

import java.util.Calendar;

import org.json.JSONObject;

import com.molice.oneingdufs.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CalendarAdapter extends BaseAdapter {
	private Context context;
	private int month;
	private int todayMonth;
	private int todayDay;
	private String[] daysText;
	private JSONObject daysMsg;
	
	public CalendarAdapter(Context context, int month, String[] daysText, JSONObject daysMsg) {
		this.context = context;
		this.daysText = daysText;
		this.daysMsg = daysMsg == null ? new JSONObject() : daysMsg;
		
		this.month = month;
		this.todayMonth = Calendar.getInstance().get(Calendar.MONTH);
		this.todayDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	}

	@Override
	public int getCount() {
		return daysText.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView day = getNewDay(position, daysMsg.optString(String.valueOf(position), null));
		if(day.getTag() == null) {
			day.setTag(null);
			day.setBackgroundColor(Color.TRANSPARENT);
		}
		// 判断是否是今天的日期，是则设置背景色为红色
		if(isToday(position)) {
			day.setBackgroundColor(context.getResources().getColor(R.color.red_light));
		}
		// 为周数那一列添加背景色
		if(position % 8 == 0) {
			day.setTextColor(context.getResources().getColor(R.color.black));
			day.setTypeface(Typeface.DEFAULT_BOLD);
		}
		return day;
	}
	
	private TextView getNewDay(int position, String msg) {
		TextView day = new TextView(context);
		day.setText(daysText[position]);
		day.setGravity(Gravity.CENTER);
		
		// 判断是否是带信息的日期，是则将文本msg添加为tag并设置背景色
		if(msg != null) {
			day.setTag(msg);
			day.setBackgroundColor(context.getResources().getColor(R.color.green_light));
		} else {
			day.setTag(null);
			day.setBackgroundColor(Color.TRANSPARENT);
		}
		return day;
	}
	
	/**
	 * 判断当前单元格是否是今天，是则设置背景色为红色
	 * @param position
	 * @return
	 */
	private boolean isToday(int position) {
		return todayMonth == month && position > 7 && position % 8 != 0 && String.valueOf(todayDay).equals(daysText[position]);
	}
}
