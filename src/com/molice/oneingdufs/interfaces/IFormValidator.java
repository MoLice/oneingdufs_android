package com.molice.oneingdufs.interfaces;

import org.json.JSONObject;

import android.view.View;
import android.widget.TextView;

/**
 * ����֤�ӿڣ�������������ؼ���֤����������֤������������ؼ����������<br />
 * isFormCorrect()��isInputCorrect()��updateFormMessage()��getInput()��addOnFocusChangeValidate()
 */
public interface IFormValidator {
	/**
	 * ��֤�������Ƿ���ȷ��ͨ��������������ؼ���������֤
	 * @return ͨ�����errorlist�Ƿ�Ϊ�����ж���֤�������Ϊ������֤ͨ��������true���ǿ��򷵻�false
	 */
	public boolean isFormCorrect();
	/**
	 * �жϵ�һ���ؼ��������Ƿ���ȷ�������жϽ�����벻ͬ��������updateFormMessage()
	 * @param input Ҫ����ı��ؼ���Ŀǰ��֧��EditText
	 * @param label ���ؼ���label��ΪTextViewʵ��
	 * @param input_name ���ؼ�name
	 * @param rex ��������ֵ��������ʽ
	 * @param label_text labelĬ����ʾ������
	 * @param error_message ��֤��ͨ��ʱ��label��ʾ�Ĵ�����������
	 * @return ��֤�������֤ͨ��Ϊtrue����֤ʧ��Ϊfalse
	 */
	public boolean isInputCorrect(View input, TextView label, String input_name, String rex, String label_text, String error_message);
	/**
	 * ����isError�ı䴫���label��ֵ�Դﵽ����������Ϣ��Ŀ�ģ�ͬʱ����errorlist����洢�б�
	 * @param label ����label��TextViewʵ��
	 * @param input_name ��name
	 * @param message label�����֣�����Ϊ������������
	 * @param isError ����ģʽ��־λ������Ϊtrue����д���������Ϊfalse��ָ�label��Ĭ��״̬
	 */
	public void updateFormMessage(TextView label, String input_name, String message, boolean isError);
	/**
	 * ���ر����ݣ���name-value�洢���˷����õ���������δ����֤��
	 * @return ��name-value�洢�ı�����
	 */
	public JSONObject getInput();
	/**
	 * Ϊ�ñ���ÿ������ؼ�����ʧȥ����ʱ�Զ���֤
	 */
	public void addOnFocusChangeValidate();
}
