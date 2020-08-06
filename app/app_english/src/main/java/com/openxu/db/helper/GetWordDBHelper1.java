package com.openxu.db.helper;

import java.util.HashMap;
import java.util.Map;

import com.openxu.db.helper.GetWordDBHelper.DBHelper;
import com.openxu.utils.Constant;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GetWordDBHelper1 extends SQLiteOpenHelper {

	private static final String TAG = "GetWordDBHelper";
	
	private static String dbName;
	private static final int START_VERSION = 1;
	private static final int HISTORY_VERSION1 = 1;
	private static final int CURRENT_VERSION = 1;

	public static final String TABLE_ID = "_id";
	public static final String TABLE_WORD_FIRST = "first"; // 首字母
	public static final String TABLE_WORD_EN = "english"; // 英文
	public static final String TABLE_WORD_LEVEL = "level"; // 级数    
	public static final String TABLE_WORD_LOCAL = "isLocal"; // 本地  
	public static final String TABLE_WORD_ERROR = "isError"; // 错误
	public static final String TABLE_WORD_REMENBER = "remenber"; // 标记以背诵
	public static final String TABLE_WORD_REM_DATE= "date"; // 背诵日期
	public static final String TABLE_WORD_EXCHANGE= "exchange"; // 形式变化
	public static final String TABLE_WORD_PH_EN= "ph_en";   //英式
	public static final String TABLE_WORD_PH_EN3= "ph_en_mp3";   //英式
	public static final String TABLE_WORD_PH_AM= "ph_am";   //美式
	public static final String TABLE_WORD_PH_AM3= "ph_am_mp3";   //美式
	public static final String TABLE_WORD_CHINA= "parts";   //中文
	public static final String TABLE_WORD_SENTS= "sents";   //例句
	public static final String TABLE_WORD_FXDATE= "fxDate";   //复习日期
	public static final String TABLE_WORD_FX= "fx";   //是否复习
	
	public static final int BOOLEAN_TRUE = 1; 
	public static final int BOOLEAN_FALSE = 0; 
	
	private static GetWordDBHelper1 helper;
	public static GetWordDBHelper1 getInstance(Context context) {
		if (helper == null) {
			helper = new GetWordDBHelper1(context);
		}
		return helper;
	};

	// 每次更改数据库版本后，需要在此处传入当前版本
	public GetWordDBHelper1(Context context) {
		super(context, GetWordDBHelper1.dbName, null, CURRENT_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		onUpgrade(db, START_VERSION, CURRENT_VERSION);
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		for (int j = oldVersion; j <= newVersion; j++) {
			// 数据库更行
			switch (j) {
			case HISTORY_VERSION1: // 版本一升级的用户（增加密钥和解密字段）
//				db.execSQL("ALTER TABLE " + TABLE_CHAT_NAME + " ADD COLUMN "
//						+ TABLE_CHAT_KEY + " VARCHAR(200)");
				break;
			}
		}
	}
}
