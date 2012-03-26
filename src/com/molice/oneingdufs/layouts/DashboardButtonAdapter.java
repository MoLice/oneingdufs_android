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
 * ��������GridView��Adapter��Item�Ǵ�ͼ���TextView
 * ͼ�����ʽ��˽�з���createItem�ж���
 * ����DashboardPart��
 * @author MoLice
 */
public class DashboardButtonAdapter extends BaseAdapter {

	private Context context;
	private Object[][] resources;

	/**
	 * ͨ��Context�ʹ洢��drawable��string��Դ��ʶ�������飬��������GridView��Adapter
	 * @param context
	 * @param resources �洢��drawable��string��Դ��ʶ��������
	 */
	public DashboardButtonAdapter(Context context, Object[][] resources) {
		this.context = context;
		this.resources = resources;
	}
	
	/**
	 * ����dashboard�İ�ť����TextViewΪ����
	 * @param position ����getView()�����е�position����
	 * @param resource �洢��Դ��ʶ���Ķ�ά���飬ÿ��һά��Ա�ĵ�һ����Ա��drawable���ڶ�����Ա��string
	 * @return TextView��ť
	 */
	private TextView createItem(int position, Object[][] resources) {
		TextView text = new TextView(context);
		text.setGravity(Gravity.CENTER_HORIZONTAL);
		text.setCompoundDrawablePadding(2);
		// ����ͼ��
		text.setCompoundDrawablesWithIntrinsicBounds(null, context.getResources().getDrawable((Integer) resources[position][0]), null, null);
		text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		text.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		text.setTextColor(context.getResources().getColor(R.color.strong_text));
		// �����ı�
		text.setText(context.getResources().getString((Integer) resources[position][1]));
		// ���tag��Ϊ���
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
