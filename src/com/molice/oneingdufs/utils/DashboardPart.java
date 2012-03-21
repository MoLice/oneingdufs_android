package com.molice.oneingdufs.utils;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.activities.LoginActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * dashboard�ֲ��࣬����һ��������ķָ��ߣ��Լ���GridView���ֵ�����Ӧ���ܰ�ť
 * ���ɵ�ʵ��������Activity����Ҫ���෽��addLayout()��ӵ�Activity
 * �����ڲ����õ�DashboardButtonAdapter�࣬����ΪGridView���Adapter
 * layout��R.layout.dashboard_part
 * @author MoLice
 */
public class DashboardPart {
	// ������
	private Context context;
	// SharedPreferencesStorager
	private SharedPreferencesStorager storager;
	// ��Դ����
	private Object[][] resources;
	// ������
	private LayoutInflater inflater;
	
	public DashboardPart(Context context, Object[][] resources) {
		this.context = context;
		this.storager = new SharedPreferencesStorager(context);
		this.resources = initResourceArray(resources);
		this.inflater = LayoutInflater.from(this.context);
	}

	/**
	 * ���������������ݵ�dashboard��ӵ���ǰActivity��
	 * @param wrapper ��ǰActivity�ĸ���������һ��LinearLayout���
	 * @param part_title ��ǰdashboard part�ı����string��Դ��ʶ��������У԰�����
	 * @return
	 */
	public void addLayout(LinearLayout wrapper, int part_title) {
		// ������������
		View layout = inflater.inflate(R.layout.dashboard_part, null);
		// �����������
		TextView title = (TextView) layout.findViewById(R.id.part_title);
		title.setText(context.getResources().getString(part_title));
		
		// ����GridView
		GridView content = (GridView) layout.findViewById(R.id.part_content);
		content.setAdapter(new DashboardButtonAdapter(context, resources));
		
		// ������������ݵ�layout��ӵ���ǰActivity�Ĳ���������
		wrapper.addView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		
		// ��Ӱ�ťClick�¼�����������resources��ÿ����ť��Ӧ��Activity
		content.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent;
				if(storager.get("isLogin", false)) {
					// ����ѵ�¼������ת���ð�ť��Ӧ��Activity
					intent = new Intent(context.getApplicationContext(), (Class<?>)resources[arg2][2]);
				} else {
					// ���δ��¼������ת��LoginActivity
					intent = new Intent(context.getApplicationContext(), LoginActivity.class);
					intent.putExtra("onLoginSuccessActivity", (Class<?>)resources[arg2][2]);
				}
				context.startActivity(intent);
			}
		});
	}
	/**
	 * ��ʼ���������Դ��ʶ�����飬�����е�null�滻Ϊ��Ӧ��Ĭ��ֵ
	 * @param resources �洢��Դ��ʶ�������飬����ͼ��drawable������string��Ҫ������Activity��class
	 * @return ������ʼ�������Դ��ʶ������
	 */
	private Object[][] initResourceArray(Object[][] resources) {
		int length = resources.length;
		// ��null�滻ΪĬ��ֵ
		for(int i=0; i<length; i++) {
			if(resources[i][0] == null) {
				resources[i][0] = R.drawable.ic_launcher;
			}
			if(resources[i][1] == null) {
				resources[i][0] = R.string.dashboard_default;
			}
			if(resources[i][2] == null) {
				resources[i][2] = LoginActivity.class;
			}
		}
		return resources;
	}
}
