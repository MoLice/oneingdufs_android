package com.molice.oneingdufs.interfaces;

import org.json.JSONObject;

import android.view.View;
import android.widget.TextView;

/**
 * 表单验证接口，包含单个输入控件验证、整个表单验证、错误处理、输入控件焦点监听等<br />
 * isFormCorrect()、isInputCorrect()、updateFormMessage()、getInput()、addOnFocusChangeValidate()
 */
public interface IFormValidator {
	/**
	 * 验证整个表单是否正确，通过遍历所有输入控件并进行验证
	 * @return 通过检查errorlist是否为空来判断验证结果，若为空则验证通过并返回true，非空则返回false
	 */
	public boolean isFormCorrect();
	/**
	 * 判断单一表单控件的输入是否正确，根据判断结果传入不同参数调用updateFormMessage()
	 * @param input 要检验的表单控件，目前仅支持EditText
	 * @param label 表单控件的label，为TextView实例
	 * @param input_name 表单控件name
	 * @param rex 检验输入值的正则表达式
	 * @param label_text label默认显示的文字
	 * @param error_message 验证不通过时由label显示的错误提醒文字
	 * @return 验证结果，验证通过为true，验证失败为false
	 */
	public boolean isInputCorrect(View input, TextView label, String input_name, String rex, String label_text, String error_message);
	/**
	 * 根据isError改变传入的label的值以达到更新提醒信息的目的，同时更新errorlist错误存储列表
	 * @param label 表单的label，TextView实例
	 * @param input_name 表单name
	 * @param message label的文字，可能为错误提醒文字
	 * @param isError 处理模式标志位，假如为true则进行错误处理，假如为false则恢复label的默认状态
	 */
	public void updateFormMessage(TextView label, String input_name, String message, boolean isError);
	/**
	 * 返回表单数据，以name-value存储，此方法得到的数据是未经验证的
	 * @return 以name-value存储的表单数据
	 */
	public JSONObject getInput();
	/**
	 * 为该表单的每个输入控件设置失去焦点时自动验证
	 */
	public void addOnFocusChangeValidate();
}
