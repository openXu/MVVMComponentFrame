package com.openxu.db.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.openxu.db.DatabaseManager;
import com.openxu.db.base.WordDaoSupport;
import com.openxu.db.bean.OpenWord;
import com.openxu.db.helper.WordDBHelper;

public class WordDaoImpl extends WordDaoSupport<OpenWord>{

	private String TAG = "WordDaoImpl";

	public WordDaoImpl() {
		super();
	}

}
