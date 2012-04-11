package com.molice.oneingdufs.activities;

import org.json.JSONArray;
import org.json.JSONObject;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.androidpn.Constants;
import com.molice.oneingdufs.layouts.ActionBarController;
import com.molice.oneingdufs.layouts.AppMenu;
import com.molice.oneingdufs.utils.FormValidator;
import com.molice.oneingdufs.utils.HttpConnectionHandler;
import com.molice.oneingdufs.utils.HttpConnectionUtils;
import com.molice.oneingdufs.utils.ProjectConstants;
import com.molice.oneingdufs.utils.SharedPreferencesStorager;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * ע��Activity<br />
 * TODO �ж��Ƿ��ѵ�¼�����ѵ�¼����û�����ѡ��Ҫôע����¼Ȼ��ע�ᣬҪô�˳�ע�Ტ����
 */
public class RegisterActivity extends Activity {
	private Button register_back;
	private Button register_submit;
	
	private JSONArray form;
	private FormValidator validator;
	private SharedPreferencesStorager storager;
	private AppMenu appMenu;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		// �����ǰ�ѵ�¼��������LoginActivity
		storager = new SharedPreferencesStorager(this);
		if(storager.get("isLogin", false)) {
			Toast.makeText(this, "�ѵ�¼", Toast.LENGTH_SHORT).show();
			finish();
		}
		
        setContentView(R.layout.register);
        
        // ���ñ�����
        ActionBarController.setTitle(this, R.string.register_title);
		
		register_back = (Button) findViewById(R.id.register_back);
		register_submit = (Button) findViewById(R.id.register_submit);
		
		appMenu = new AppMenu(this);
		
		form = new JSONArray();
		// ���username
		form.put(FormValidator.createInputData(R.id.register_username, "username", R.id.register_username_label, "^(\\w|\\d){4,20}$", R.string.register_username_label, R.string.register_username_error));
		// ���password
		form.put(FormValidator.createInputData(R.id.register_password, "password", R.id.register_password_label, "^.{6,}$", R.string.register_password_label, R.string.register_password_error));
		// ���studentId
		form.put(FormValidator.createInputData(R.id.register_studentId, "studentId", R.id.register_studentId_label, "^20\\d{9}$", R.string.register_studentId_label, R.string.register_studentId_error));
		// ���mygdufs_pwd
		form.put(FormValidator.createInputData(R.id.register_mygdufs_pwd, "mygdufs_pwd", R.id.register_mygdufs_pwd_label, "^.+$", R.string.register_mygdufs_pwd_label, R.string.register_mygdufs_pwd_error));
		
		validator = new FormValidator(this, form);
		// ����ʧȥ����ʱ�Զ���֤
		validator.addOnFocusChangeValidate();
		
		// ��Ӧ���ذ�ť���ж��Ƿ����޸Ĳ�������ʾ
		register_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				validator.checkBackIfFormModified();
			}
		});
		
		// ��Ӧע�ᰴť���ύע�����������ת����ע��ɹ��򷵻ص����˺Ź�������
		register_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(validator.isFormModified()) {
					if(validator.isFormCorrect()) {
						JSONObject input = validator.getInput();
						// ���XMPP�û���
						try {
							input.putOpt("apn_username", storager.get(Constants.XMPP_USERNAME, ""));
						} catch (Exception e) {
							Log.d("JSON����", "RegisterActivity, e=" + e.toString());
						}
						new HttpConnectionUtils(connectionHandler, storager).post(ProjectConstants.URL_REGISTER, input);
					} else {
						Log.d("����֤", "ʧ��");
						ProjectConstants.alertDialog(RegisterActivity.this, "�������", "�밴����ʾ�޸�", true);
					}
				} else {
					// ��ʾû�иĶ�
					Toast.makeText(RegisterActivity.this, "���޸�", Toast.LENGTH_SHORT);
				}
			}
		});
    }
    
    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	appMenu.onCreateOptionsMenu(menu);
    	return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Log.d("MainActivity", "onOptionsItemSelected������");
    	return appMenu.onOptionsItemSelected(item);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	if(storager.get("isLogin", false)) {
    		// ��ʾ��¼�飬����δ��¼��
    		menu.setGroupVisible(AppMenu.NOTLOGIN, false);
    		menu.setGroupVisible(AppMenu.ISLOGIN, true);
    	} else {
    		// ��ʾδ��¼�飬���ص�¼��
    		menu.setGroupVisible(AppMenu.NOTLOGIN, true);
    		menu.setGroupVisible(AppMenu.ISLOGIN, false);
    	}
    	return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)	{
    	if(keyCode == KeyEvent.KEYCODE_BACK) {
    		validator.checkBackIfFormModified();
    	}
    	return super.onKeyDown(keyCode, event);
    }
    private HttpConnectionHandler connectionHandler = new HttpConnectionHandler(this) {
    	@Override
    	protected void onSucceed(JSONObject result) {
    		super.onSucceed(result);
    		// TODO ���ｫֱ��ʹ��LoginActivity��¼�ɹ�ʱ�Ĵ���
			storager
			.set("username", result.optString("username"))
			.set("sessionid", result.optString("sessionid"))
			.set("isLogin", true)
			.save();
			// ��ת��MainActivity
			startActivity(new Intent(getApplicationContext(), MainActivity.class));
			Toast.makeText(RegisterActivity.this, "��ӭ��ĵ�����" + result.optString("username"), Toast.LENGTH_SHORT).show();
			finish();
    	}
    	@Override
    	protected void onFailed(JSONObject result) {
    		super.onFailed(result);
    		if(result.optString("resultMsg", "").matches("^\\d$")) {
    			// ��������״̬��
    			Toast.makeText(RegisterActivity.this, result.optString("resultMsg", "") + "����������", Toast.LENGTH_SHORT).show();
    		} else if(result.has("formErrors")) {
				// ����֤ʧ��
				JSONObject formErrors = result.optJSONObject("formErrors");
				// ����FormValidatorʵ������������Ϣ��ʾ��label��
				validator.updateFormMessageFromServer(formErrors);
			} else {
				ProjectConstants.alertDialog(RegisterActivity.this, "ע��ʧ��", result.optString("resultMsg"), true);
			}
    	}
    };
}