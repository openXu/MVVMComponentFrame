package com.openxu.db.manager;

import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;


import com.openxu.core.utils.XLog;
import com.openxu.db.bean.Word;
import com.openxu.db.dao.DaoMaster;
import com.openxu.db.dao.WordDao;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.internal.DaoConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * autour : openXu
 * date : 2017/8/31 13:41
 * className : MigrationHelper
 * version : 1.0
 * description : 数据库迁移帮助类
 *               数据库升级时，先创建临时表，将老数据备份，然后删除所有老的表，然后创建新表，最后将临时表中的数据迁移到新表中
 *
 *               注意：此方式升级时，如果修改了某个bean中某个字段，数据库升级之后，此字段的数据将会丢失
 */
public class MigrationHelper {
    private static final String CONVERSION_CLASS_NOT_FOUND_EXCEPTION = "MIGRATION HELPER - CLASS DOESN'T MATCH WITH THE CURRENT PARAMETERS";
    private static MigrationHelper instance;

    public static MigrationHelper getInstance() {
        if(instance == null) {
            instance = new MigrationHelper();
        }
        return instance;
    }
    /**创建临时表->删除旧表->创建新表->导入数据*/
    public void migrate(Database db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        generateTempTables(db, daoClasses);
        DaoMaster.dropAllTables(db, true);
        DaoMaster.createAllTables(db, false);
        restoreData(db, daoClasses);
    }

    /**
     * 临时表生产
     * @param db
     * @param daoClasses
     */
    private void generateTempTables(Database db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        for(int i = 0; i < daoClasses.length; i++) {
            try{
                DaoConfig daoConfig = new DaoConfig(db, daoClasses[i]);
                String divider = "";
                String tableName = daoConfig.tablename;
                //数据库升级时，如果新增了表，此处创建临时表然后备份老数据会报异常（仅对新增表）
                if(!hasTable(db, tableName)) {
                    XLog.w("新添加的表"+tableName+",不用备份");
                    continue;
                }
                String tempTableName = daoConfig.tablename.concat("_TEMPTB");
                ArrayList<String> properties = new ArrayList<>();

                StringBuilder createTableStringBuilder = new StringBuilder();
                createTableStringBuilder.append("CREATE TABLE ").append(tempTableName).append(" (");

                for(int j = 0; j < daoConfig.properties.length; j++) {
                    String columnName = daoConfig.properties[j].columnName;

                    if(getColumns(db, tableName).contains(columnName)) {
                        properties.add(columnName);
                        String type = null;
                        try {
                            type = getTypeByClass(daoConfig.properties[j].type);
                        } catch (Exception exception) {
                            exception.printStackTrace();
//                            FLog.e(exception.getMessage());
                        }
                        createTableStringBuilder.append(divider).append(columnName).append(" ").append(type);
                        if(daoConfig.properties[j].primaryKey) {
                            createTableStringBuilder.append(" PRIMARY KEY");
                        }
                        divider = ",";
                    }
                }
                createTableStringBuilder.append(");");
                XLog.d("生成临时表："+createTableStringBuilder.toString());
                //CREATE TABLE PERSON_TEMP (_id INTEGER PRIMARY KEY);
                db.execSQL(createTableStringBuilder.toString());
                StringBuilder insertTableStringBuilder = new StringBuilder();

                insertTableStringBuilder.append("INSERT INTO ").append(tempTableName).append(" (");
                insertTableStringBuilder.append(TextUtils.join(",", properties));
                insertTableStringBuilder.append(") SELECT ");
                insertTableStringBuilder.append(TextUtils.join(",", properties));
                insertTableStringBuilder.append(" FROM ").append(tableName).append(";");
                XLog.d(tableNum(db, tableName)+"填充临时表："+insertTableStringBuilder.toString());
                db.execSQL(insertTableStringBuilder.toString());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private boolean hasTable(Database db, String tableName){
        //执行查询
        String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='"+tableName.trim()+"' ";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToNext()){
            int count = cursor.getInt(0);
            if(count>0){
                return true;
            }
        }
        return false;
    }
    private int tableNum(Database db, String tableName){
        String sql = "select count(*) as c from "+tableName.trim()+"";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToNext()){
            return cursor.getInt(0);
        }
        return 0;
    }
    private void restoreData(Database db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        for(int i = 0; i < daoClasses.length; i++) {
            try{
                //数据库升级时，如果新增了表，此处备份老数据会报异常（仅对新增表）
                DaoConfig daoConfig = new DaoConfig(db, daoClasses[i]);

                String tableName = daoConfig.tablename;
                String tempTableName = daoConfig.tablename.concat("_TEMPTB");
                if(!hasTable(db, tempTableName)) {
                    XLog.w("--新添加的表"+tableName+",不用备份");
                    continue;
                }
                ArrayList<String> properties = new ArrayList();

                for (int j = 0; j < daoConfig.properties.length; j++) {
                    String columnName = daoConfig.properties[j].columnName;

                    if(getColumns(db, tempTableName).contains(columnName)) {
                        properties.add(columnName);
                    }
                }

                StringBuilder insertTableStringBuilder = new StringBuilder();

                insertTableStringBuilder.append("INSERT INTO ").append(tableName).append(" (");
                insertTableStringBuilder.append(TextUtils.join(",", properties));
                insertTableStringBuilder.append(") SELECT ");
                insertTableStringBuilder.append(TextUtils.join(",", properties));
                insertTableStringBuilder.append(" FROM ").append(tempTableName).append(";");

                StringBuilder dropTableStringBuilder = new StringBuilder();

                dropTableStringBuilder.append("DROP TABLE ").append(tempTableName);
                XLog.d(tableNum(db, tempTableName)+"迁移老数据："+insertTableStringBuilder.toString());
//                FLog.d("删除临时表："+dropTableStringBuilder.toString());
                db.execSQL(insertTableStringBuilder.toString());
                db.execSQL(dropTableStringBuilder.toString());
                XLog.d(tableNum(db, tableName)+"完成后数据");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private String getTypeByClass(Class<?> type) throws Exception {
        if(type.equals(String.class)) {
            return "TEXT";
        }
        if(type.equals(Integer.class) || type.equals(int.class) || type.equals(Long.class) || type.equals(long.class)) {
            return "INTEGER";
        }
        if(type.equals(Boolean.class)) {
            return "BOOLEAN";
        }

        Exception exception = new Exception(CONVERSION_CLASS_NOT_FOUND_EXCEPTION.concat(" - Class: ").concat(type.toString()));
        exception.printStackTrace();
//        FLog.e(exception.getMessage());
        throw exception;
    }

    private static List<String> getColumns(Database db, String tableName) {
        List<String> columns = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName + " limit 1", null);
            if (cursor != null) {
                columns = new ArrayList<>(Arrays.asList(cursor.getColumnNames()));
            }
        } catch (Exception e) {
            Log.v(tableName, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return columns;
    }
}
