package com.openxu.db;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.openxu.db.helper.GetWordDBHelper;
import com.openxu.db.helper.GetWordDBHelper.DBHelper;
import com.openxu.db.helper.UserDBHelper;
import com.openxu.db.helper.WordDBHelper;
import com.openxu.utils.BookUtils;

public class DatabaseManager {
//	private static AtomicInteger mWordOpenCounter;
	private static AtomicInteger mUserOpenCounter;

	private static DatabaseManager instance;
	private static SQLiteOpenHelper mWordDatabaseHelper;
	private static SQLiteOpenHelper mUserDatabaseHelper;
	private SQLiteDatabase mWordDatabase;
	private SQLiteDatabase mUserDatabase;
	
	private static Map<String, DBHelper> helperMap = new HashMap<String, DBHelper>();
	private static Map<String, AtomicInteger> openCounterMap;
	private static Map<String, SQLiteDatabase> dataBaseMap;

	public static synchronized void initializeInstance(Context context) {
		if (instance == null) {
			instance = new DatabaseManager();
			mWordDatabaseHelper = WordDBHelper.getInstance(context);
//			mWordOpenCounter = new AtomicInteger();
			mUserDatabaseHelper = UserDBHelper.getInstance(context);
			mUserOpenCounter = new AtomicInteger();
			
			helperMap = new HashMap<String, DBHelper>();
			openCounterMap = new HashMap<String, AtomicInteger>();
			dataBaseMap = new HashMap<String, SQLiteDatabase>();
			for(String dbName : BookUtils.DB_TABLE_NAMES){
				DBHelper helper = GetWordDBHelper.getInstance(context, dbName);
				helperMap.put(dbName, helper);
				AtomicInteger wordOpenCounter = new AtomicInteger();
				openCounterMap.put(dbName, wordOpenCounter);
			}
		}
	}

	public static synchronized DatabaseManager getInstance() {
		if (instance == null) {
			throw new IllegalStateException(
					DatabaseManager.class.getSimpleName()+ " is not initialized, call initializeInstance(..) method first.");
		}
		return instance;
	}
	
	public synchronized SQLiteDatabase openWordDatabase() {
//		if (mWordOpenCounter.incrementAndGet() == 1) {
			if(mWordDatabase==null){
			mWordDatabase = mWordDatabaseHelper.getWritableDatabase();
		}
		return mWordDatabase;
	}
	public synchronized void closeWordDatabase() {
		/*if (mWordOpenCounter.decrementAndGet() == 0) {
			// Closing database
			mWordDatabase.close();
		}*/
	}
	
	public synchronized SQLiteDatabase openWordDatabase(String dbName) {
		AtomicInteger openCounter = openCounterMap.get(dbName);
		if (openCounter.incrementAndGet() == 1) {
			DBHelper helper = helperMap.get(dbName);
			SQLiteDatabase database = helper.getWritableDatabase();
			dataBaseMap.put(dbName, database);
		}
		return dataBaseMap.get(dbName);
	}
	public synchronized void closeWordDatabase(String dbName) {
		AtomicInteger openCounter = openCounterMap.get(dbName);
		if (openCounter.decrementAndGet() == 0) {
			// Closing database
			dataBaseMap.get(dbName).close();
		}
	}
	
	public synchronized SQLiteDatabase openUserDatabase() {
		if (mUserOpenCounter.incrementAndGet() == 1) {
			// Opening new database
			mUserDatabase = mUserDatabaseHelper.getWritableDatabase();
		}
		return mUserDatabase;
	}
	public synchronized void closeUserDatabase() {
		if (mUserOpenCounter.decrementAndGet() == 0) {
			// Closing database
			mUserDatabase.close();
		}
	}

}
