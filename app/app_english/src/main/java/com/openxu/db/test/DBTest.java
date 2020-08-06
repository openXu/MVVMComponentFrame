package com.openxu.db.test;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.openxu.db.DatabaseManager;
import com.openxu.db.bean.OneSentence;
import com.openxu.db.bean.OpenWord;
import com.openxu.db.impl.OneSentenceDaoImpl;
import com.openxu.db.impl.WordDaoImpl;
import com.openxu.utils.MyUtil;

public class DBTest extends AndroidTestCase {
	private static final String TAG = "DBTest";

	public void insert4Word(){
			
	}
	public void findWord(){
		DatabaseManager.initializeInstance(getContext());
		OneSentenceDaoImpl dao = new OneSentenceDaoImpl();
		OneSentence onr = new OneSentence();
		onr.setSid("1");
		onr.setContent("ADADSFSAFASDF");
		onr.setNote("中文");
		long id = dao.insert(onr);
		
		List<OneSentence> list = dao.findAll();
		MyUtil.LOG_E(TAG, "每日一句："+list.size());
		MyUtil.LOG_E(TAG, "每日一句："+list.get(0));	
	}
	public void testDate() {
		// 初始化数据库管理工具类
		DatabaseManager.initializeInstance(getContext());
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		//插入数据不能带空格
		db.execSQL("update word4 set date =20150213, remenber =1 where _id BETWEEN 17 AND 26");
		db.execSQL("update word4 set date =20150214, remenber =1 where _id BETWEEN 121 AND 150");
		db.execSQL("update word4 set date =20150303, remenber =1 where _id BETWEEN 211 AND 230");
		db.execSQL("update word4 set date =20150313, remenber =1 where _id BETWEEN 271 AND 370");
		db.execSQL("update word4 set date =20150422, remenber =1 where _id BETWEEN 391 AND 400");
		db.execSQL("update word4 set date =20150430, remenber =1 where _id BETWEEN 421 AND 520");
		db.execSQL("update word4 set date =20150511, remenber =1 where _id BETWEEN 600 AND 700");
		db.execSQL("update word4 set date =20150514, remenber =1 where _id BETWEEN 702 AND 703");
		DatabaseManager.getInstance().closeWordDatabase();
	}
	
	public void find(){
		DatabaseManager.initializeInstance(getContext());
		WordDaoImpl dao = new WordDaoImpl();
		List<OpenWord> words = dao.findByCondition(" _id BETWEEN ? and ? ", new String[]{"121", "151"}, null);
		for(OpenWord word : words)
			MyUtil.LOG_V(TAG, "单词："+word);
		DatabaseManager.getInstance().closeWordDatabase();
	}
}