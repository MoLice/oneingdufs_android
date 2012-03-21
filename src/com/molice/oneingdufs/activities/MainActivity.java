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
 * Ӧ����ҳ<br />
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
        
        // ��ȡR.layout.main�еĸ�����LinearLayout������addDashboardPart����
        main_wrapper =(LinearLayout) findViewById(R.id.main_wrapper);
        // ��ȡR.layout.actionbar_layout�еĸ�����RelativeLayout������setActionBarButtons����
        actionbar = (RelativeLayout) findViewById(R.id.actionbar);
        
        storager = new SharedPreferencesStorager(this);
        
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
		} catch (Exception e) {
			Log.d("BaseInfo�洢����", e.toString());
		}
        
        // ���У԰����
        addDashboardPart(R.string.dashboard_life, new Object[][] {
        		// ��ˮ
        		{null, R.string.dashboard_water, HomeActivity.class},
        		// ����
        		{null, R.string.dashboard_fix, LifeActivity.class},
        		// У԰��
        		{null, R.string.dashboard_card, null},
        		// ʧ������
        		{null, R.string.dashboard_lost, null},
        		// ��������
        		{null, R.string.dashboard_gdufslife, null}
        });
        
        // ��Ӹ�������
        addDashboardPart(R.string.dashboard_user, new Object[][] {
        		// �ҵİ༶
        		{null, R.string.dashboard_class, RegisterActivity.class},
        		// ��Ϣ����
        		{null, R.string.dashboard_message, null},
        		// �ҵ��ճ�
        		{null, R.string.dashboard_todo, null},
        		// ��������
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
    	// �ж��Ƿ��ѵ�¼�����ݲ�ͬ״̬�ı�ActionBar�ұߵĲ�����ť
        if(!storager.get("isLogin", false)) {
            // ��ActionBar�ұߵĲ�����ť����Ϊ��¼��ť
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
     * ��dashboard part��ӵ������У���ť���¼���������{@link DashboardPart}�ж���
     * @param part_title ��dashboard part�ı��⣬����ΪR.string��Դ��ʶ��
     * @param part_resources �洢��dashboard part�������Դ�Ķ�ά����
     */
    private void addDashboardPart(int part_title, Object[][] part_resources) {
    	DashboardPart dashboard_part = new DashboardPart(this, part_resources);
    	dashboard_part.addLayout(main_wrapper, part_title);
    }
    
    /**
     * ��actionbar buttons������ӵ���ǰ�����actionbar��
     * @param buttons_layout Ҫ��ӵ�actionbar buttons layout
     * @return ʹ��LayoutInflater���ɵ�View��Ҳ������buttons_layout��Ӧ��layout
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