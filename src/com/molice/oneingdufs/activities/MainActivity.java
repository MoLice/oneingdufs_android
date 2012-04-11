package com.molice.oneingdufs.activities;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.androidpn.ServiceManager;
import com.molice.oneingdufs.layouts.AppMenu;
import com.molice.oneingdufs.layouts.DashboardPart;
import com.molice.oneingdufs.layouts.ActionBarController;
import com.molice.oneingdufs.utils.ProjectConstants;
import com.molice.oneingdufs.utils.SharedPreferencesStorager;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;

/**
 * 应用主页<br />
 * 启动AndroidPN服务{@link #startNotificationService()}<br/>
 * R.layout.main
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-2-14
 */
public class MainActivity extends Activity {
	
	private LinearLayout main_wrapper;
	
	private SharedPreferencesStorager storager;
	private AppMenu appMenu;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // 设置标题为第几周
        ActionBarController.setTitle(this, "12周");
        
        // 获取R.layout.main中的主容器LinearLayout，用于addDashboardPart引用
        main_wrapper =(LinearLayout) findViewById(R.id.main_wrapper);
        
        storager = new SharedPreferencesStorager(this);
        appMenu = new AppMenu(this);
        // 检测应用及系统基本信息是否存储正确
        // TODO 待应用增加了首次安装后的教学界面后，将此段代码迁移到该界面，实现每次安装只运行一次的目的
        ProjectConstants.setBaseInfo(this);
        // 如果不存在本机号码phoneNumber则重新获取并存储
        ProjectConstants.setPhone(this, storager);
        
        // 添加校园生活
        addDashboardPart(R.string.dashboard_life, new Object[][] {
        		// 订水
        		{null, R.string.dashboard_water, LifeWaterActivity.class},
        		// 报修
        		{null, R.string.dashboard_fix, LifeFixActivity.class},
        		// 校园卡
        		{null, R.string.dashboard_card, LifeCardActivity.class},
        		// 后勤留言
        		{null, R.string.dashboard_gdufslife, null}
        });
        
        // 添加在校学习
        addDashboardPart(R.string.dashboard_study, new Object[][] {
        		// 我的课表
        		{null, R.string.dashboard_syllabus, StudySyllabusActivity.class},
        		// 图书馆
        		{null, R.string.dashboard_library, null},
        		// 我的班级
        		{null, R.string.dashboard_class, null},
        });
        
        // 添加公共消息
        addDashboardPart(R.string.dashboard_common, new Object[][] {
        		// 校历
        		{null, R.string.dashboard_calendar, null},
        		// 常用电话
        		{null, R.string.dashboard_telephones, null},
        		// 活动讲座
        		{null, R.string.dashboard_activity, null},
        		// 失物招领
        		{null, R.string.dashboard_lost, CommonLostActivity.class},
        });
        
        // 启动AndroidPN服务
        startNotificationService();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	// 判断是否已登录，根据不同状态改变ActionBar右边的操作按钮
        if(!storager.get("isLogin", false)) {
            // 将ActionBar右边的操作按钮设置为登录按钮
            View wrapper = ActionBarController.setActionBarButtons(this, new int[] {R.layout.actionbar_buttons_login});
            ImageButton actionbar_login = (ImageButton) wrapper.findViewById(R.id.actionbar_login);
            Log.d("看按钮是否存在", "MainActivity#onResume, ImageButton=" + actionbar_login.getMeasuredHeight());
            actionbar_login.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    			}
    		});
        } else {
        	ActionBarController.setActionBarButtons(this, new int[] {});
        }
    }
    
    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	appMenu.onCreateOptionsMenu(menu);
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	if(storager.get("isLogin", false)) {
    		// 显示登录组，隐藏未登录组
    		menu.setGroupVisible(AppMenu.NOTLOGIN, false);
    		menu.setGroupVisible(AppMenu.ISLOGIN, true);
    	} else {
    		// 显示未登录组，隐藏登录组
    		menu.setGroupVisible(AppMenu.NOTLOGIN, true);
    		menu.setGroupVisible(AppMenu.ISLOGIN, false);
    	}
    	return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Log.d("MainActivity", "onOptionsItemSelected被调用");
    	return appMenu.onOptionsItemSelected(item);
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
     * 启动AndroidPN服务
     */
    private void startNotificationService() {
    	ServiceManager serviceManager = new ServiceManager(this);
    	serviceManager.setNotificationIcon(R.drawable.ic_launcher);
    	serviceManager.startService();
    }
}