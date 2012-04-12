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
import android.widget.Toast;

/**
 * У԰����-��������<br/>
 * R.layout.life_gdufslife
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-4-13
 */
public class LifeGdufsLifeActivity extends Activity{
	private EditText email;
	private Button cancel;
	private Button submit;
	
	private SharedPreferencesStorager storager;
	private FormValidator validator;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.life_gdufslife);
		
		// ���ñ�����
		ActionBarController.setTitle(this, R.string.life_gdufslife);
		
		storager = new SharedPreferencesStorager(this);
		
		// ��ʼ��View���
		email = (EditText) findViewById(R.id.life_gdufslife_email);
		cancel = (Button) findViewById(R.id.life_gdufslife_cancel);
		submit = (Button) findViewById(R.id.life_gdufslife_submit);
		email.setText(storager.get("user_info_email", ""));
		
		// ����֤��
		JSONArray form = new JSONArray();
		form.put(FormValidator.createInputData(R.id.life_gdufslife_title, "title", R.id.life_gdufslife_title_label, "^.{1,}$", R.string.life_gdufslife_title_label, R.string.life_gdufslife_title_error));
		form.put(FormValidator.createInputData(R.id.life_gdufslife_content, "content", R.id.life_gdufslife_content_label, "^.{4,}$", R.string.life_gdufslife_content_label, R.string.life_gdufslife_content_error));
		form.put(FormValidator.createInputData(R.id.life_gdufslife_email, "email", R.id.life_gdufslife_email_label, "^([\\w\\d_\\.-]+)@([\\w\\d_-]+\\.)+\\w{2,4}$", R.string.life_gdufslife_email_label, R.string.life_gdufslife_email_error));
		form.put(FormValidator.createInputData(R.id.life_gdufslife_campus, "campus", R.id.life_gdufslife_campus_label, "^.{1,}$", R.string.life_gdufslife_campus_label, R.string.life_gdufslife_campus_error));
		form.put(FormValidator.createInputData(R.id.life_gdufslife_type, "type", R.id.life_gdufslife_type_label, "^.{1,}$", R.string.life_gdufslife_type_label, R.string.life_gdufslife_type_error));
		// ��name�����ﱻsetTag
		validator = new FormValidator(this, form);
		// ����ʧȥ����ʱ�Զ���֤
		validator.addOnFocusChangeValidate();
		
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(validator.isFormModified()) {
					if(validator.isFormCorrect()) {
						// ��֤ͨ��
						JSONObject data = validator.getInput();
						try {
							data.putOpt("name", storager.get("username", ""));
						} catch (Exception e) {
							Log.d("JSON����", "LifeGdufsLifeActivity#submit, e=" + e.toString());
						}
						Toast.makeText(LifeGdufsLifeActivity.this, "�ɹ��ύ���ԣ���ȴ��ظ�", Toast.LENGTH_SHORT).show();
						finish();
					} else {
						ProjectConstants.alertDialog(LifeGdufsLifeActivity.this, "�������", "�밴����ʾ�޸�", true);
					}
				} else {
					Toast.makeText(LifeGdufsLifeActivity.this, "���޸�", Toast.LENGTH_SHORT).show();
				}
			}
		});
		cancel.setOnClickListener(new OnClickListener() {
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