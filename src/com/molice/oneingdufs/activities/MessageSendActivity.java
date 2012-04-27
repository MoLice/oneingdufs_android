package com.molice.oneingdufs.activities;

import org.json.JSONArray;
import org.json.JSONObject;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.layouts.ActionBarController;
import com.molice.oneingdufs.utils.FormValidator;
import com.molice.oneingdufs.utils.HttpConnectionHandler;
import com.molice.oneingdufs.utils.HttpConnectionUtils;
import com.molice.oneingdufs.utils.ProjectConstants;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MessageSendActivity extends Activity {
	Button cancel;
	Button submit;
	
	FormValidator validator;
	JSONArray form;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_send);
        
        ActionBarController.setTitle(this, R.string.message_send);
        
        cancel = (Button) findViewById(R.id.message_send_cancel);
        submit = (Button) findViewById(R.id.message_send_submit);
        cancel.setOnClickListener(listener);
        submit.setOnClickListener(listener);
        
        form = new JSONArray();
        form.put(FormValidator.createInputData(R.id.message_send_title, "title", R.id.message_send_title_label, "^.{1,}$", R.string.message_send_title_label, R.string.message_send_title_error));
        form.put(FormValidator.createInputData(R.id.message_send_username, "username", R.id.message_send_username_label, "^(\\w|\\d){4,20}$", R.string.message_send_username_label, R.string.message_send_username_error));
        form.put(FormValidator.createInputData(R.id.message_send_content, "content", R.id.message_send_content_label, "^.{1,}$", R.string.message_send_content_label, R.string.message_send_content_error));
        validator = new FormValidator(this, form);
        validator.addOnFocusChangeValidate();
        
	}
	
	@Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event)	{
    	if(keyCode == KeyEvent.KEYCODE_BACK) {
    		validator.checkBackIfFormModified();
    	}
    	return super.onKeyDown(keyCode, event);
    }
	
	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.message_send_cancel:
				// 取消按钮
				validator.checkBackIfFormModified();
				break;
			case R.id.message_send_submit:
				// 提交按钮，发送表单
				if(validator.isFormModified()) {
					if(validator.isFormCorrect()) {
						// 验证通过
						JSONObject input = validator.getInput();
						new HttpConnectionUtils(handler, MessageSendActivity.this).post(ProjectConstants.URL.message_send, input);
					}
				} else {
					// 提示没有改动
					Toast.makeText(MessageSendActivity.this, "无修改", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
		}
	};
	
	private HttpConnectionHandler handler = new HttpConnectionHandler(this) {
		@Override
    	protected void onSucceed(JSONObject result) {
			super.onSucceed(result);
			// TODO 将消息存储到发件箱
			finish();
			Toast.makeText(MessageSendActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
		}
		@Override
		protected void onFailed(JSONObject result) {
			super.onFailed(result);
			// 失败
			Log.d("发送消息失败", "MessageSendActivity#onFailed, result=" + result.toString());
		}
	};
}
