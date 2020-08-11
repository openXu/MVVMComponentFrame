package com.openxu.db.manager;

import android.content.Context;

import com.openxu.db.dao.DaoMaster;
import com.openxu.db.dao.DaoSession;
import com.openxu.db.dao.WordDao;


/**
 * autour : openXu
 * date : 2017/8/31 15:43
 * className : GreenDaoManager
 * version : 1.0
 * description : 数据库dao获取帮助类
 */
public class GreenDaoManager {

    private static GreenDaoManager instance;

    private DatabaseHelper mDevOpenHelper;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    public static GreenDaoManager init(Context context){
        if(instance == null)
            instance = new GreenDaoManager(context);
        return instance;
    }

    public static GreenDaoManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("GreenDaoManager is not initialized, call initDb(..) method first.");
        }
        return instance;
    }
    private GreenDaoManager(Context context){
        mDevOpenHelper = new DatabaseHelper(context, null, null);
        mDaoMaster = new DaoMaster(mDevOpenHelper.getWritableDb());
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoSession getSession(){
        return  mDaoSession;
    }

    /**
     * 获取数据库操作类
     * 此处需要手动添加
     * @param className
     */
    public Object getDao(Class className){
        if(className.getSimpleName().equals(WordDao.class.getSimpleName()))
            return mDaoSession.getWordDao();
        return null;
    }

    public void clearDb(){
//        mDevOpenHelper.getWritableDb().
    }

}
