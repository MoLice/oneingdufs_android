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
 * dashboard局部类，包含一条带标题的分割线，以及用GridView布局的自适应功能按钮
 * 生成的实例被用于Activity，需要用类方法addLayout()添加到Activity
 * 本类内部会用到DashboardButtonAdapter类，该类为GridView添加Adapter
 * layout：R.layout.dashboard_part
 * @author MoLice
 */
public class DashboardPart {
	// 上下文
	private Context context;
	// SharedPreferencesStorager
	private SharedPreferencesStorager storager;
	// 资源数组
	private Object[][] resources;
	// 扩充器
	private LayoutInflater inflater;
	
	public DashboardPart(Context context, Object[][] resources) {
		this.context = context;
		this.storager = new SharedPreferencesStorager(context);
		this.resources = initResourceArray(resources);
		this.inflater = LayoutInflater.from(this.context);
	}

	/**
	 * 将创建并填充好内容的dashboard添加到当前Activity中
	 * @param wrapper 当前Activity的父容器，是一个LinearLayout组件
	 * @param part_title 当前dashboard part的标题的string资源标识符，例如校园生活等
	 * @return
	 */
	public void addLayout(LinearLayout wrapper, int part_title) {
		// 声明布局容器
		View layout = inflater.inflate(R.layout.dashboard_part, null);
		// 设置区块标题
		TextView title = (TextView) layout.findViewById(R.id.part_title);
		title.setText(context.getResources().getString(part_title));
		
		// 设置GridView
		GridView content = (GridView) layout.findViewById(R.id.part_content);
		content.setAdapter(new DashboardButtonAdapter(context, resources));
		
		// 将已添加了内容的layout添加到当前Activity的布局容器中
		wrapper.addView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		
		// 添加按钮Click事件监听，启动resources内每个按钮对应的Activity
		content.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent;
				if(storager.get("isLogin", false)) {
					// 如果已登录，则跳转到该按钮对应的Activity
					intent = new Intent(context.getApplicationContext(), (Class<?>)resources[arg2][2]);
				} else {
					// 如果未登录，则跳转到LoginActivity
					intent = new Intent(context.getApplicationContext(), LoginActivity.class);
					intent.putExtra("onLoginSuccessActivity", (Class<?>)resources[arg2][2]);
				}
				context.startActivity(intent);
			}
		});
	}
	/**
	 * 初始化传入的资源标识符数组，将其中的null替换为对应的默认值
	 * @param resources 存储资源标识符的数组，包含图标drawable、文字string、要启动的Activity的class
	 * @return 经过初始化后的资源标识符数组
	 */
	private Object[][] initResourceArray(Object[][] resources) {
		int length = resources.length;
		// 将null替换为默认值
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
