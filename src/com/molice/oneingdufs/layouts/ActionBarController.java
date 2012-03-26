package com.molice.oneingdufs.layouts;

import com.molice.oneingdufs.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
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
     * @param layout 要添加的actionbar buttons layout
     * @return 使用LayoutInflater生成的View，也即参数buttons_layout对应的layout
     */
    public static View setActionBarButtons(Activity activity, int layout) {
    	LinearLayout wrapper = (LinearLayout) activity.findViewById(R.id.actionbar_buttonWrapper);
    	wrapper.removeAllViews();
    	LayoutInflater inflater = LayoutInflater.from(activity);
    	View action_buttons = inflater.inflate(layout, null);
    	wrapper.addView(action_buttons);
    	return action_buttons;
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
