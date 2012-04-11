package com.molice.oneingdufs.layouts;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.utils.ProjectConstants;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * 更改ActionBar的类
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-3-25
 */
public class ActionBarController {
	public ActionBarController() {
	}
	
	/**
     * 将actionbar buttons布局添加到当前界面的actionbar中
     * @param activity 当前视图
     * @param layouts 要添加的actionbar buttons layout，每个layout只包含一个按钮，layouts则为包含所有要添加的按钮的资源ID的数组。若需要为空，则传入一个空数组
     * @return {@link R.id.actionbar_buttonWrapper}容器
     * TODO 为按钮添加focus状态切换时背景色的改变
     */
    public static View setActionBarButtons(Activity activity, int[] layouts) {
    	LinearLayout wrapper = (LinearLayout) activity.findViewById(R.id.actionbar_buttonWrapper);
    	wrapper.removeAllViews();
    	LayoutInflater inflater = LayoutInflater.from(activity);
    	int layouts_count = layouts.length;
    	for(int i=0; i<layouts_count; i++) {
    		// 先添加分割线
    		View split_line = inflater.inflate(R.layout.actionbar_splitline, null);
    		LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, ProjectConstants.getPxFromDip(activity, 38));
    		wrapper.addView(split_line, lp1);
    		// 添加按钮
    		View action_button = inflater.inflate(layouts[i], null);
    		LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ProjectConstants.getPxFromDip(activity, 40), LayoutParams.FILL_PARENT);
    		wrapper.addView(action_button, lp2);
    	}
    	return wrapper;
    }
    
    /**
     * 设置ActionBar居中的标题
     * @param activity 当前视图
     * @param title 标题文本
     */
    public static void setTitle(Activity activity, String title) {
    	TextView actionbar_title = (TextView) activity.findViewById(R.id.actionbar_currentActivity);
    	actionbar_title.setText(title);
    }
    
    /**
     * 设置ActionBar居中的标题
     * @param activity 当前视图
     * @param title 标题文本资源标识符
     */
    public static void setTitle(Activity activity, int title) {
    	TextView actionbar_title = (TextView) activity.findViewById(R.id.actionbar_currentActivity);
    	actionbar_title.setText(title);
    }
}
