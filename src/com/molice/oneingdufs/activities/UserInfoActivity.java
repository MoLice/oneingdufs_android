package com.molice.oneingdufs.activities;

import org.json.JSONArray;
import org.json.JSONObject;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.layouts.ActionBarController;
import com.molice.oneingdufs.utils.FormValidator;
import com.molice.oneingdufs.utils.ProjectConstants;
import com.molice.oneingdufs.utils.SharedPreferencesStorager;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * �û�����-������Ϣ��ͼ<br />
 * R.layout.user_info
 */
public class UserInfoActivity extends Activity {
	private TextView info_username;
	private TextView info_studentId;
	// TODO ���ֻ�������Ϊ�Զ����EditText����EditText�ڲ����һ����ť�����°�ťʱ��ȡ�������벢���
	private EditText info_phone;
	private Button info_cancel;
	private Button info_submit;
	
	private SharedPreferencesStorager storager;
	private FormValidator validator;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info);
        
		// ���ñ�����
		ActionBarController.setTitle(this, R.string.user_info_title);
		
		// ��ʼ��View��Ա
		info_username = (TextView) findViewById(R.id.user_username);
		info_studentId = (TextView) findViewById(R.id.user_studentId);
		info_phone = (EditText) findViewById(R.id.user_info_phone);
		info_cancel = (Button) findViewById(R.id.user_info_cancel);
		info_submit = (Button) findViewById(R.id.user_info_submit);
		
		storager = new SharedPreferencesStorager(this);
		
		// �����û�����ѧ��
		info_username.setText(storager.get("username", ""));
		info_studentId.setText(storager.get("studentId", ""));
		
		// �Զ�����ֻ�����
		info_phone.setText(storager.get("user_info_phone", ""));
		
		// ����֤��
		JSONArray form = new JSONArray();
		form.put(FormValidator.createInputData(R.id.user_info_email, "email", R.id.user_info_email_label, "^([\\w\\d_\\.-]+)@([\\w\\d_-]+\\.)+\\w{2,4}$|^.{0}$", R.string.user_info_email_label, R.string.user_info_email_error));
		form.put(FormValidator.createInputData(R.id.user_info_truename, "truename", R.id.user_info_truename_label, "^[\u00b7\u4e00-\u9fa5]*$|^.{0}$", R.string.user_info_truename_label, R.string.user_info_truename_error));
		form.put(FormValidator.createInputData(R.id.user_info_phone, "phone", R.id.user_info_phone_label, "^\\d{11}$|^.{0}$", R.string.user_info_phone_label, R.string.user_info_phone_error));
		form.put(FormValidator.createInputData(R.id.user_info_cornet, "cornet", R.id.user_info_cornet_label, "^\\d{3,6}$|^.{0}$", R.string.user_info_cornet_label, R.string.user_info_cornet_error));
		form.put(FormValidator.createInputData(R.id.user_info_qq, "qq", R.id.user_info_qq_label, "^\\d{0,10}$|^.{0}$", R.string.user_info_qq_label, R.string.user_info_qq_error));
		// ��name�����ﱻsetTag
		validator = new FormValidator(this, form);
		// ����ʧȥ����ʱ�Զ���֤
		validator.addOnFocusChangeValidate();
		// �ӱ��ش洢�лָ�����
		validator.setInputFromLocalStorage("user_info_");
		// ��Ϊ�ָ������ˣ�����Ҫ���¸���oriInputsValue
		validator.updateOriInputsValue();

		// ��ʾ�û�����ѧ��
		if(storager.has("username")) {
			info_username.setText(storager.get("username", "��ǰ�û�"));
		}
		if(storager.has("studentId")) {
			info_studentId.setText(storager.get("studentId", ""));
		}
		
		info_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(validator.isFormModified()) {
					if(validator.isFormCorrect()) {
						// ��֤ͨ�����������󣬱��浽����
						JSONObject input = validator.getInput();
						// ��������ֵ
						validator.updateOriInputsValue();
						// �������ݵ�����
						validator.setInputToLocalStorager("user_info_");
						Toast.makeText(UserInfoActivity.this, "������Ϣ�ѱ���", Toast.LENGTH_SHORT).show();
						Log.d("UserInfo��֤ͨ��", input.toString());
					} else {
						ProjectConstants.alertDialog(UserInfoActivity.this, "�������", "�밴����ʾ�޸�", true);
					}
				} else {
					Toast.makeText(UserInfoActivity.this, "���޸�", Toast.LENGTH_SHORT).show();
				}
			}
		});
		info_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				validator.checkBackIfFormModified();
			}
		});
	}
	
    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if(keyCode == KeyEvent.KEYCODE_BACK) {
    		validator.checkBackIfFormModified();
    	}
    	return super.onKeyDown(keyCode, event);
    }
}
