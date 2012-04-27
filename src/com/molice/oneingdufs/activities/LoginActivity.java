package com.molice.oneingdufs.activities;

import org.json.JSONObject;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.layouts.ActionBarController;
import com.molice.oneingdufs.utils.HttpConnectionHandler;
import com.molice.oneingdufs.utils.HttpConnectionUtils;
import com.molice.oneingdufs.utils.ProjectConstants;
import com.molice.oneingdufs.utils.SharedPreferencesStorager;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * ��¼Activity<br />
 * ��intent.putExtra("onLoginSuccessActivity", Activity.class)�ķ�ʽ����onLoginSuccessActivity|onLoginFailedActivityʱ����תActivity<br />
 * ��¼�ɹ�����ת�󣬵�¼Activity���ᱻfinish()
 * TODO ��Ȼ��������²����ڵ�¼״̬�л����õ���¼���棬��Ϊ���Է���һ��������onCreate()��onStart()��������ӵ�¼״̬���жϣ������ѵ�¼����ת�����˺Ź������棬��ʾ��ǰ�ѵ�¼���Ƿ�Ҫע����������һ���˺ŵ�½��
 */
public class LoginActivity extends Activity {
	// �û��������
	private EditText login_username;
	// ���������
	private EditText login_password;
	// ��¼��ť
	private Button login_submit;
	// ע�ᰴť
	private Button login_register;
	// SharedPreferences
	private SharedPreferencesStorager storager;
	// ��¼���������ܳɹ�ʧ�ܣ�����Activity��ת��Intent
	private Intent intent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// �����ǰ�ѵ�¼��������LoginActivity
		storager = new SharedPreferencesStorager(this);
		if(storager.get("isLogin", false)) {
			Toast.makeText(this, "�ѵ�¼", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		setContentView(R.layout.login);
		
		// ���ñ�����
		ActionBarController.setTitle(this, R.string.login_title);
		
		// ��ʼ����Ա����
		login_username = (EditText) findViewById(R.id.login_username);
		login_password = (EditText) findViewById(R.id.login_password);
		login_submit = (Button) findViewById(R.id.login_submit);
		login_register = (Button) findViewById(R.id.login_register);
		
		// ��������Ѵ洢�������Զ�����û���
		if(storager.has("username")) {
			login_username.setText(storager.get("username", ""));
		}
		
		// ���õ�¼��ť�ĵ�����������е�¼
		login_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				JSONObject data = new JSONObject();
				try {
					data.putOpt("username", login_username.getText().toString());
					data.putOpt("password", login_password.getText().toString());
				} catch (Exception e) {
					Log.d("JSON�쳣", "LoginActivity#submit, e=" + e.toString());
				}
				new HttpConnectionUtils(connectionHandler, LoginActivity.this).post(ProjectConstants.URL.login, data);
			}
		});
		
		// ����ע�ᰴť�ĵ����������ת��@{link RegisterActivity}��finish()@{link LoginActivity}
		login_register.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
				finish();
			}
		});
		
		// ����ı������ݱ仯���¼�������ֻ���û��������붼��Ϊ��ʱ����¼��ť���ܽ��ܵ��
		login_username.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				onInputChanged(s, start, before, count);
			}
		});
		
		login_password.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				onInputChanged(s, start, before, count);
			}
		});
	}
	
    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }
	/**
	 * ������Ӧ�û����������������ı����TextChange�¼� ��������ı���ͬʱ��Ϊ�������õ�¼��ť�������¼��ť������
	 */
	private void onInputChanged(CharSequence s, int start, int before, int count) {
		if (login_username.getText().length() < 4
				|| login_password.getText().length() == 0)
			login_submit.setEnabled(false);
		else
			login_submit.setEnabled(true);
	}
	
	/**
	 * �����¼�������Activity��ת�����ݵ���{@link LoginActivity}ʱָ���ĵ�¼�ɹ���ʧ�ܺ����תActivity������ת����û�У���Ĭ����ת��{@link MainActivity}
	 * ����{@link LoginActivity}��Activity����intent.putExtra("onLoginSuccessActivity", Activity.class)�ķ�ʽ����onLoginSuccessActivity|onLoginFailedActivityʱ����תActivity
	 * @param whichStatus success|failed����־Ҫ����success����failed��������������ֵ������ת��{@link MainActivity}
	 */
	private void callActivityAfterLogin(String whichStatus) {
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			if(whichStatus.equals("success") && extras.get("onLoginSuccessActivity") != null) {
				intent = new Intent(getApplicationContext(), (Class<?>) extras.get("onLoginSuccessActivity"));
				startActivity(intent);
			} else if(whichStatus.equals("failed") && extras.get("onLoginFailedActivity") != null) {
				intent = new Intent(getApplicationContext(), (Class<?>) extras.get("onLoginFailedActivity"));
				startActivity(intent);
			}
		} else {
			//��ΪMainActivity��launchMode="singleTop"�����Բ�������������һ�����������������MainActivityʵ��
			//startActivity(new Intent(getApplicationContext(), MainActivity.class));
		}
		finish();
	}
	
	private HttpConnectionHandler connectionHandler = new HttpConnectionHandler(this) {
		@Override
		protected void onSucceed(JSONObject result) {
			// TODO ��¼�ɹ��������ص������û����ݴ洢��sqlite���Ա�Ӧ�����л�Activityʱ����ÿ�ζ��ӷ�����������������
			super.onSucceed(result);
			JSONObject user_info = result.optJSONObject("user_info");
			JSONObject user_roomaddress = result.optJSONObject("user_roomaddress");
			storager
			.set("isLogin", true)
			.set("sessionid", result.optString("sessionid"))
			.set("username", result.optString("username"))
			.set("studentId", result.optString("studentId"))
			.set("user_info_email", user_info.optString("email", ""))
			.set("user_info_truename", user_info.optString("truename", ""))
			.set("user_info_phone", user_info.optString("phone", ""))
			.set("user_info_cornet", user_info.optString("cornet", ""))
			.set("user_info_qq", user_info.optString("qq", ""))
			.set("user_roomaddress_building", user_roomaddress.optString("building", ""))
			.set("user_roomaddress_room", user_roomaddress.optString("room", ""))
			.save();
			// ���ص�ĳ��Activity
			callActivityAfterLogin("success");
		}
	};
}