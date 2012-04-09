package com.molice.oneingdufs.activities;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.interfaces.OnHttpRequestListener;
import com.molice.oneingdufs.layouts.ActionBarController;
import com.molice.oneingdufs.layouts.AppMenu;
import com.molice.oneingdufs.utils.ClientToServer;
import com.molice.oneingdufs.utils.ProjectConstants;
import com.molice.oneingdufs.utils.SharedPreferencesStorager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
	// ѡ��˵�Menu
	private AppMenu appMenu;
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
		
		appMenu = new AppMenu(this);
		
		// ��������Ѵ洢�������Զ�����û���
		if(storager.isExist("username")) {
			login_username.setText(storager.get("username", ""));
		}
		
		// ���õ�¼��ť�ĵ�����������е�¼
		login_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String username = login_username.getText().toString();
				String password = login_password.getText().toString();
				if(!username.equals("") && username.equals(storager.get("username", "")) && password.equals(storager.get("password", ""))) {
					// ��¼�ɹ�
					storager.set("isLogin", true).save();
					Toast.makeText(LoginActivity.this, "��ӭ������" + storager.get("username", ""), Toast.LENGTH_LONG).show();
					// ���ص�ĳ��Activity
					callActivityAfterLogin("success");
				} else {
					// ��¼ʧ��
					Toast.makeText(LoginActivity.this, "��¼ʧ�ܣ������û��������룬��������ע��", Toast.LENGTH_LONG).show();
				}
				/*
				// ������������ӣ�׼����¼
				ClientToServer client = new ClientToServer(LoginActivity.this);
				// ���http�����������������Ĳ�ͬ�׶ν��в���
				client.setOnRequestListener(httpRequestListener);
				// ���û�����������ӵ�http����
				JSONObject postData = new JSONObject();
				try {
					postData.put("username", login_username.getText().toString());
					postData.put("password", login_password.getText().toString());
				} catch (Exception e) {
					Log.d("JSON����", "LoginActivity, e=" + e.toString());
				}
				// ����post��¼���������־λΪ0
				client.post(ProjectConstants.URL_LOGIN, postData, 0);
				*/
			}
		});
		
		// ����ע�ᰴť�ĵ����������ת��@{link RegisterActivity}��finish()@{link LoginActivity}
		login_register.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
				startActivity(intent);
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
		}
		intent = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(intent);
		finish();
	}
	
	private OnHttpRequestListener httpRequestListener = new OnHttpRequestListener() {
		
		@Override
		public void onTimeout(int requestCode, ClientToServer target, HttpRequestBase method, ConnectTimeoutException e) {
			Log.d("HTTP-TimeOut", "Timeout");
		}
		
		@Override
		public void onSuccess(int requestCode, ClientToServer target, HttpRequestBase method, HttpResponse response,
				JSONObject result) {
			switch(requestCode) {
			case 0:
				// ��¼POST /home/login/
				if(result.optBoolean("success")) {
					// TODO ��¼�ɹ��������ص������û����ݴ洢��sqlite���Ա�Ӧ�����л�Activityʱ����ÿ�ζ��ӷ�����������������
					storager
					.set("username", result.optString("username"))
					.set("sessionid", result.optString("sessionid"))
					.set("isLogin", true)
					.save();
					// ���ص�ĳ��Activity
					callActivityAfterLogin("success");
				} else {
					// ��¼ʧ��
					new AlertDialog.Builder(LoginActivity.this)
					.setTitle("��¼ʧ��")
					.setMessage(result.optString("resultMsg"))
					.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					})
					.show();
				}
				break;
			}
		}
		
		@Override
		public void onFailed(int requestCode, ClientToServer target, HttpRequestBase method, HttpResponse response) {
			Log.d("HTTP����ʧ��", "������룺" + String.valueOf(response.getStatusLine().getStatusCode()));
			// ����ʧ�ܣ��ж�����
			method.abort();
		}
		
		@Override
		public void onException(int requestCode, ClientToServer target, HttpRequestBase method, Exception e) {
			Log.d("HTTP�׳��쳣", e.toString());
		}
		
		@Override
		public void beforeSend(int requestCode, ClientToServer target, HttpRequestBase method, HttpClient client) {
		}
		
		@Override
		public void afterSend(int requestCode, ClientToServer target, HttpRequestBase method, HttpClient client) {
			// �ر�����
			client.getConnectionManager().shutdown();
		}
	};
}