package com.molice.oneingdufs.layouts;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.activities.AboutActivity;
import com.molice.oneingdufs.activities.LoginActivity;
import com.molice.oneingdufs.activities.RegisterActivity;
import com.molice.oneingdufs.activities.SettingsActivity;
import com.molice.oneingdufs.activities.UserHomeActivity;
import com.molice.oneingdufs.androidpn.ServiceManager;
import com.molice.oneingdufs.utils.Debug;
import com.molice.oneingdufs.utils.Logout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * ���ݵ�¼״̬�Ĳ�ͬ������Activity��Menu
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-3-25
 */
public class AppMenu {
	private Context context;
	
	// �ѵ�¼��
	public final static int ISLOGIN = 0;
	// δ��¼��
	public final static int NOTLOGIN = 1;
	
	private final int mUser = 0;// �˺Ź���
	private final int mSetting = 1;// ������� 
	private final int mLogout = 2;// �˳���¼
	private final int mAbout = 3;// ����
	private final int mClose = 5;// �˳����
	private final int mRegister = 6;// ע��
	private final int mLogin = 7;// ��¼
	private final int mClearData = 8;// ��մ洢���ݣ�������
	
	public AppMenu(Context context) {
		this.context = context;
	}
	
	public void onCreateOptionsMenu(Menu menu) {
		// �ѵ�¼��
		// ��������
		menu.add(ISLOGIN, mUser, 0, R.string.menu_user);
		// �������
		menu.add(ISLOGIN, mSetting, 1, R.string.menu_setting);
		// �˳���¼
		menu.add(ISLOGIN, mLogout, 2, R.string.menu_logout);
		// ����
		menu.add(ISLOGIN, mAbout, 3, R.string.menu_about);
		// �˳����
		menu.add(ISLOGIN, mClose, 4, R.string.menu_close);
		
		// δ��¼��
		// ��¼
		menu.add(NOTLOGIN, mLogin, 0, R.string.menu_login);
		// �������
		menu.add(NOTLOGIN, mSetting, 1, R.string.menu_setting);
		// ע��
		menu.add(NOTLOGIN, mRegister, 2, R.string.menu_register);
		// ����
		menu.add(NOTLOGIN, mAbout, 3, R.string.menu_about);
		// �˳�
		menu.add(NOTLOGIN, mClose, 4, R.string.menu_close);
		
		// �����ã���մ洢����
		menu.add(ISLOGIN, mClearData, 5, "����û�����");
		menu.add(NOTLOGIN, mClearData, 5, "����û�����");
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		int item_id = item.getItemId();
		switch(item_id) {
		case mUser:
			// ��������
			context.startActivity(new Intent(context.getApplicationContext(), UserHomeActivity.class));
			break;
		case mSetting:
			// �������
			context.startActivity(new Intent(context.getApplicationContext(), SettingsActivity.class));
			break;
		case mLogout:
			// �˳���¼
			Logout logout = new Logout(context);
			logout.logout();
			break;
		case mAbout:
			// ����
			context.startActivity(new Intent(context.getApplicationContext(), AboutActivity.class));
			break;
		case mClose:
			// �˳����
			new AlertDialog.Builder(context)
				.setTitle("ȷ���˳���")
				.setMessage("�˳�������ͷ��ٲ����ֻ���Դ��������ܽ��ղ�����Ϣ֪ͨ�����鱣�ֺ�̨����")
				.setPositiveButton("�˳�", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Activity activity = (Activity) context;
						if(SettingsActivity.getNotificationEnabled(activity))
							new ServiceManager(activity).stopService();
						activity.finish();
						System.exit(0);
					}
				})
				.setNegativeButton("ȡ��", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				})
				.show();
			break;
		case mRegister:
			// ע��
			context.startActivity(new Intent(context.getApplicationContext(), RegisterActivity.class));
			break;
		case mLogin:
			// ��¼
			context.startActivity(new Intent(context.getApplicationContext(), LoginActivity.class));
			break;
		case mClearData:
			// �������
			Debug.clearAllUserData(context);
			Toast.makeText(context, "�û����������", Toast.LENGTH_SHORT).show();
			break;
		}
		
		return true;
	}
}