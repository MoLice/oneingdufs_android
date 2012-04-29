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
 * Ӧ����ҳ<br />
 * ����AndroidPN����{@link #startNotificationService()}<br/>
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
        
        // ���ñ���Ϊ�ڼ���
        ActionBarController.setTitle(this, CommonCalendarActivity.getCurrentWeekThisTerm());
        
        // ��ȡR.layout.main�е�������LinearLayout������addDashboardPart����
        main_wrapper =(LinearLayout) findViewById(R.id.main_wrapper);
        
        storager = new SharedPreferencesStorager(this);
        appMenu = new AppMenu(this);
        // ���Ӧ�ü�ϵͳ������Ϣ�Ƿ�洢��ȷ
        // TODO ��Ӧ���������״ΰ�װ��Ľ�ѧ����󣬽��˶δ���Ǩ�Ƶ��ý��棬ʵ��ÿ�ΰ�װֻ����һ�ε�Ŀ��
        ProjectConstants.setBaseInfo(this);
        // ��������ڱ�������phoneNumber�����»�ȡ���洢
        ProjectConstants.setPhone(this, storager);
        
        // ���У԰����
        addDashboardPart(R.string.dashboard_life, new Object[][] {
        		// ��ˮ
        		{R.drawable.ic_water, R.string.dashboard_water, LifeWaterActivity.class},
        		// ����
        		{R.drawable.ic_fix, R.string.dashboard_fix, LifeFixActivity.class},
        		// У԰��
        		{R.drawable.ic_card, R.string.dashboard_card, LifeCardActivity.class},
        		// ��������
        		{R.drawable.ic_gdufslife, R.string.dashboard_gdufslife, LifeGdufsLifeActivity.class}
        });
        
        // �����Уѧϰ
        addDashboardPart(R.string.dashboard_study, new Object[][] {
        		// �ҵĿα�
        		{R.drawable.ic_syllabus, R.string.dashboard_syllabus, StudySyllabusActivity.class},
        		// ͼ���
        		{R.drawable.ic_library, R.string.dashboard_library, null},
        		// �ҵİ༶
        		{R.drawable.ic_class, R.string.dashboard_class, null},
        });
        
        // ��ӹ�����Ϣ
        addDashboardPart(R.string.dashboard_common, new Object[][] {
        		// У��
        		{R.drawable.ic_calendar, R.string.dashboard_calendar, CommonCalendarActivity.class},
        		// ���õ绰
        		{R.drawable.ic_telephones, R.string.dashboard_telephones, null},
        		// �����
        		{R.drawable.ic_activity, R.string.dashboard_activity, null},
        		// ʧ������
        		{R.drawable.ic_lost, R.string.dashboard_lost, CommonLostActivity.class},
        });
        
        // ����AndroidPN����
        if(SettingsActivity.getNotificationEnabled(this))
        	startNotificationService();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	// �ж��Ƿ��ѵ�¼�����ݲ�ͬ״̬�ı�ActionBar�ұߵĲ�����ť
        if(!storager.get("isLogin", false)) {
            // ��ActionBar�ұߵĲ�����ť����Ϊ��¼��ť
            View wrapper = ActionBarController.setActionBarButtons(this, new int[] {R.layout.actionbar_buttons_login});
            ImageButton actionbar_login = (ImageButton) wrapper.findViewById(R.id.actionbar_login);
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
    		// ��ʾ��¼�飬����δ��¼��
    		menu.setGroupVisible(AppMenu.NOTLOGIN, false);
    		menu.setGroupVisible(AppMenu.ISLOGIN, true);
    	} else {
    		// ��ʾδ��¼�飬���ص�¼��
    		menu.setGroupVisible(AppMenu.NOTLOGIN, true);
    		menu.setGroupVisible(AppMenu.ISLOGIN, false);
    	}
    	return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Log.d("MainActivity", "onOptionsItemSelected������");
    	return appMenu.onOptionsItemSelected(item);
    }
    
    /**
     * ��dashboard part��ӵ������У���ť���¼���������{@link DashboardPart}�ж���
     * @param part_title ��dashboard part�ı��⣬����ΪR.string��Դ��ʶ��
     * @param part_resources �洢��dashboard part�������Դ�Ķ�ά����
     */
    private void addDashboardPart(int part_title, Object[][] part_resources) {
    	DashboardPart dashboard_part = new DashboardPart(this, part_resources);
    	dashboard_part.addLayout(main_wrapper, part_title);
    }
    
    /**
     * ����AndroidPN����
     */
    private void startNotificationService() {
    	ServiceManager serviceManager = new ServiceManager(this);
    	serviceManager.setNotificationIcon(R.drawable.icon);
    	serviceManager.startService();
    }
}