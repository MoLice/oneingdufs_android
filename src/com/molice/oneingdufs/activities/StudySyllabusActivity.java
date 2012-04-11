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
 * 在校学习-我的课表<br/>
 * R.layout.study_syllabus
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-3-28
 */
public class StudySyllabusActivity extends Activity {
	private AppMenu appMenu;
	private SharedPreferencesStorager storager;
	
	// 所有课程数据
	private LinearLayout[] classes;
	// 星期几的按钮
	private TextView[] days;
	// 星期几旁边的颜色条
	private View[] lines;
	// 课程背景色
	private int[] color_class;
	// 星期几颜色
	private int color_dayBg;
	// 当前星期几颜色
	private int color_curDay;
	
	/**
	 * 课程表，从服务端返回的JSON数据，结构如下：<br/>
	 * <pre>
	 * [
	 *   // 星期一的课程
	 *   [
	 *     {
	 *       "noon": 0|1|2,// 早上|下午|晚上
	 *       "cutter": 0|1|2|3,// 前一节|后一节|3-5小节、6-8小节|7-9小节
	 *       "content": "网络编程\\n实验楼A-207",// 课程名称、上课地点
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
			syllabus = new JSONArray("[[{\"noon\":2,\"cutter\":1,\"content\":\"网络编程\\n实验楼A-207\"},{\"noon\":0,\"cutter\":1,\"content\":\"网络编程\\n实验楼A-207\"}],[],[],[{\"noon\":1,\"cutter\":3,\"content\":\"网络编程\\n实验楼A-207\"}],[],[],[]]");
		} catch (Exception e) {
			Log.e("JSON错误", "StudySyllabusActivity#syllabus, e=" + e.toString());
		}
        if(syllabus != null) {
        	// 默认显示星期一
        	setOneClassFromObject(syllabus.optJSONArray(0));
	        for(int i=0; i<7; i++) {
	        	days[i].setTag(i);
	        	days[i].setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						int index = new Integer(String.valueOf(v.getTag()));
						// 显示当前被点击的星期为红色
						for(int i=0; i<7; i++) {
							days[i].setTextColor(color_dayBg);
							lines[i].setBackgroundColor(Color.TRANSPARENT);
						}
						((TextView) v).setTextColor(color_curDay);
						lines[index].setBackgroundColor(color_curDay);
						// 根据点击的天数，显示左边的课表
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
    	Log.d("MainActivity", "onOptionsItemSelected被调用");
    	return appMenu.onOptionsItemSelected(item);
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
    // 从{@link #syllabus}得到每天的课表，并填充到视图中
    /**
     * 将一节课的数据转换为视图控件
     * @param data 当天课表的一节课
     */
    private void setOneClassFromObject(JSONArray datas) {
    	// 清空视图内今天所有课程的内容
    	for(int i=0; i<3; i++) {
    		((TextView) classes[i].getChildAt(0)).setText("");
    		((TextView) classes[i].getChildAt(0)).setBackgroundColor(Color.TRANSPARENT);
    		((TextView) classes[i].getChildAt(1)).setText("");
    		((TextView) classes[i].getChildAt(1)).setBackgroundColor(Color.TRANSPARENT);
    	}
    	// 遍历每一节课，生成代表课程的TextView并填充到相应位置
    	int length=datas.length();
    	for(int i=0; i<length; i++) {
    		// 获取每节课的数据
    		JSONObject data = datas.optJSONObject(i);
    		TextView class1 = (TextView) classes[data.optInt("noon")].getChildAt(0);
    		TextView class2 = (TextView) classes[data.optInt("noon")].getChildAt(1);
        	// 将TextView添加到对应位置
        	if(data.optInt("cutter") == 0) {
        		// 是该时段的第一节课
        		class1.setText(data.optString("content"));
        		class1.setBackgroundColor(color_class[data.optInt("noon")]);
        	} else if(data.optInt("cutter") == 1) {
        		// 该时段的第二节课
        		class2.setText(data.optString("content"));
        		class2.setBackgroundColor(color_class[data.optInt("noon")]);
        	} else if(data.optInt("cutter") == 2 && data.optInt("noon") == 0) {
        		// 早上第二节课十二点三下课，所以将第二个TextView的layout_weight设为3
        		class2.setText(data.optString("content"));
        		class2.setBackgroundColor(color_class[data.optInt("noon")]);
        		class2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, getResources().getDimensionPixelSize(R.dimen.zeroDip), 3));
        	} else if(data.optInt("cutter") == 2 && data.optInt("noon") == 1) {
        		// 下午只有一节课，两点上课，四点下课，所以将第一个TextView的layout_weight设为3，第二个设为1
        		class1.setText(data.optString("content"));
        		class1.setBackgroundColor(color_class[data.optInt("noon")]);
        		class1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, getResources().getDimensionPixelSize(R.dimen.zeroDip), 3));
        		class2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, getResources().getDimensionPixelSize(R.dimen.zeroDip), 1));
        	} else if(data.optInt("cutter") == 3 && data.optInt("noon") == 1) {
        		// 下午只有一节课，两点多上课，五点下课，所以将第一个TextView的layout_weight设为1，第二个设为3
        		class2.setText(data.optString("content"));
        		class2.setBackgroundColor(color_class[data.optInt("noon")]);
        		class1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, getResources().getDimensionPixelSize(R.dimen.zeroDip), 1));
        		class2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, getResources().getDimensionPixelSize(R.dimen.zeroDip), 3));
        	}
    	}
    }
}
