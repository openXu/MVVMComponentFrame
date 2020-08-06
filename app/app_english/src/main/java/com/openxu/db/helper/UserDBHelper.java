package com.openxu.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.openxu.utils.MyUtil;

public class UserDBHelper extends SQLiteOpenHelper {

	private static final String TAG = "WordDBHelper";
	private static final String NAME = "openuser.db";
	
	private static final int START_VERSION = 1;
	private static final int HISTORY_VERSION1 = 1;
	private static final int CURRENT_VERSION = 1;

	public static final String TABLE_ID = "_id";
	
	public static final String TABLE_USER = "user"; 
	public static final String TABLE_USER_OBJID = "objectId";
	public static final String TABLE_USER_NAME = "name"; 
	public static final String TABLE_USER_PASW = "pasw"; 
	public static final String TABLE_USER_CHATNAME = "chatName"; 
	public static final String TABLE_USER_CHATPASW = "chatPasw"; 
	public static final String TABLE_USER_POINT = "point"; 
	public static final String TABLE_USER_JINGYAN = "jingyan"; 
	public static final String TABLE_USER_MAIL = "mail";
	public static final String TABLE_USER_ICON = "icon";
	public static final String TABLE_USER_PHONE = "phone";
	public static final String TABLE_USER_LEV = "level";
	public static final String TABLE_USER_NUMDAY = "numDay";
	public static final String TABLE_USER_NUMP = "nump";
	public static final String TABLE_USER_X1 = "x1";   //性别
	public static final String TABLE_USER_X2 = "x2";

	public static final int BOOLEAN_TRUE = 1; 
	public static final int BOOLEAN_FALSE = 0; 
	
	private static UserDBHelper helper = null;

	public static UserDBHelper getInstance(Context context) {
		if (helper == null) {
			helper = new UserDBHelper(context);
		}
		return helper;
	};

	// 每次更改数据库版本后，需要在此处传入当前版本
	public UserDBHelper(Context context) {
		super(context, NAME, null, CURRENT_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		MyUtil.LOG_E(TAG, "没有用户表，现在创建一个");
		db.execSQL("CREATE TABLE " + TABLE_USER + " (" 
	            + TABLE_ID+ " integer primary key autoincrement, " 
	            + TABLE_USER_OBJID+ " TEXT, " 
				+ TABLE_USER_NAME+ " TEXT, " 
				+ TABLE_USER_PASW+ " TEXT, " 
				+ TABLE_USER_CHATNAME+ " TEXT, " 
				+ TABLE_USER_CHATPASW+ " TEXT, " 
				+ TABLE_USER_POINT+ " TEXT, " 
				+ TABLE_USER_JINGYAN+ " TEXT, " 
				+ TABLE_USER_MAIL+ " TEXT, " 
				+ TABLE_USER_ICON+ " TEXT, " 
				+ TABLE_USER_PHONE+ " TEXT, " 
				+ TABLE_USER_LEV+ " TEXT, " 
				+ TABLE_USER_NUMDAY+ " TEXT, " 
				+ TABLE_USER_NUMP+ " TEXT, " 
				+ TABLE_USER_X1+ " TEXT, " 
				+ TABLE_USER_X2+ " TEXT)") ;
		onUpgrade(db, START_VERSION, CURRENT_VERSION);
	}

	/**
	 * 删除数据库
	 * 
	 * @param context
	 * @return
	 */
	public boolean deleteDatabase(Context context) {
		return context.deleteDatabase(NAME);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		for (int j = oldVersion; j <= newVersion; j++) {
			// 数据库更行
			switch (j) {
			case HISTORY_VERSION1: // 添加用户表
				break;
			}
		}
	}
}
