package com.openxu.db.manager;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.openxu.core.utils.XLog;
import com.openxu.db.dao.DaoMaster;
import com.openxu.db.dao.WordDao;

import org.greenrobot.greendao.database.Database;


/**
 * autour : openXu
 * date : 2017/8/31 13:52
 * className : DatabaseHelper
 * version : 1.0
 * description : DBHelper，此类控制数据库表结构初始化，以及数据库版本升级
 */
public class DatabaseHelper extends DaoMaster.OpenHelper{
    private String TAG = "DatabaseHelper";

    public static String DB_NAME = "openword.db";

    public DatabaseHelper(Context context, String name) {
        super(context, DB_NAME);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, DB_NAME, factory);
    }
    @Override
    public void onCreate(Database db) {
        XLog.w("======================初始化数据库表结构");
        super.onCreate(db);
    }
    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        XLog.w("======================数据库升级oldVersion=" + oldVersion + " to  newVersion=" + newVersion );
        MigrationHelper.getInstance().migrate(db, WordDao.class);
    }
}
