package com.molice.oneingdufs.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {
	public final static int DATABASE_VERSION = 1;
	
	private static SQLiteDatabase editor;
	private static SQLiteDatabase reader;
	
	private Context context;
	
	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, new SharedPreferencesStorager(context).get("username", "test"), factory, version);
		
		this.context = context;
		editor = this.getWritableDatabase();
		reader = this.getReadableDatabase();
	}
	
	public DatabaseHelper(Context context) {
		this(context, null, null, DATABASE_VERSION);
	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {
		// 每次成功打开数据库后进行操作
		super.onOpen(db);
		Log.d("数据库操作", "DatabaseHelper#onOpen");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 创建数据库后对数据库进行操作
		Log.d("数据库操作", "DatabaseHelper#onCreate");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 更改数据库版本后进行操作
		Log.d("数据库操作", "DatabaseHelper#onUpgrade");
	}
	
	public long insert(String table, ContentValues values) {
		return editor.insert(table, null, values);
	}
	
	public Cursor get(String table, String col, String value) {
		try {
			return reader.query(table, null, col + "=?", new String[] {value}, null, null, null);
		} catch (Exception e) {
			Log.d("数据库读取", "DatabaseHelper#get(col=" + col + ", value=" + value + ", e=" + e.toString());
			Toast.makeText(context, "数据库读取错误", Toast.LENGTH_SHORT).show();	
			return null;
		}
	}
	
	public int update(String table, ContentValues values, String col, String value) {
		try {
			return editor.update(table, values, col + "=?", new String[] {value});
		} catch (Exception e) {
			Log.d("数据库更新", "DatabaseHelper#update(table=" + table + ", values=" + values.toString() + ", col=" + col + ", value=" + value + ", e=" + e.toString());
			Toast.makeText(context, "数据库更新错误", Toast.LENGTH_SHORT).show();
			return -1;
		}
	}
	
	public boolean delete(String table, String col, String value) {
		return editor.delete(table, col + "=?", new String[] {value}) > 0;
	}
	
	public long updateOrInsert(String table, ContentValues values, String col, String value) {
		if(get(table, col, value) != null) {
			return update(table, values, col, value);
		}
		return insert(table, values);
	}
	
	public void closeAll() {
		close();
		editor.close();
		reader.close();
	}
	
	public boolean isTableExist(String table) {
		if(table != null) {
			try {
				Cursor cursor = editor.rawQuery("SELECT COUNT(*) as c FROM sqlite_master WHERE type='table' and name='" + table + "'", null);
				if(cursor.moveToNext()) {
					int count = cursor.getInt(0);
					cursor.close();
					if(count > 0)
						return true;
				}
				cursor.close();
			} catch (Exception e) {
				Log.d("数据库-表检查异常", "DatabaseHelper#isTableExist(table=" + table + "), e=" + e.toString());
			}
		}
		return false;
	}
	
	public boolean createTableIfNotExist(String table) {
		if(!isTableExist(table)) {
			try {
				if(table.equals(DB.MESSAGE)) {
					editor.execSQL(DB.CREATE_TABLE_MESSAGE);
					return true;
				}
				if(table.equals(DB.GROUP)) {
					editor.execSQL(DB.CREATE_TABLE_GROUP);
					return true;
				}
			} catch (Exception e) {
				Log.d("数据库-创建表异常", "DatabaseHelper#createTableIfNotExist, table=" + table + ", e=" + e.toString());
				return false;
			}
			return false;
		}
		return true;
	}
	
	public static class DB {
		// 消息表
		public final static String MESSAGE = "message";
		public final static String CREATE_TABLE_MESSAGE = "CREATE TABLE " + MESSAGE + " (id int(11) not null, title varchar(20) not null, _from varchar(30) not null, content text not null, date text not null);";
		
		// 群组表
		public final static String GROUP = "user_group";
		public final static String CREATE_TABLE_GROUP = "CREATE TABLE " + GROUP + " (id int(11) not null, name varchar(20) not null, member text not null, timestamp char(19));";
	}
}
