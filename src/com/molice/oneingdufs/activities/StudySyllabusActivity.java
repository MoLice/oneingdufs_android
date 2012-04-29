package com.molice.oneingdufs.activities;

import org.json.JSONArray;
import org.json.JSONObject;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.utils.HttpConnectionHandler;
import com.molice.oneingdufs.utils.HttpConnectionUtils;
import com.molice.oneingdufs.utils.ProjectConstants;
import com.molice.oneingdufs.utils.SharedPreferencesStorager;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 在校学习-我的课表<br/>
 * R.layout.study_syllabus
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-3-28
 */
public class StudySyllabusActivity extends Activity {
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
        
        // 检查是否需要发送更新课表的请求
        checkLocalSyllabus();
	}
    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }
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
    		// 获取上下两个课表容器的引用
    		TextView class1 = (TextView) classes[data.optInt("noon")].getChildAt(0);
    		TextView class2 = (TextView) classes[data.optInt("noon")].getChildAt(1);
    		// 回复容器的面积
    		class1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, getResources().getDimensionPixelSize(R.dimen.zeroDip), 2));
    		class2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, getResources().getDimensionPixelSize(R.dimen.zeroDip), 2));
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
    
    /**
     * 检查本地存储里是否有课表数据，若有则提取并显示，若没有则发起网络连接，从服务端下载
     * @return 如果本地存储已经有，则返回true，否则返回false
     */
    private boolean checkLocalSyllabus() {
    	if(storager.get("study_syllabus", "").equals("")) {
    		// 本地不存在课表数据，需要发送请求到服务端
    		new HttpConnectionUtils(connectionHandler, this).get(ProjectConstants.URL.study_syllabus, null);
    		return false;
    	} else {
    		// 本地存在课表数据，直接提取并显示
    		try {
    			syllabus = new JSONArray(storager.get("study_syllabus", "[]"));
    			displaySyllabus();
    		} catch (Exception e) {
    			Log.e("JSON错误", "StudySyllabusActivity#checkLocalSyllabus, e=" + e.toString());
    		}
    		return false;
    	}
    }
    
    private void displaySyllabus() {
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
    
    private HttpConnectionHandler connectionHandler= new HttpConnectionHandler(this) {
    	@Override
    	protected void onSucceed(JSONObject result) {
    		super.onSucceed(result);
    		syllabus = result.optJSONArray("syllabus");
    		storager.set("study_syllabus", syllabus.toString()).save();
    		displaySyllabus();
    		Toast.makeText(StudySyllabusActivity.this, "成功获取课表", Toast.LENGTH_SHORT).show();
    	}
    };
}
