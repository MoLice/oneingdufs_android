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
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
        
        // ��ȡR.layout.main�еĸ�����LinearLayout������addDashboardPart����
        main_wrapper =(LinearLayout) findViewById(R.id.main_wrapper);
        
        storager = new SharedPreferencesStorager(this);
        appMenu = new AppMenu(this);
        
        // ���Ӧ�ü�ϵͳ������Ϣ�Ƿ�洢��ȷ
        // TODO ��Ӧ���������״ΰ�װ��Ľ�ѧ����󣬽��˶δ���Ǩ�Ƶ��ý��棬ʵ��ÿ�ΰ�װֻ����һ�ε�Ŀ��
        try {
        	StringBuilder baseInfo = new StringBuilder(storager.get("baseInfo", ""));
        	// ���������baseInfo�����ߴ��ڵ�Ӧ�ð汾�͵�ǰӦ�ð汾��һ�£�����������Ӧ�ð汾�ˣ��������´洢
            if(baseInfo.length() == 0 || !baseInfo.substring(baseInfo.indexOf("app_version=OneInGDUFS/") + 23, baseInfo.indexOf("app_version=OneInGDUFS/") + 27).equals(getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS).versionName)) {
            	baseInfo.append("app_version=OneInGDUFS/").append(getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS).versionName)
            		.append(";system_version=Android/").append(Build.VERSION.RELEASE)
            		.append(";sdk=").append(Build.VERSION.SDK)
            		.append(";display=width:").append(getWindowManager().getDefaultDisplay().getWidth()).append(",height:").append(getWindowManager().getDefaultDisplay().getHeight()).append(";");
            	storager.set("baseInfo", baseInfo.toString()).save();
            }
            // ��������ڱ�������phoneNumber�����»�ȡ���洢
            if(!storager.isExist("phoneNumber")) {
            	storager.set("phoneNumber", ProjectConstants.getPhoneNumber(this));
            }
		} catch (Exception e) {
			Log.d("BaseInfo�洢����", e.toString());
		}
        
        // ���У԰����
        addDashboardPart(R.string.dashboard_life, new Object[][] {
        		// ��ˮ
        		{null, R.string.dashboard_water, LifeWaterActivity.class},
        		// ����
        		{null, R.string.dashboard_fix, LifeFixActivity.class},
        		// У԰��
        		{null, R.string.dashboard_card, LifeCardActivity.class},
        		// ʧ������
        		{null, R.string.dashboard_lost, null},
        		// ��������
        		{null, R.string.dashboard_gdufslife, null}
        });
        
        // ��Ӹ�������
        addDashboardPart(R.string.dashboard_user, new Object[][] {
        		// �ҵİ༶
        		{null, R.string.dashboard_class, null},
        		// ��Ϣ����
        		{null, R.string.dashboard_message, null},
        		// �ҵ��ճ�
        		{null, R.string.dashboard_todo, null},
        		// ��������
        		{null, R.string.dashboard_info, UserInfoActivity.class},
        });
        
        // ����AndroidPN����
        //startNotificationService();
    }
    
    @Override
    public void onNewIntent(Intent intent) {
    	super.onNewIntent(intent);
    	Log.d("MainActivity#onNewIntent", "��������");
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	// �ж��Ƿ��ѵ�¼�����ݲ�ͬ״̬�ı�ActionBar�ұߵĲ�����ť
        if(!storager.get("isLogin", false)) {
            // ��ActionBar�ұߵĲ�����ť����Ϊ��¼��ť
            View actionbar_buttons = ActionBarController.setActionBarButtons(this, R.layout.actionbar_buttons_login);
            Button actionbar_login = (Button) actionbar_buttons.findViewById(R.id.actionbar_login);
            actionbar_login.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				Intent i = new Intent(getApplicationContext(), LoginActivity.class);
    				startActivity(i);
    			}
    		});
        } else {
        	ActionBarController.setActionBarButtons(this, R.layout.actionbar_buttons_main);
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
    	serviceManager.setNotificationIcon(R.drawable.ic_launcher);
    	serviceManager.startService();
    }
}