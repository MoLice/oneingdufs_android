package com.molice.oneingdufs.activities;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONObject;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.layouts.ActionBarController;
import com.molice.oneingdufs.layouts.CalendarAdapter;
import com.molice.oneingdufs.utils.ProjectConstants;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CommonCalendarActivity extends Activity {
	private static Calendar FIRST_DAY_OF_TERM;
	private static Calendar LAST_DAY_OF_TERM;
	private int totalWeeksOfTerm;
	private Calendar firstday;
	private Calendar lastday;

	private GridView grid;
	private ArrayList<String> gridItemText;
	
	private LinearLayout tabWrapper;
	private LinearLayout[] tabs;
	private LayoutInflater inflater;
	private int tabColor;
	private int tabColorCurrent;
	
	/**
	 * 从服务端返回的数据，包括几月几日、什么信息。<strong>这里的一月为1</strong>，因为是从服务端返回的数据，所以不能因为Java的一月是0所以就用0
<pre>{
  '2': {
  	'details': '整个月份的说明',
    '26': '报到',
    '27': '正式上课',
  },
  '3': {
  	'details': '整个月份的说明',
    '8': '妇女节，下午停课',
  },
}</pre>
	 */
	private JSONObject importantDays;
	/**
	 * 来源于{@link #importantDays}，经过转换，可供{@link CalendarAdapter}使用的数据，key值为在整个GridView内的单元格index
<pre>{
  '12': '报到',
  '13': '正式上课',
}
</pre>
	 */
	private JSONObject daysMsg;
	
	private TextView details;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_calendar);

		// 设置标题
		ActionBarController.setTitle(this, R.string.common_calendar);

		FIRST_DAY_OF_TERM = getFirstDayThisTerm();
		LAST_DAY_OF_TERM = getLastDayThisTerm();
		totalWeeksOfTerm = LAST_DAY_OF_TERM.get(Calendar.WEEK_OF_YEAR) - FIRST_DAY_OF_TERM.get(Calendar.WEEK_OF_YEAR) + 1;

		grid = (GridView) findViewById(R.id.common_calendar_grid);
		tabWrapper = (LinearLayout) findViewById(R.id.common_calendar_tab);
		inflater = LayoutInflater.from(this);
		tabColor = getResources().getColor(R.color.black_light);
		tabColorCurrent = getResources().getColor(R.color.green);
		details = (TextView) findViewById(R.id.common_calendar_details);
		
		try {
			importantDays = new JSONObject("{\"2\":{\"details\":\"2月26日开学\\n2月27报到\",\"26\":\"报到\",\"27\":\"正式上课\"},\"3\":{\"details\":\"三月份没什么事\",\"8\":\"妇女节，下午停课\"},\"4\":{\"2\":\"清明节放假\",\"3\":\"清明节放假\",\"4\":\"清明节放假\"},\"7\":{\"14\":\"放假\"}}");
		} catch (Exception e) {
			Log.d("JSON异常", "CommonCalendarActivity#importantDays, e=" + e.toString());
		}
		
		grid.setOnItemClickListener(itemClickListener);
		setMonthTab(ProjectConstants.COMMON.CALENDAR_STARTMONTH, ProjectConstants.COMMON.CALENDAR_ENDMONTH, ProjectConstants.COMMON.CALENDAR_STARTMONTH);
		setMonthDetails(ProjectConstants.COMMON.CALENDAR_STARTMONTH);
		setGridViewData(ProjectConstants.COMMON.CALENDAR_STARTMONTH);
	}
	
	/**
	 * 创建月份选项卡
	 * @param from 从哪个月份开始，注意1月份为0
	 * @param to 到哪个月份结束，注意1月份为0
	 * @param current 默认要显示哪个月份
	 */
	private void setMonthTab(int from, int to, int current) {
		tabs = new LinearLayout[to - from + 1];
		int i = 0;
		for(int m=from; m<=to; m++) {
			tabs[i] = createTab(m, m == current ? true : false);
			tabWrapper.addView(tabs[i]);
			i++;
		}
	}
	
	/**
	 * 由{@link #createTab(int, boolean)}调用，用于产生每一个Tab项
	 * @param month 代表的月份，注意1月份为0
	 * @param current 该Tab是否为当前显示的月份，如果是则添加额外的样式
	 * @return 由LinearLayout包装的TabItem
	 */
	private LinearLayout createTab(int month, boolean current) {
		LinearLayout tabItem = (LinearLayout) inflater.inflate(R.layout.tab_item, null);
		TextView tabText = (TextView) tabItem.getChildAt(0);
		View line = tabItem.getChildAt(1);
		
		tabText.setText(String.valueOf(month + 1) + "月");
		if(current) {
			// 设置当前tab的额外属性
			tabText.setTextColor(tabColorCurrent);
			line.setBackgroundColor(tabColorCurrent);
		}
		
		tabItem.setTag(month);
		tabItem.setOnClickListener(tabClickListener);
		
		return tabItem;
	}
	
	private OnClickListener tabClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// 清除所有item的样式
			int length = tabs.length;
			for(int i=0; i<length; i++) {
				((TextView) tabs[i].getChildAt(0)).setTextColor(tabColor);
				tabs[i].getChildAt(1).setBackgroundColor(tabColor);
			}
			// 设置被点击item的样式
			int month = Integer.parseInt(String.valueOf(v.getTag()));
			LinearLayout tabItem = (LinearLayout) v;
			((TextView) tabItem.getChildAt(0)).setTextColor(tabColorCurrent);
			tabItem.getChildAt(1).setBackgroundColor(tabColorCurrent);
			// 改变日期内容
			setGridViewData(month);
			// 改变下面月份说明
			setMonthDetails(month);
		}
	};
	
	private OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if(view.getTag() != null) {
				Toast.makeText(CommonCalendarActivity.this, String.valueOf(view.getTag()), Toast.LENGTH_SHORT).show();
			}
		}
	};

	/**
	 * 设置日历内容，包括周数、当月天数及星期
	 * @param month 显示哪个月份的日历
	 */
	private void setGridViewData(int month) {
		// 清空数据
		grid.setAdapter(new CalendarAdapter(this, new String[] {}, null));
		daysMsg = new JSONObject();
		
		firstday = getFirstday(month);
		lastday = getLastday(month);
		
		// 本月第一行开头空几格（本月第一天星期几-1）
		int spaceItem = firstday.get(Calendar.DAY_OF_WEEK) - 1;
		// GridView的行数，包括表头在内
		int rowCount = (int) Math.ceil((lastday.get(Calendar.DAY_OF_MONTH) + spaceItem) / 7.0) + 1;
		// GridView的列数，包括第一列“周数”
		int colCount = 8;
		// 表头
		String[] gridTitle = "周日一二三四五六".split("");
		
		// 利用表头初始化数组
		gridItemText = new ArrayList<String>();
		for (int i = 1; i < gridTitle.length; i++)
			gridItemText.add(gridTitle[i]);
		
		// 添加日期
		for (int r = 1; r < rowCount; r++) {
			// 添加本行周数
			gridItemText.add(formatWeekFromIntToString(getWeek(r)));
			for (int c = 1; c < colCount; c++) {
				// 添加本行的日期
				gridItemText.add(getCurrentItemText(r, c, spaceItem));
			}
		}

		grid.setAdapter(new CalendarAdapter(this, gridItemText
				.toArray(new String[] {}), daysMsg));
	}
	
	private void setMonthDetails(int month) {
		month++;
		JSONObject m = importantDays.optJSONObject(String.valueOf(month));
		details.setText("");
		if(m != null) {
			details.setText(m.optString("details", ""));
		}
	}

	/**
	 * 获取当前日期，并通过{@link #importantDays}判断该天是否需要加上文本说明并标识为重要日期，<br/>
	 * 如果需要，则将从{@link #importantDays}提取的msg添加到{@link #daysMsg}中，key值为该天的表格在整个GridView中的index
	 * 
	 * @param r 循环的行数，第一行为1（因为0是表头）
	 * @param c 循环的列数，第一列为1（因为0是周数列）
	 * @param spaceItem 本月第一行开头空几几格
	 * @return 如果符合要求则返回文本格式的数字，否则返回空字符串
	 */
	private String getCurrentItemText(int r, int c, int spaceItem) {
		int day = c - spaceItem + 7 * (r - 1);
		if (day < 1 || day > lastday.get(Calendar.DAY_OF_MONTH))
			return "";
		
		String month = String.valueOf(lastday.get(Calendar.MONTH) + 1);
		JSONObject importantDay = importantDays.optJSONObject(month);
		if(importantDay != null
				&& importantDay.optString(String.valueOf(day), null) != null) {
			// 这一天有信息需要添加进去，则获取这一天在整个GridView中的index并作为key添加到daysMsg中
			try {
				daysMsg.putOpt(String.valueOf(gridItemText.size()), importantDay.optString(String.valueOf(day)));
			} catch (Exception e) {
				Log.d("JSON异常", "CommonCalendarActivity#getCurrentItemText, e=" + e.toString());
			}
		}
		return String.valueOf(day);
	}

	/**
	 * 获取当前行在本学期中是第几周
	 * @param r 行数，注意第一行是1，因为表头是0
	 * @return 代表本学期周数的数字，例如第一周为1，若该行时学期尚未开始，则返回0
	 */
	private int getWeek(int r) {
		int result = firstday.get(Calendar.WEEK_OF_YEAR)
				- FIRST_DAY_OF_TERM.get(Calendar.WEEK_OF_YEAR) + r;
		if(result > totalWeeksOfTerm)
			result = -1;
		return result < 1 ? 0 : result;
	}

	/**
	 * 将数字格式的周数转换为字符串格式，例如13转换为十三，21转换为二十一
	 * @param week 周数，第一周为1
	 * @return 周数的序号，字符串格式，如一、十一、二十一
	 */
	private String formatWeekFromIntToString(int week) {
		if (week <= 0)
			return "";
		return String.valueOf(week);
//		String str = "一二三四五六七八九十";
//		if (week <= 10)
//			return str.substring(week - 1, week);
//		// n十的情况
//		int quotient = (int) Math.floor(week / 10);
//		int remainder = week % 10;
//		return formatWeekFromIntToString(quotient) + "十"
//				+ formatWeekFromIntToString(remainder);
		
	}

	private Calendar getFirstDayThisTerm() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(ProjectConstants.COMMON.CALENDAR_STARTYEAR, ProjectConstants.COMMON.CALENDAR_STARTMONTH, ProjectConstants.COMMON.CALENDAR_STARTDAY);
		return calendar;
	}
	
	private Calendar getLastDayThisTerm() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(ProjectConstants.COMMON.CALENDAR_ENDYEAR, ProjectConstants.COMMON.CALENDAR_ENDMONTH, ProjectConstants.COMMON.CALENDAR_ENDDAY);
		return calendar;
	}

	private Calendar getFirstday(int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), month, 1);
		return calendar;
	}

	private Calendar getLastday(int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), month, 1);
		calendar.set(calendar.get(Calendar.YEAR), month, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return calendar;
	}

	@Override
	public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);
	}
}