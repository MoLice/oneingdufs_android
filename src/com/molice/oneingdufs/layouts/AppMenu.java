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
 * 根据登录状态的不同，生成Activity主Menu
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-3-25
 */
public class AppMenu {
	private Context context;
	
	// 已登录组
	public final static int ISLOGIN = 0;
	// 未登录组
	public final static int NOTLOGIN = 1;
	
	private final int mUser = 0;// 账号管理
	private final int mSetting = 1;// 软件设置 
	private final int mLogout = 2;// 退出登录
	private final int mAbout = 3;// 关于
	private final int mClose = 5;// 退出软件
	private final int mRegister = 6;// 注册
	private final int mLogin = 7;// 登录
	private final int mClearData = 8;// 清空存储数据，调试用
	
	public AppMenu(Context context) {
		this.context = context;
	}
	
	public void onCreateOptionsMenu(Menu menu) {
		// 已登录组
		// 个人中心
		menu.add(ISLOGIN, mUser, 0, R.string.menu_user);
		// 软件设置
		menu.add(ISLOGIN, mSetting, 1, R.string.menu_setting);
		// 退出登录
		menu.add(ISLOGIN, mLogout, 2, R.string.menu_logout);
		// 关于
		menu.add(ISLOGIN, mAbout, 3, R.string.menu_about);
		// 退出软件
		menu.add(ISLOGIN, mClose, 4, R.string.menu_close);
		
		// 未登录组
		// 登录
		menu.add(NOTLOGIN, mLogin, 0, R.string.menu_login);
		// 软件设置
		menu.add(NOTLOGIN, mSetting, 1, R.string.menu_setting);
		// 注册
		menu.add(NOTLOGIN, mRegister, 2, R.string.menu_register);
		// 关于
		menu.add(NOTLOGIN, mAbout, 3, R.string.menu_about);
		// 退出
		menu.add(NOTLOGIN, mClose, 4, R.string.menu_close);
		
		// 调试用，清空存储数据
		menu.add(ISLOGIN, mClearData, 5, "清空用户数据");
		menu.add(NOTLOGIN, mClearData, 5, "清空用户数据");
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		int item_id = item.getItemId();
		switch(item_id) {
		case mUser:
			// 个人中心
			context.startActivity(new Intent(context.getApplicationContext(), UserHomeActivity.class));
			break;
		case mSetting:
			// 软件设置
			context.startActivity(new Intent(context.getApplicationContext(), SettingsActivity.class));
			break;
		case mLogout:
			// 退出登录
			Logout logout = new Logout(context);
			logout.logout();
			break;
		case mAbout:
			// 关于
			context.startActivity(new Intent(context.getApplicationContext(), AboutActivity.class));
			break;
		case mClose:
			// 退出软件
			new AlertDialog.Builder(context)
				.setTitle("确定退出？")
				.setMessage("退出软件会释放少部分手机资源，但你可能将收不到消息通知，建议保持后台运行")
				.setPositiveButton("退出", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Activity activity = (Activity) context;
						if(SettingsActivity.getNotificationEnabled(activity))
							new ServiceManager(activity).stopService();
						activity.finish();
						System.exit(0);
					}
				})
				.setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				})
				.show();
			break;
		case mRegister:
			// 注册
			context.startActivity(new Intent(context.getApplicationContext(), RegisterActivity.class));
			break;
		case mLogin:
			// 登录
			context.startActivity(new Intent(context.getApplicationContext(), LoginActivity.class));
			break;
		case mClearData:
			// 清空数据
			Debug.clearAllUserData(context);
			Toast.makeText(context, "用户数据已清空", Toast.LENGTH_SHORT).show();
			break;
		}
		
		return true;
	}
}