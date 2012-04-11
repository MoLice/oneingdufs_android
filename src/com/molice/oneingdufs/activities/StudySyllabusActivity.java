package com.molice.oneingdufs.activities;

import org.json.JSONArray;
import org.json.JSONObject;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.layouts.AppMenu;
import com.molice.oneingdufs.utils.SharedPreferencesStorager;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * ��Уѧϰ-�ҵĿα�<br/>
 * R.layout.study_syllabus
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-3-28
 */
public class StudySyllabusActivity extends Activity {
	private AppMenu appMenu;
	private SharedPreferencesStorager storager;
	
	// ���пγ�����
	private LinearLayout[] classes;
	// ���ڼ��İ�ť
	private TextView[] days;
	// ���ڼ��Աߵ���ɫ��
	private View[] lines;
	// �γ̱���ɫ
	private int[] color_class;
	// ���ڼ���ɫ
	private int color_dayBg;
	// ��ǰ���ڼ���ɫ
	private int color_curDay;
	
	/**
	 * �γ̱��ӷ���˷��ص�JSON���ݣ��ṹ���£�<br/>
	 * <pre>
	 * [
	 *   // ����һ�Ŀγ�
	 *   [
	 *     {
	 *       "noon": 0|1|2,// ����|����|����
	 *       "cutter": 0|1|2|3,// ǰһ��|��һ��|3-5С�ڡ�6-8С��|7-9С��
	 *       "content": "������\\nʵ��¥A-207",// �γ����ơ��Ͽεص�
	 *     },
	 *   ],
	 * ]
	 * </pre>
	 */
	private JSONArray syllabus;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_syllabus);
        
        appMenu = new AppMenu(this);
        storager = new SharedPreferencesStorager(this);
        
        classes = new LinearLayout[] {
        	(LinearLayout) findViewById(R.id.study_syllabus_classes1),
        	(LinearLayout) findViewById(R.id.study_syllabus_classes2),
        	(LinearLayout) findViewById(R.id.study_syllabus_classes3),
        };
        days = new TextView[] {
        	(TextView) findViewById(R.id.study_syllabus_day1),
        	(TextView) findViewById(R.id.study_syllabus_day2),
        	(TextView) findViewById(R.id.study_syllabus_day3),
        	(TextView) findViewById(R.id.study_syllabus_day4),
        	(TextView) findViewById(R.id.study_syllabus_day5),
        	(TextView) findViewById(R.id.study_syllabus_day6),
        	(TextView) findViewById(R.id.study_syllabus_day7),
        };
        lines = new View[] {
        	findViewById(R.id.study_syllabus_line1),
        	findViewById(R.id.study_syllabus_line2),
        	findViewById(R.id.study_syllabus_line3),
        	findViewById(R.id.study_syllabus_line4),
        	findViewById(R.id.study_syllabus_line5),
        	findViewById(R.id.study_syllabus_line6),
        	findViewById(R.id.study_syllabus_line7),
        };
        
        color_class = new int[] {
        	getResources().getColor(R.color.pink_light),
        	getResources().getColor(R.color.green_light),
        	getResources().getColor(R.color.blue_light),
        };
        color_dayBg = getResources().getColor(R.color.black_light);
        color_curDay = getResources().getColor(R.color.red_light);
        
        try {
			syllabus = new JSONArray("[[{\"noon\":2,\"cutter\":1,\"content\":\"������\\nʵ��¥A-207\"},{\"noon\":0,\"cutter\":1,\"content\":\"������\\nʵ��¥A-207\"}],[],[],[{\"noon\":1,\"cutter\":3,\"content\":\"������\\nʵ��¥A-207\"}],[],[],[]]");
		} catch (Exception e) {
			Log.e("JSON����", "StudySyllabusActivity#syllabus, e=" + e.toString());
		}
        if(syllabus != null) {
        	// Ĭ����ʾ����һ
        	setOneClassFromObject(syllabus.optJSONArray(0));
	        for(int i=0; i<7; i++) {
	        	days[i].setTag(i);
	        	days[i].setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						int index = new Integer(String.valueOf(v.getTag()));
						// ��ʾ��ǰ�����������Ϊ��ɫ
						for(int i=0; i<7; i++) {
							days[i].setTextColor(color_dayBg);
							lines[i].setBackgroundColor(Color.TRANSPARENT);
						}
						((TextView) v).setTextColor(color_curDay);
						lines[index].setBackgroundColor(color_curDay);
						// ���ݵ������������ʾ��ߵĿα�
						setOneClassFromObject(syllabus.optJSONArray(index));
					}
				});
	        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
    	Log.d("MainActivity", "onOptionsItemSelected������");
    	return appMenu.onOptionsItemSelected(item);
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
    // ��{@link #syllabus}�õ�ÿ��Ŀα�����䵽��ͼ��
    /**
     * ��һ�ڿε�����ת��Ϊ��ͼ�ؼ�
     * @param data ����α��һ�ڿ�
     */
    private void setOneClassFromObject(JSONArray datas) {
    	// �����ͼ�ڽ������пγ̵�����
    	for(int i=0; i<3; i++) {
    		((TextView) classes[i].getChildAt(0)).setText("");
    		((TextView) classes[i].getChildAt(0)).setBackgroundColor(Color.TRANSPARENT);
    		((TextView) classes[i].getChildAt(1)).setText("");
    		((TextView) classes[i].getChildAt(1)).setBackgroundColor(Color.TRANSPARENT);
    	}
    	// ����ÿһ�ڿΣ����ɴ���γ̵�TextView����䵽��Ӧλ��
    	int length=datas.length();
    	for(int i=0; i<length; i++) {
    		// ��ȡÿ�ڿε�����
    		JSONObject data = datas.optJSONObject(i);
    		TextView class1 = (TextView) classes[data.optInt("noon")].getChildAt(0);
    		TextView class2 = (TextView) classes[data.optInt("noon")].getChildAt(1);
        	// ��TextView��ӵ���Ӧλ��
        	if(data.optInt("cutter") == 0) {
        		// �Ǹ�ʱ�εĵ�һ�ڿ�
        		class1.setText(data.optString("content"));
        		class1.setBackgroundColor(color_class[data.optInt("noon")]);
        	} else if(data.optInt("cutter") == 1) {
        		// ��ʱ�εĵڶ��ڿ�
        		class2.setText(data.optString("content"));
        		class2.setBackgroundColor(color_class[data.optInt("noon")]);
        	} else if(data.optInt("cutter") == 2 && data.optInt("noon") == 0) {
        		// ���ϵڶ��ڿ�ʮ�������¿Σ����Խ��ڶ���TextView��layout_weight��Ϊ3
        		class2.setText(data.optString("content"));
        		class2.setBackgroundColor(color_class[data.optInt("noon")]);
        		class2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, getResources().getDimensionPixelSize(R.dimen.zeroDip), 3));
        	} else if(data.optInt("cutter") == 2 && data.optInt("noon") == 1) {
        		// ����ֻ��һ�ڿΣ������ϿΣ��ĵ��¿Σ����Խ���һ��TextView��layout_weight��Ϊ3���ڶ�����Ϊ1
        		class1.setText(data.optString("content"));
        		class1.setBackgroundColor(color_class[data.optInt("noon")]);
        		class1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, getResources().getDimensionPixelSize(R.dimen.zeroDip), 3));
        		class2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, getResources().getDimensionPixelSize(R.dimen.zeroDip), 1));
        	} else if(data.optInt("cutter") == 3 && data.optInt("noon") == 1) {
        		// ����ֻ��һ�ڿΣ�������ϿΣ�����¿Σ����Խ���һ��TextView��layout_weight��Ϊ1���ڶ�����Ϊ3
        		class2.setText(data.optString("content"));
        		class2.setBackgroundColor(color_class[data.optInt("noon")]);
        		class1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, getResources().getDimensionPixelSize(R.dimen.zeroDip), 1));
        		class2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, getResources().getDimensionPixelSize(R.dimen.zeroDip), 3));
        	}
    	}
    }
}
