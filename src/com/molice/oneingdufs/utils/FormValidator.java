package com.molice.oneingdufs.utils;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.interfaces.IFormValidator;

/**
 * ����֤��ʵ����ʵ��{@link IFormValidator}�ӿ�
 * TODO ʹ���߳�ִ�����������������Կ��ǰ��߳����������ﻹ����ʵ������ʱ�����û��������ã�
 */
public class FormValidator implements IFormValidator {
	// ��ʾ�ñ���Activityʵ��
	private Activity activity;
	// ��ǰactivity����Դ����
	private Resources resources;
	
	// �����ݼ����洢���Ǿ���initData()�������
	private JSONArray form;
	// ����洢�б�
	private JSONObject errorlist;
	// �洢inputԭʼֵ���ַ����������жϱ��Ƿ����䶯
	private JSONObject oriInputsValue;
	// ����ʱlabel��ʾ����ɫ
	private int color_error;
	// labelĬ����ʾ����ɫ
	private int color_label;
	
	/**
	 * @param activity ��ʾ�ñ���Activityʵ�������������н���findViewById()��getResources()����
	 * @param form �����ݼ���ΪJSONArray��ÿ��arrayԪ������һ��JSONObject��ÿ��JSONObject�洢����������ؼ����������ݣ����忴ʾ��
	 * @example JSONArray form = [<br />
	 *   {<br />
	 *     "input": int, // ����ؼ���R.id<br />�����ڳ�ʼ�������б��滻Ϊ��Ӧ��Viewʵ������
	 *     "input_name": String, // ���ؼ���name<br />
	 *     "input_label": int, // ����ؼ���label��R.id<br />�����ڳ�ʼ�������б��滻Ϊ��Ӧ��Viewʵ������
	 *     "rex": String, // ��֤����ֵ��������ʽ<br />
	 *     "label_text": int, // ����ؼ���label���ı���R.string<br />
	 *     "error_message": int, // ��֤ʧ��ʱ��ʾ�Ĵ�����ʾ�ı���R.string<br />
	 *   },<br />
	 * ]
	 */
	public FormValidator(Activity activity, JSONArray form) {
		this.activity = activity;
		this.resources = this.activity.getResources();
		this.color_error = this.resources.getColor(R.color.form_error);
		this.color_label = this.resources.getColor(R.color.form_label);
		
		this.errorlist = new JSONObject();
		this.oriInputsValue = new JSONObject();
		// ��ʼ��form���ݣ��滻���е���Դ��ʶ��
		this.form = initData(form);
	}
	/**
	 * ��������input��label
	 * <ol>
	 * <li>��form�д洢����Դ��ʶ���滻Ϊ��Ӧ��ʵ������
	 * <li>����setTag()���InputData���ȥ</li>
	 * <li>��ȡinput�ĳ�ʼֵ�������жϱ��Ƿ����޸ģ��Ӷ�����submit��ť�Ŀ���״̬</li>
	 * </ol>
	 */
	private JSONArray initData(JSONArray form) {
		int length = form.length();
		for(int i=0; i<length; i++) {
			JSONObject formData = form.optJSONObject(i);
			EditText input = (EditText) activity.findViewById(formData.optInt("input"));
			TextView label = (TextView) activity.findViewById(formData.optInt("input_label"));
			String label_text = resources.getString(formData.optInt("label_text"));
			String error_message = resources.getString(formData.optInt("error_message"));
			InputData inputData = new InputData();
			inputData.index = i;
			inputData.input_name = formData.optString("input_name");
			input.setTag(inputData);
			label.setTag(inputData);
			try {
				formData.putOpt("input", input);
				formData.putOpt("input_label", label);
				formData.putOpt("label_text", label_text);
				formData.putOpt("error_message", error_message);
				oriInputsValue.putOpt(formData.optString("input_name"), input.getText().toString());
				form.put(i, formData);
			} catch (Exception e) {
				Log.d("JSON����", "initData, i=" + String.valueOf(i) + " e=" + e.toString());
			}
		}
		return form;
	}
	/**
	 * ����Ϊÿ��������ؼ����ɶ�����JSONObject���÷������ڿ�������ʵ����FormValidator�������form����
	 * @param input Ҫ����ı��ؼ���Ŀǰ��֧��EditText
	 * @param input_name ���ؼ�name
	 * @param input_label ���ؼ���label��ΪTextViewʵ��
	 * @param rex ��������ֵ��������ʽ
	 * @param label_text labelĬ����ʾ������
	 * @param error_message ��֤��ͨ��ʱ��label��ʾ�Ĵ�����������
	 */
	public static JSONObject createInputData(int input, String input_name, int input_label, String rex, int label_text, int error_message) {
		JSONObject inputData = new JSONObject();
    	try {
			inputData.putOpt("input", input);
			inputData.putOpt("input_name", input_name);
	    	inputData.putOpt("input_label", input_label);
	    	inputData.putOpt("rex", rex);
	    	inputData.putOpt("label_text", label_text);
	    	inputData.putOpt("error_message", error_message);
		} catch (Exception e) {
			Log.d("JSON����", "getInputData��e=" + e.toString());
		}
    	return inputData;
	}
	
	/**
	 * ������������������˱���֤ʧ�ܶ���������ʧ�ܵ�����£����ݷ���˷��صĴ�����Ϣ���½���<br />
	 * ��initData()���Ѿ���InputDataͨ��setTag()��ӵ�label�ڣ���˴˴�����input_name������label��findViewWithTag()��Android2.2�����ϵģ�<br />
	 * <strong>ע�⣺ʹ�ñ�������ǰ����layout�����ϸ���ģ��������EditText��TextView��Ϊͬһ��LinearLayou��ֱ����Ԫ��</strong>
	 * @param formErrors ����˷��صı�������ʾ��Ϣ
	 */
	public void updateFormMessageFromServer(JSONObject formErrors) {
		// ��ȡ��һ������ؼ����Ա��ȡ��������ؼ���ViewParent
		View firstInput = null;
		LinearLayout viewParent = null;
		int childCount = 0;
		// ����View�����в��Ҷ�Ӧ��tag
		if(form.optJSONObject(0) != null) {
			firstInput = (View) form.optJSONObject(0).opt("input");
			// ��ȡViewParent
			viewParent = (LinearLayout) firstInput.getParent();
			childCount = viewParent.getChildCount();
			for(int i=0; i<childCount; i++) {
				View child = viewParent.getChildAt(i);
				if(child instanceof Button || child instanceof EditText) {
					// ��ΪButton��EditText���̳���TextView���������ų�
				} else if(child instanceof TextView) {
					TextView label = (TextView) viewParent.getChildAt(i);
					InputData inputData = (InputData) child.getTag();
					if(formErrors.has(inputData.input_name)) {
						JSONArray errors = formErrors.optJSONArray(inputData.input_name);
						label.setText(errors.optString(0));
						label.setTextColor(color_error);
					} else {
						label.setTextColor(color_label); 
					}
				}
			}
		}
		alertFormMsgDialog(null, null);
	}
	/**
	 * ����Toast��ʾ�����ڶ�ʱ�����Զ���ʧ
	 * @param message ��ʾ���ݣ���Ϊnull����ʾ���������롱
	 */
	public void alertFormToast(String message) {
		if(message == null) message = "��������";
		Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}
	/**
	 * �����Ի�����ֻӵ��һ����ȷ������ť�����°�ť��رնԻ���
	 * @param title���Ի�����⣬��Ϊnull��Ĭ�ϡ��������
	 * @param message �Ի������ݣ���Ϊnull��Ĭ�ϡ��밴��ʾ�޸ġ�
	 */
	public void alertFormMsgDialog(String title, String message) {
		if(title == null) title = "�������";
		if(message == null) message = "�밴��ʾ�޸�";
		new AlertDialog.Builder(activity)
		.setTitle(title)
		.setMessage(message)
		.setPositiveButton("ȷ��", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		})
		.show();
	}
	
	@Override
	public void addOnFocusChangeValidate() {
		int length = form.length();
		for(int i=0; i<length; i++) {
			View input = (View) form.optJSONObject(i).opt("input");
			input.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if(!hasFocus) {
						InputData inputData = (InputData) v.getTag();
						JSONObject formData = form.optJSONObject(inputData.index);
						// ʧȥ����ʱ������֤����
						isInputCorrect(v,
							(TextView) formData.opt("input_label"),
							formData.optString("input_name"),
							formData.optString("rex"),
							formData.optString("label_text"),
							formData.optString("error_message"));
					}
				}
			});
		}
	}
	
	/**
	 * �����Ƿ��ѱ��޸�
	 * @return ���޸��򷵻�true�����򷵻�false
	 */
	public boolean isFormModified() {
		JSONObject currentInput = getInput();
		@SuppressWarnings("unchecked")
		Iterator<String> iterator = currentInput.keys();
		while(iterator.hasNext()) {
			if(!currentInput.optString(iterator.next()).equals(oriInputsValue.optString(iterator.next()))) {
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean isFormCorrect() {
		int length = form.length();
		for(int i=0; i<length; i++) {
			JSONObject formData = form.optJSONObject(i);
			isInputCorrect((View) formData.opt("input"),
					(TextView) formData.opt("input_label"),
					formData.optString("input_name"),
					formData.optString("rex"),
					formData.optString("label_text"),
					formData.optString("error_message"));
		}
		if(errorlist.length() == 0)
			return true;
		return false;
	}

	
	@Override
	public boolean isInputCorrect(View input, TextView label, String input_name, String rex, String label_text, String error_message) {
		EditText _input = (EditText) input;
		if(_input.getText().toString().matches(rex)) {
			// ��֤ͨ��
			updateFormMessage(label, input_name, label_text, false);
			return true;
		}
		// ��֤ʧ��
		updateFormMessage(label, input_name, error_message, true);
		return false;
	}

	
	@Override
	public void updateFormMessage(TextView label, String input_name, String message, boolean isError) {
		label.setText(message);
		if(isError) {
			// ��������Ϊ������ɫ
			label.setTextColor(color_error);
			// ��Ӵ����ǵ�errorlist
			try {
				errorlist.put(input_name, 1);
			} catch (Exception e) {
				Log.d("JSON����", "updateFormMessage��e=" + e.toString());
			}
		} else {
			// ����ָ�ΪĬ����ɫ
			label.setTextColor(color_label);
			// �������Ǵ�errorlist���Ƴ�
			try {
				errorlist.put(input_name, null);
			} catch (Exception e) {
				Log.d("JSON����", "updateFormMessage��e=" + e.toString());
			}
		}
	}
	
	@Override
	public JSONObject getInput() {
		int length = form.length();
		EditText input = null;
		JSONObject result = new JSONObject();
		for(int i=0; i<length; i++) {
			JSONObject inputData = form.optJSONObject(i);
			input = (EditText) inputData.opt("input");
			try {
				result.put(inputData.optString("input_name"), input.getText().toString());
			} catch (Exception e) {
				Log.d("JSON����", "getInput��e=" + e.toString());
			}
		}
		
		return result;
	}

	
	/**
	 * �ӱ��ش洢���ȡ�����ݲ���ʾ����
	 */
	public void setInputFromLocalStorage() {
		
	}
	
	/**
	 * ����ǰ����ؼ���form�е�����������ؼ���input_name�󶨵�input��label��setTag()��
	 */
	private class InputData {
		int index;
		String input_name;
	}

}
