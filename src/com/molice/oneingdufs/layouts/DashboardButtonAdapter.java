package com.molice.oneingdufs.layouts;

import com.molice.oneingdufs.R;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 创建用于GridView的Adapter，Item是带图标的TextView
 * 图标的样式在私有方法createItem中定义
 * 用于DashboardPart类
 * @author MoLice
 */
public class DashboardButtonAdapter extends BaseAdapter {

	private Context context;
	private Object[][] resources;

	/**
	 * 通过Context和存储有drawable、string资源标识符的数组，创建用于GridView的Adapter
	 * @param context
	 * @param resources 存储有drawable、string资源标识符的数组
	 */
	public DashboardButtonAdapter(Context context, Object[][] resources) {
		this.context = context;
		this.resources = resources;
	}
	
	/**
	 * 生成dashboard的按钮，以TextView为载体
	 * @param position 传入getView()方法中的position参数
	 * @param resource 存储资源标识符的二维数组，每个一维成员的第一个成员是drawable，第二个成员是string
	 * @return TextView按钮
	 */
	private TextView createItem(int position, Object[][] resources) {
		TextView text = new TextView(context);
		text.setGravity(Gravity.CENTER_HORIZONTAL);
		text.setCompoundDrawablePadding(2);
		// 设置图标
		text.setCompoundDrawablesWithIntrinsicBounds(null, context.getResources().getDrawable((Integer) resources[position][0]), null, null);
		text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		text.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		text.setTextColor(context.getResources().getColor(R.color.strong_text));
		// 设置文本
		text.setText(context.getResources().getString((Integer) resources[position][1]));
		// 添加tag作为标记
		//text.setTag(position);
		return text;
	}

	@Override
	public int getCount() {
		return resources.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView text = createItem(position, resources);
		return text;
	}
}
