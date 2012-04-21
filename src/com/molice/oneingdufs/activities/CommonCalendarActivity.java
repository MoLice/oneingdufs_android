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
	 * �ӷ���˷��ص����ݣ��������¼��ա�ʲô��Ϣ��<strong>�����һ��Ϊ1</strong>����Ϊ�Ǵӷ���˷��ص����ݣ����Բ�����ΪJava��һ����0���Ծ���0
<pre>{
  '2': {
  	'details': '�����·ݵ�˵��',
    '26': '����',
    '27': '��ʽ�Ͽ�',
  },
  '3': {
  	'details': '�����·ݵ�˵��',
    '8': '��Ů�ڣ�����ͣ��',
  },
}</pre>
	 */
	private JSONObject importantDays;
	/**
	 * ��Դ��{@link #importantDays}������ת�����ɹ�{@link CalendarAdapter}ʹ�õ����ݣ�keyֵΪ������GridView�ڵĵ�Ԫ��index
<pre>{
  '12': '����',
  '13': '��ʽ�Ͽ�',
}
</pre>
	 */
	private JSONObject daysMsg;
	
	private TextView details;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_calendar);

		// ���ñ���
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
			importantDays = new JSONObject("{\"2\":{\"details\":\"2��26�տ�ѧ\\n2��27����\",\"26\":\"����\",\"27\":\"��ʽ�Ͽ�\"},\"3\":{\"details\":\"���·�ûʲô��\",\"8\":\"��Ů�ڣ�����ͣ��\"},\"4\":{\"2\":\"�����ڷż�\",\"3\":\"�����ڷż�\",\"4\":\"�����ڷż�\"},\"7\":{\"14\":\"�ż�\"}}");
		} catch (Exception e) {
			Log.d("JSON�쳣", "CommonCalendarActivity#importantDays, e=" + e.toString());
		}
		
		grid.setOnItemClickListener(itemClickListener);
		setMonthTab(ProjectConstants.COMMON.CALENDAR_STARTMONTH, ProjectConstants.COMMON.CALENDAR_ENDMONTH, ProjectConstants.COMMON.CALENDAR_STARTMONTH);
		setMonthDetails(ProjectConstants.COMMON.CALENDAR_STARTMONTH);
		setGridViewData(ProjectConstants.COMMON.CALENDAR_STARTMONTH);
	}
	
	/**
	 * �����·�ѡ�
	 * @param from ���ĸ��·ݿ�ʼ��ע��1�·�Ϊ0
	 * @param to ���ĸ��·ݽ�����ע��1�·�Ϊ0
	 * @param current Ĭ��Ҫ��ʾ�ĸ��·�
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
	 * ��{@link #createTab(int, boolean)}���ã����ڲ���ÿһ��Tab��
	 * @param month ������·ݣ�ע��1�·�Ϊ0
	 * @param current ��Tab�Ƿ�Ϊ��ǰ��ʾ���·ݣ����������Ӷ������ʽ
	 * @return ��LinearLayout��װ��TabItem
	 */
	private LinearLayout createTab(int month, boolean current) {
		LinearLayout tabItem = (LinearLayout) inflater.inflate(R.layout.tab_item, null);
		TextView tabText = (TextView) tabItem.getChildAt(0);
		View line = tabItem.getChildAt(1);
		
		tabText.setText(String.valueOf(month + 1) + "��");
		if(current) {
			// ���õ�ǰtab�Ķ�������
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
			// �������item����ʽ
			int length = tabs.length;
			for(int i=0; i<length; i++) {
				((TextView) tabs[i].getChildAt(0)).setTextColor(tabColor);
				tabs[i].getChildAt(1).setBackgroundColor(tabColor);
			}
			// ���ñ����item����ʽ
			int month = Integer.parseInt(String.valueOf(v.getTag()));
			LinearLayout tabItem = (LinearLayout) v;
			((TextView) tabItem.getChildAt(0)).setTextColor(tabColorCurrent);
			tabItem.getChildAt(1).setBackgroundColor(tabColorCurrent);
			// �ı���������
			setGridViewData(month);
			// �ı������·�˵��
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
	 * �����������ݣ�������������������������
	 * @param month ��ʾ�ĸ��·ݵ�����
	 */
	private void setGridViewData(int month) {
		// �������
		grid.setAdapter(new CalendarAdapter(this, new String[] {}, null));
		daysMsg = new JSONObject();
		
		firstday = getFirstday(month);
		lastday = getLastday(month);
		
		// ���µ�һ�п�ͷ�ռ��񣨱��µ�һ�����ڼ�-1��
		int spaceItem = firstday.get(Calendar.DAY_OF_WEEK) - 1;
		// GridView��������������ͷ����
		int rowCount = (int) Math.ceil((lastday.get(Calendar.DAY_OF_MONTH) + spaceItem) / 7.0) + 1;
		// GridView��������������һ�С�������
		int colCount = 8;
		// ��ͷ
		String[] gridTitle = "����һ����������".split("");
		
		// ���ñ�ͷ��ʼ������
		gridItemText = new ArrayList<String>();
		for (int i = 1; i < gridTitle.length; i++)
			gridItemText.add(gridTitle[i]);
		
		// �������
		for (int r = 1; r < rowCount; r++) {
			// ��ӱ�������
			gridItemText.add(formatWeekFromIntToString(getWeek(r)));
			for (int c = 1; c < colCount; c++) {
				// ��ӱ��е�����
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
	 * ��ȡ��ǰ���ڣ���ͨ��{@link #importantDays}�жϸ����Ƿ���Ҫ�����ı�˵������ʶΪ��Ҫ���ڣ�<br/>
	 * �����Ҫ���򽫴�{@link #importantDays}��ȡ��msg��ӵ�{@link #daysMsg}�У�keyֵΪ����ı��������GridView�е�index
	 * 
	 * @param r ѭ������������һ��Ϊ1����Ϊ0�Ǳ�ͷ��
	 * @param c ѭ������������һ��Ϊ1����Ϊ0�������У�
	 * @param spaceItem ���µ�һ�п�ͷ�ռ�����
	 * @return �������Ҫ���򷵻��ı���ʽ�����֣����򷵻ؿ��ַ���
	 */
	private String getCurrentItemText(int r, int c, int spaceItem) {
		int day = c - spaceItem + 7 * (r - 1);
		if (day < 1 || day > lastday.get(Calendar.DAY_OF_MONTH))
			return "";
		
		String month = String.valueOf(lastday.get(Calendar.MONTH) + 1);
		JSONObject importantDay = importantDays.optJSONObject(month);
		if(importantDay != null
				&& importantDay.optString(String.valueOf(day), null) != null) {
			// ��һ������Ϣ��Ҫ��ӽ�ȥ�����ȡ��һ��������GridView�е�index����Ϊkey��ӵ�daysMsg��
			try {
				daysMsg.putOpt(String.valueOf(gridItemText.size()), importantDay.optString(String.valueOf(day)));
			} catch (Exception e) {
				Log.d("JSON�쳣", "CommonCalendarActivity#getCurrentItemText, e=" + e.toString());
			}
		}
		return String.valueOf(day);
	}

	/**
	 * ��ȡ��ǰ���ڱ�ѧ�����ǵڼ���
	 * @param r ������ע���һ����1����Ϊ��ͷ��0
	 * @return ����ѧ�����������֣������һ��Ϊ1��������ʱѧ����δ��ʼ���򷵻�0
	 */
	private int getWeek(int r) {
		int result = firstday.get(Calendar.WEEK_OF_YEAR)
				- FIRST_DAY_OF_TERM.get(Calendar.WEEK_OF_YEAR) + r;
		if(result > totalWeeksOfTerm)
			result = -1;
		return result < 1 ? 0 : result;
	}

	/**
	 * �����ָ�ʽ������ת��Ϊ�ַ�����ʽ������13ת��Ϊʮ����21ת��Ϊ��ʮһ
	 * @param week ��������һ��Ϊ1
	 * @return ��������ţ��ַ�����ʽ����һ��ʮһ����ʮһ
	 */
	private String formatWeekFromIntToString(int week) {
		if (week <= 0)
			return "";
		return String.valueOf(week);
//		String str = "һ�����������߰˾�ʮ";
//		if (week <= 10)
//			return str.substring(week - 1, week);
//		// nʮ�����
//		int quotient = (int) Math.floor(week / 10);
//		int remainder = week % 10;
//		return formatWeekFromIntToString(quotient) + "ʮ"
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