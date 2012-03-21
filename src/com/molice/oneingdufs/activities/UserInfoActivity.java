package com.molice.oneingdufs.activities;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONObject;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.interfaces.OnHttpRequestListener;
import com.molice.oneingdufs.utils.ClientToServer;
import com.molice.oneingdufs.utils.FormValidator;
import com.molice.oneingdufs.utils.SharedPreferencesStorager;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * �û�����-������Ϣ��ͼ<br />
 * R.layout.user_info
 */
public class UserInfoActivity extends Activity {
	private TextView info_username;
	private TextView info_studentId;
	private EditText info_email;
	private EditText info_truename;
	// TODO ���ֻ�������Ϊ�Զ����EditText����EditText�ڲ����һ����ť�����°�ťʱ��ȡ�������벢���
	private EditText info_telphone;
	private EditText info_cornet;
	private EditText info_qq;
	private Button info_cancel;
	private Button info_submit;
	
	private SharedPreferencesStorager storager;
	private FormValidator validator;
	private ClientToServer client;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info);
        
		// ���ñ�����
		TextView currentActivity = (TextView) findViewById(R.id.actionbar_currentActivity);
		currentActivity.setText(R.string.user_info_title);
		
		// ��ʼ��View��Ա
		info_username = (TextView) findViewById(R.id.user_info_username);
		info_studentId = (TextView) findViewById(R.id.user_info_studentId);
		info_email = (EditText) findViewById(R.id.user_info_email);
		info_truename = (EditText) findViewById(R.id.user_info_truename);
		info_telphone = (EditText) findViewById(R.id.user_info_telphone);
		info_cornet = (EditText) findViewById(R.id.user_info_cornet);
		info_qq = (EditText) findViewById(R.id.user_info_qq);
		info_cancel = (Button) findViewById(R.id.user_info_cancel);
		info_submit = (Button) findViewById(R.id.user_info_submit);
		
		storager = new SharedPreferencesStorager(this);
		client = new ClientToServer(this);
		
		
		// ����֤��
		JSONArray form = new JSONArray();
		form.put(FormValidator.createInputData(R.id.user_info_email, "email", R.id.user_info_email_label, "^([\\w\\d_\\.-]+)@([\\w\\d_-]+\\.)+\\w{2,4}$|^.{0}$", R.string.user_info_email_label, R.string.user_info_email_error));
		form.put(FormValidator.createInputData(R.id.user_info_truename, "truename", R.id.user_info_truename_label, "^[\u00b7\u4e00-\u9fa5]*$|^.{0}$", R.string.user_info_truename_label, R.string.user_info_truename_error));
		form.put(FormValidator.createInputData(R.id.user_info_telphone, "telphone", R.id.user_info_telphone_label, "^\\d{11}$|^.{0}$", R.string.user_info_telphone_label, R.string.user_info_telphone_error));
		form.put(FormValidator.createInputData(R.id.user_info_cornet, "cornet", R.id.user_info_cornet_label, "^\\d{3,6}$|^.{0}$", R.string.user_info_cornet_label, R.string.user_info_cornet_error));
		form.put(FormValidator.createInputData(R.id.user_info_qq, "qq", R.id.user_info_qq_label, "^\\d{0,10}$|^.{0}$", R.string.user_info_qq_label, R.string.user_info_qq_error));
		// ��name�����ﱻsetTag
		validator = new FormValidator(this, form);
		// ����ʧȥ����ʱ�Զ���֤
		validator.addOnFocusChangeValidate();

		// ��ʾ�û�����ѧ��
		if(storager.isExist("username")) {
			info_username.setText(storager.get("username", "��ǰ�û�"));
		}
		// ��ʾ������
		validator.setInputFromLocalStorage();
		
		info_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(validator.isFormModified()) {
					if(validator.isFormCorrect()) {
						// ��֤ͨ������������
						JSONObject input = validator.getInput();
						Log.d("UserInfo��֤ͨ��", input.toString());
					} else {
						validator.alertFormMsgDialog(null, null);
					}
				} else {
					validator.alertFormToast("���޸�");
				}
			}
		});
	}
	
	private OnHttpRequestListener listener = new OnHttpRequestListener() {
		
		@Override
		public void onTimeout(int requestCode, ClientToServer target,
				HttpRequestBase method, ConnectTimeoutException e) {
		}
		
		@Override
		public void onSuccess(int requestCode, ClientToServer target,
				HttpRequestBase method, HttpResponse response, JSONObject result) {
		}
		
		@Override
		public void onFailed(int requestCode, ClientToServer target,
				HttpRequestBase method, HttpResponse response) {
		}
		
		@Override
		public void onException(int requestCode, ClientToServer target,
				HttpRequestBase method, Exception e) {
		}
		
		@Override
		public void beforeSend(int requestCode, ClientToServer target,
				HttpRequestBase method, HttpClient client) {
		}
		
		@Override
		public void afterSend(int requestCode, ClientToServer target,
				HttpRequestBase method, HttpClient client) {
		}
	};
}
