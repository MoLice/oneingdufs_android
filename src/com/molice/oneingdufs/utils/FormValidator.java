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
 * 表单验证器实例，实现{@link IFormValidator}接口
 * TODO 使用线程执行所有网络请求（试试看是把线程声明在这里还是在实例化的时候由用户自行设置）
 */
public class FormValidator implements IFormValidator {
	// 显示该表单的Activity实例
	private Activity activity;
	// 当前activity的资源索引
	private Resources resources;
	
	// 表单数据集，存储的是经过initData()处理过的
	private JSONArray form;
	// 错误存储列表
	private JSONObject errorlist;
	// 存储input原始值的字符集，用于判断表单是否发生变动
	private JSONObject oriInputsValue;
	// 错误时label显示的颜色
	private int color_error;
	// label默认显示的颜色
	private int color_label;
	
	/**
	 * @param activity 显示该表单的Activity实例，用于在类中进行findViewById()、getResources()操作
	 * @param form 表单数据集，为JSONArray，每个array元素又是一个JSONObject，每个JSONObject存储单个表单输入控件的所有数据，具体看示例
	 * @example JSONArray form = [<br />
	 *   {<br />
	 *     "input": int, // 输入控件的R.id<br />，会在初始化过程中被替换为对应的View实体引用
	 *     "input_name": String, // 表单控件的name<br />
	 *     "input_label": int, // 输入控件的label的R.id<br />，会在初始化过程中被替换为对应的View实体引用
	 *     "rex": String, // 验证输入值的正则表达式<br />
	 *     "label_text": int, // 输入控件的label的文本的R.string<br />
	 *     "error_message": int, // 验证失败时显示的错误提示文本的R.string<br />
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
		// 初始化form数据，替换其中的资源标识符
		this.form = initData(form);
	}
	/**
	 * 遍历所有input和label
	 * <ol>
	 * <li>将form中存储的资源标识符替换为对应的实体引用
	 * <li>调用setTag()添加InputData类进去</li>
	 * <li>获取input的初始值，用于判断表单是否发生修改，从而更改submit按钮的可用状态</li>
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
				Log.d("JSON错误", "initData, i=" + String.valueOf(i) + " e=" + e.toString());
			}
		}
		return form;
	}
	/**
	 * 方便为每个表单输入控件生成独立的JSONObject，该方法用于快速生成实例化FormValidator类所需的form参数
	 * @param input 要检验的表单控件，目前仅支持EditText
	 * @param input_name 表单控件name
	 * @param input_label 表单控件的label，为TextView实例
	 * @param rex 检验输入值的正则表达式
	 * @param label_text label默认显示的文字
	 * @param error_message 验证不通过时由label显示的错误提醒文字
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
			Log.d("JSON错误", "getInputData，e=" + e.toString());
		}
    	return inputData;
	}
	
	/**
	 * 用于请求结束且因服务端表单验证失败而导致请求失败的情况下，根据服务端返回的错误信息更新界面<br />
	 * 在initData()内已经将InputData通过setTag()添加到label内，因此此处根据input_name来查找label（findViewWithTag()是Android2.2及以上的）<br />
	 * <strong>注意：使用本方法的前提是layout必须严格按照模板来做，EditText、TextView均为同一个LinearLayou的直接子元素</strong>
	 * @param formErrors 服务端返回的表单错误提示信息
	 */
	public void updateFormMessageFromServer(JSONObject formErrors) {
		// 获取第一个输入控件，以便获取所有输入控件的ViewParent
		View firstInput = null;
		LinearLayout viewParent = null;
		int childCount = 0;
		// 遍历View，从中查找对应的tag
		if(form.optJSONObject(0) != null) {
			firstInput = (View) form.optJSONObject(0).opt("input");
			// 获取ViewParent
			viewParent = (LinearLayout) firstInput.getParent();
			childCount = viewParent.getChildCount();
			for(int i=0; i<childCount; i++) {
				View child = viewParent.getChildAt(i);
				if(child instanceof Button || child instanceof EditText) {
					// 因为Button、EditText均继承自TextView，所以先排除
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
	 * 弹出Toast提示，并在短时间内自动消失
	 * @param message 提示内容，若为null则显示“请检查输入”
	 */
	public void alertFormToast(String message) {
		if(message == null) message = "请检查输入";
		Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}
	/**
	 * 弹出对话框，且只拥有一个“确定”按钮，按下按钮则关闭对话框
	 * @param title　对话框标题，若为null则默认“输入错误”
	 * @param message 对话框内容，若为null则默认“请按提示修改”
	 */
	public void alertFormMsgDialog(String title, String message) {
		if(title == null) title = "输入错误";
		if(message == null) message = "请按提示修改";
		new AlertDialog.Builder(activity)
		.setTitle(title)
		.setMessage(message)
		.setPositiveButton("确定", new OnClickListener() {
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
						// 失去焦点时调用验证方法
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
	 * 检查表单是否已被修改
	 * @return 被修改则返回true，否则返回false
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
			// 验证通过
			updateFormMessage(label, input_name, label_text, false);
			return true;
		}
		// 验证失败
		updateFormMessage(label, input_name, error_message, true);
		return false;
	}

	
	@Override
	public void updateFormMessage(TextView label, String input_name, String message, boolean isError) {
		label.setText(message);
		if(isError) {
			// 将其设置为错误颜色
			label.setTextColor(color_error);
			// 添加错误标记到errorlist
			try {
				errorlist.put(input_name, 1);
			} catch (Exception e) {
				Log.d("JSON错误", "updateFormMessage，e=" + e.toString());
			}
		} else {
			// 将其恢复为默认颜色
			label.setTextColor(color_label);
			// 将错误标记从errorlist中移除
			try {
				errorlist.put(input_name, null);
			} catch (Exception e) {
				Log.d("JSON错误", "updateFormMessage，e=" + e.toString());
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
				Log.d("JSON错误", "getInput，e=" + e.toString());
			}
		}
		
		return result;
	}

	
	/**
	 * 从本地存储里读取表单数据并显示出来
	 */
	public void setInputFromLocalStorage() {
		
	}
	
	/**
	 * 将当前输入控件在form中的索引、输入控件的input_name绑定到input和label的setTag()中
	 */
	private class InputData {
		int index;
		String input_name;
	}

}
