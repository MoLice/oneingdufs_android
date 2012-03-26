package com.molice.oneingdufs.layouts;

import com.molice.oneingdufs.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * ����ActionBar����
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-3-25
 */
public class ActionBarController {
	public ActionBarController() {
	}
	
	/**
     * ��actionbar buttons������ӵ���ǰ�����actionbar��
     * @param activity ��ǰ��ͼ
     * @param layout Ҫ��ӵ�actionbar buttons layout
     * @return ʹ��LayoutInflater���ɵ�View��Ҳ������buttons_layout��Ӧ��layout
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
     * ����ActionBar���еı���
     * @param activity ��ǰ��ͼ
     * @param title �����ı�
     */
    public static void setTitle(Activity activity, String title) {
    	TextView actionbar_title = (TextView) activity.findViewById(R.id.actionbar_currentActivity);
    	actionbar_title.setText(title);
    }
    
    /**
     * ����ActionBar���еı���
     * @param activity ��ǰ��ͼ
     * @param title �����ı���Դ��ʶ��
     */
    public static void setTitle(Activity activity, int title) {
    	TextView actionbar_title = (TextView) activity.findViewById(R.id.actionbar_currentActivity);
    	actionbar_title.setText(title);
    }
}
