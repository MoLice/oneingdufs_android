package com.molice.oneingdufs.activities;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.utils.DashboardPart;
import com.molice.oneingdufs.utils.SharedPreferencesStorager;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

/**
 * 应用主页<br />
 * R.layout.main
 */
public class MainActivity extends Activity {
	
	private LinearLayout main_wrapper;
	private RelativeLayout actionbar;
	
	private SharedPreferencesStorager storager;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // 获取R.layout.main中的父容器LinearLayout，用于addDashboardPart引用
        main_wrapper =(LinearLayout) findViewById(R.id.main_wrapper);
        // 获取R.layout.actionbar_layout中的父容器RelativeLayout，用于setActionBarButtons引用
        actionbar = (RelativeLayout) findViewById(R.id.actionbar);
        
        storager = new SharedPreferencesStorager(this);
        
        // 检测应用及系统基本信息是否存储正确
        // TODO 待应用增加了首次安装后的教学界面后，将此段代码迁移到该界面，实现每次安装只运行一次的目的
        try {
        	StringBuilder baseInfo = new StringBuilder(storager.get("baseInfo", ""));
        	// 如果不存在baseInfo，或者存在但应用版本和当前应用版本不一致（可能是升级应用版本了），则重新存储
            if(baseInfo.length() == 0 || !baseInfo.substring(baseInfo.indexOf("app_version=OneInGDUFS/") + 23, baseInfo.indexOf("app_version=OneInGDUFS/") + 27).equals(getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS).versionName)) {
            	baseInfo.append("app_version=OneInGDUFS/").append(getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS).versionName)
            		.append(";system_version=Android/").append(Build.VERSION.RELEASE)
            		.append(";sdk=").append(Build.VERSION.SDK)
            		.append(";display=width:").append(getWindowManager().getDefaultDisplay().getWidth()).append(",height:").append(getWindowManager().getDefaultDisplay().getHeight()).append(";");
            	storager.set("baseInfo", baseInfo.toString()).save();
            }
		} catch (Exception e) {
			Log.d("BaseInfo存储错误", e.toString());
		}
        
        // 添加校园生活
        addDashboardPart(R.string.dashboard_life, new Object[][] {
        		// 订水
        		{null, R.string.dashboard_water, HomeActivity.class},
        		// 报修
        		{null, R.string.dashboard_fix, LifeActivity.class},
        		// 校园卡
        		{null, R.string.dashboard_card, null},
        		// 失物招领
        		{null, R.string.dashboard_lost, null},
        		// 后勤留言
        		{null, R.string.dashboard_gdufslife, null}
        });
        
        // 添加个人中心
        addDashboardPart(R.string.dashboard_user, new Object[][] {
        		// 我的班级
        		{null, R.string.dashboard_class, RegisterActivity.class},
        		// 消息中心
        		{null, R.string.dashboard_message, null},
        		// 我的日程
        		{null, R.string.dashboard_todo, null},
        		// 个人中心
        		{null, R.string.dashboard_info, UserInfoActivity.class},
        });
    }
    
    @Override
    public void onNewIntent(Intent intent) {
    	super.onNewIntent(intent);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	// 判断是否已登录，根据不同状态改变ActionBar右边的操作按钮
        if(!storager.get("isLogin", false)) {
            // 将ActionBar右边的操作按钮设置为登录按钮
            View actionbar_buttons = setActionBarButtons(R.layout.actionbar_buttons_login);
            Button actionbar_login = (Button) actionbar_buttons.findViewById(R.id.actionbar_login);
            actionbar_login.setOnClickListener(new OnClickListener() {
    			
    			@Override
    			public void onClick(View v) {
    				Intent i = new Intent(getApplicationContext(), LoginActivity.class);
    				startActivity(i);
    			}
    		});
        }
    }
    
    /**
     * 将dashboard part添加到界面中，按钮的事件监听是在{@link DashboardPart}中定义
     * @param part_title 该dashboard part的标题，必须为R.string资源标识符
     * @param part_resources 存储该dashboard part所需的资源的二维数组
     */
    private void addDashboardPart(int part_title, Object[][] part_resources) {
    	DashboardPart dashboard_part = new DashboardPart(this, part_resources);
    	dashboard_part.addLayout(main_wrapper, part_title);
    }
    
    /**
     * 将actionbar buttons布局添加到当前界面的actionbar中
     * @param buttons_layout 要添加的actionbar buttons layout
     * @return 使用LayoutInflater生成的View，也即参数buttons_layout对应的layout
     */
    private View setActionBarButtons(int buttons_layout) {
    	LayoutInflater inflater = LayoutInflater.from(this);
    	View action_buttons = inflater.inflate(buttons_layout, null);
    	LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
    	params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    	params.addRule(RelativeLayout.CENTER_VERTICAL);
    	actionbar.addView(action_buttons, params);
    	return action_buttons;
    }
}