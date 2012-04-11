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
     * @param layouts Ҫ��ӵ�actionbar buttons layout��ÿ��layoutֻ����һ����ť��layouts��Ϊ��������Ҫ��ӵİ�ť����ԴID�����顣����ҪΪ�գ�����һ��������
     * @return {@link R.id.actionbar_buttonWrapper}����
     * TODO Ϊ��ť���focus״̬�л�ʱ����ɫ�ĸı�
     */
    public static View setActionBarButtons(Activity activity, int[] layouts) {
    	LinearLayout wrapper = (LinearLayout) activity.findViewById(R.id.actionbar_buttonWrapper);
    	wrapper.removeAllViews();
    	LayoutInflater inflater = LayoutInflater.from(activity);
    	int layouts_count = layouts.length;
    	for(int i=0; i<layouts_count; i++) {
    		// ����ӷָ���
    		View split_line = inflater.inflate(R.layout.actionbar_splitline, null);
    		LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, ProjectConstants.getPxFromDip(activity, 38));
    		wrapper.addView(split_line, lp1);
    		// ��Ӱ�ť
    		View action_button = inflater.inflate(layouts[i], null);
    		LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ProjectConstants.getPxFromDip(activity, 40), LayoutParams.FILL_PARENT);
    		wrapper.addView(action_button, lp2);
    	}
    	return wrapper;
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
