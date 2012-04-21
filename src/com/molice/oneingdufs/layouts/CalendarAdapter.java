package com.molice.oneingdufs.layouts;

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
	private String[] daysText;
	private JSONObject daysMsg;
	
	public CalendarAdapter(Context context, String[] daysText, JSONObject daysMsg) {
		this.context = context;
		this.daysText = daysText;
		this.daysMsg = daysMsg == null ? new JSONObject() : daysMsg;
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
		// 为周数那一列添加背景色
		if(position % 8 == 0) {
			day.setTextColor(context.getResources().getColor(R.color.black));
			day.setTypeface(Typeface.DEFAULT_BOLD);
		}
		return day;
	}
	
	private TextView getNewDay(int position, String msg) {
		TextView day = new TextView(context);
		day.setGravity(Gravity.CENTER);
		if(msg != null) {
			day.setTag(msg);
			day.setBackgroundColor(context.getResources().getColor(R.color.green));
		} else {
			day.setTag(null);
			day.setBackgroundColor(Color.TRANSPARENT);
		}
		day.setText(daysText[position]);
		return day;
	}
	
}
