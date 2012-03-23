package com.molice.oneingdufs.activities;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONObject;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.androidpn.Constants;
import com.molice.oneingdufs.interfaces.OnHttpRequestListener;
import com.molice.oneingdufs.utils.ClientToServer;
import com.molice.oneingdufs.utils.FormValidator;
import com.molice.oneingdufs.utils.SharedPreferencesStorager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * ע��Activity<br />
 * TODO �ж��Ƿ��ѵ�¼�����ѵ�¼����û�����ѡ��Ҫôע����¼Ȼ��ע�ᣬҪô�˳�ע�Ტ����
 */
public class RegisterActivity extends Activity {
	private Button register_login;
	private Button register_submit;
	
	private JSONArray form;
	private FormValidator validator;
	private SharedPreferencesStorager storager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        // ���ñ�����
        TextView currentActivity = (TextView) findViewById(R.id.actionbar_currentActivity);
		currentActivity.setText(R.string.register_title);
		
		register_login = (Button) findViewById(R.id.register_login);
		register_submit = (Button) findViewById(R.id.register_submit);
		
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
		
		// ��Ӧ��¼��ť����ת��@{link LoginActivity}��finish()@{link RegisterActivity}
		register_login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), LoginActivity.class);
				startActivity(i);
				finish();
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
							input.put("apn_username", storager.get(Constants.XMPP_USERNAME, ""));
						} catch (Exception e) {
							Log.d("JSON����", "RegisterActivity, e=" + e.toString());
						}
						// ������������ӣ�׼��ע��
						ClientToServer client = new ClientToServer(RegisterActivity.this);
						// ���http�����������������Ĳ�ͬ�׶ν��в���
						client.setOnRequestListener(httpRequestListener);
						// ����������ӵ�http����
						client.post(ClientToServer.URL_REGISTER, input, 0);
					} else {
						Log.d("����֤", "ʧ��");
						validator.alertFormMsgDialog(null, null);
					}
				} else {
					// ��ʾû�иĶ�
					validator.alertFormToast("���޸�");
				}
			}
		});
    }
    
    private OnHttpRequestListener httpRequestListener = new OnHttpRequestListener() {
		
		@Override
		public void onTimeout(int requestCode, ClientToServer target,
				HttpRequestBase method, ConnectTimeoutException e) {
		}
		
		@Override
		public void onSuccess(int requestCode, ClientToServer target,
				HttpRequestBase method, HttpResponse response, JSONObject result) {
			// TODO �жϷ���˷��ص���Ϣ����ע��ɹ����򷵻�sessionid����sessionid�洢������Ȼ��ǰActivity finish��������ת��MainActivity
			// TODO ������֤ʧ�ܣ��򵯳���ʾ����ʾ������Ϣ
			Log.d("���󷵻ؽ��", "result=" + result);
			switch(requestCode) {
			case 0:
				if(result.optBoolean("success")) {
					// �ɹ�
					Log.d("���󷵻ؽ��,�ɹ�, ", "resultMsg:" + result.optString("resultMsg"));
					// TODO ���ｫֱ��ʹ��LoginActivity��¼�ɹ�ʱ�Ĵ���
					storager
					.set("username", result.optString("username"))
					.set("sessionid", result.optString("sessionid"))
					.set("isLogin", true)
					.save();
					// ��ת��MainActivity
					Intent intent = new Intent(getApplicationContext(), MainActivity.class);
					startActivity(intent);
					finish();
				} else {
					// ʧ��
					Log.d("���󷵻ؽ��,ʧ��, ", "resultMsg:" + result.optString("resultMsg"));
					// ����֤ʧ��
					if(result.has("formErrors")) {
						JSONObject formErrors = result.optJSONObject("formErrors");
						// ����FormValidatorʵ������������Ϣ��ʾ��label��
						validator.updateFormMessageFromServer(formErrors);
					} else {
						validator.alertFormMsgDialog("ע��ʧ��", result.optString("resultMsg"));
					}
				}
				break;
			}
		}
		
		@Override
		public void onFailed(int requestCode, ClientToServer target,
				HttpRequestBase method, HttpResponse response) {
			Log.d("����ʧ��", "onFailed:" + String.valueOf(response.getStatusLine().getStatusCode()));
		}
		
		@Override
		public void onException(int requestCode, ClientToServer target,
				HttpRequestBase method, Exception e) {
			Log.d("����ʧ��", "onException");
		}
		
		@Override
		public void beforeSend(int requestCode, ClientToServer target,
				HttpRequestBase method, HttpClient client) {
		}
		
		@Override
		public void afterSend(int requestCode, ClientToServer target,
				HttpRequestBase method, HttpClient client) {
			Log.d("�������", "afterSend");
		}
	};
}