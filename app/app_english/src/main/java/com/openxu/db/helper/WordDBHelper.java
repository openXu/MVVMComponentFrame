package com.openxu.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.openxu.utils.MyUtil;

public class WordDBHelper extends SQLiteOpenHelper {

	private static final String TAG = "WordDBHelper";
	private static final String NAME = "openword.db";
	
	private static final int START_VERSION = 1;
	private static final int HISTORY_VERSION1 = 1;
	private static final int CURRENT_VERSION = 2;

	public static final String TABLE_ID = "_id";
	
	public static final String TABLE_SENTENCE = "sentence"; // 每日一句
	public static final String TABLE_WORDg = "wordgsn"; // 单词表
	public static final String TABLE_WORD4 = "wordji"; // 单词表
	public static final String TABLE_WORD6 = "wordshun"; // 单词表
	public static final String TABLE_WORD8 = "wordfa"; // 单词表
	public static final String TABLE_MYWORD = "myword"; // 自己添加的单词本
	
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
	public static final String TABLE_WORD_ADDDATE= "addDate";   //添加日期
	
	public static final String TABLE_WORD_FXDATE= "fxDate";   //复习日期
	public static final String TABLE_WORD_FX= "fx";   //是否复习
	
	
	
	public static final String TABLE_SENTENCE_SID = "sid"; //每日一句ID   
	public static final String TABLE_SENTENCE_TTS = "tts"; //音频地址    
	public static final String TABLE_SENTENCE_CON = "content"; //英文内容 
	public static final String TABLE_SENTENCE_NOT = "note"; //中文内容
	public static final String TABLE_SENTENCE_LOVE = "love"; //每日一句喜欢个数 
	public static final String TABLE_SENTENCE_TRANS = "translation"; //词霸小编 
	public static final String TABLE_SENTENCE_PIC = "picture"; //图片地址
	public static final String TABLE_SENTENCE_DATE = "dateline"; //时间
	public static final String TABLE_SENTENCE_SPV = "s_pv"; //浏览数
	public static final String TABLE_SENTENCE_FXIMG = "fenxiang_img"; //分享图片
	
	public static final String TABLE_MYWORD_NAME = "name";             // 单词本名称
	public static final String TABLE_MYWORD_TABLE = "wordTable";       //对应单词表
	public static final String TABLE_MYWORD_CREATEDATE = "createDate"; // 日期
	
	public static final int BOOLEAN_TRUE = 1; 
	public static final int BOOLEAN_FALSE = 0; 
	
	private static WordDBHelper helper = null;

	public static WordDBHelper getInstance(Context context) {
		if (helper == null) {
			helper = new WordDBHelper(context);
		}
		return helper;
	};

	// 每次更改数据库版本后，需要在此处传入当前版本
	public WordDBHelper(Context context) {
		super(context, NAME, null, CURRENT_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
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
			case HISTORY_VERSION1: // 添加复习日期
				String sql = "alter table "+TABLE_WORDg+" add "+TABLE_WORD_FXDATE +" varchar(30)";
				db.execSQL(sql);
				sql = "alter table "+TABLE_WORDg+" add "+TABLE_WORD_FX +" varchar(30)";
				db.execSQL(sql);
				sql = "update "+TABLE_WORDg+" set "+TABLE_WORD_FXDATE +" = \"\" , "+TABLE_WORD_FX +" = 0";
				db.execSQL(sql);
				
				sql = "alter table "+TABLE_WORD4+" add "+TABLE_WORD_FXDATE +" varchar(30)";
				db.execSQL(sql);
				sql = "alter table "+TABLE_WORD4+" add "+TABLE_WORD_FX +" varchar(30)";
				db.execSQL(sql);
				sql = "update "+TABLE_WORD4+" set "+TABLE_WORD_FXDATE +" = \"\" , "+TABLE_WORD_FX +" = 0";
				db.execSQL(sql);
				
				sql = "alter table "+TABLE_WORD6+" add "+TABLE_WORD_FXDATE +" varchar(30)";
				db.execSQL(sql);
				sql = "alter table "+TABLE_WORD6+" add "+TABLE_WORD_FX +" varchar(30)";
				db.execSQL(sql);
				sql = "update "+TABLE_WORD6+" set "+TABLE_WORD_FXDATE +" = \"\" , "+TABLE_WORD_FX +" = 0";
				db.execSQL(sql);
				
				sql = "alter table "+TABLE_WORD8+" add "+TABLE_WORD_FXDATE +" varchar(30)";
				db.execSQL(sql);
				sql = "alter table "+TABLE_WORD8+" add "+TABLE_WORD_FX +" varchar(30)";
				db.execSQL(sql);
				sql = "update "+TABLE_WORD8+" set "+TABLE_WORD_FXDATE +" = \"\" , "+TABLE_WORD_FX +" = 0";
				db.execSQL(sql);
				
				db.execSQL("CREATE TABLE " + TABLE_MYWORD + " ( " + TABLE_ID
						+ " integer primary key autoincrement, " 
						+ TABLE_MYWORD_NAME + " TEXT, " 
						+ TABLE_MYWORD_TABLE + " TEXT, " 
						+ TABLE_MYWORD_CREATEDATE + " TEXT)") ;
				MyUtil.LOG_E(TAG, "创建单词本表");
				break;
			}
		}
	}
}
