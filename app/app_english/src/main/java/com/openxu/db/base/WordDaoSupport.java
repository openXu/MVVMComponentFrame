package com.openxu.db.base;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.openxu.db.DatabaseManager;
import com.openxu.db.annotation.Column;
import com.openxu.db.annotation.ID;
import com.openxu.db.helper.WordDBHelper;
import com.openxu.ui.MyApplication;
import com.openxu.utils.Constant;
import com.openxu.utils.MyUtil;

/**
 * 数据库操作实现类的公共部分
 * 
 * @param <M>
 */
public abstract class WordDaoSupport<M> implements DAO<M> {
private String TAG = "DaoSupport";
	public WordDaoSupport() {
		super();
	}

	@Override
	public long insert(M m) {
		String table = MyApplication.property.tableName;
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		ContentValues values = new ContentValues();
		fillContentValues(m, values);
		long id = db.insert(table, null, values);
		DatabaseManager.getInstance().closeWordDatabase();
		return id;
	}

	@Override
	public void insertAll(List<M> list) {
		String table = MyApplication.property.tableName;
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		for (M m : list) {
			ContentValues values = new ContentValues();
			fillContentValues(m, values);
			long id = db.insert(table, null, values);
		}
		DatabaseManager.getInstance().closeWordDatabase();
	}

	@Override
	public int delete(Serializable id) {
		String table = MyApplication.property.tableName;
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		int row = db.delete(table, WordDBHelper.TABLE_ID + "=?",
				new String[] { id.toString() });
		DatabaseManager.getInstance().closeWordDatabase();
		return row;
	}
	@Override
	public void deleteAll() {
		String table = MyApplication.property.tableName;
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		db.execSQL("delete from " + table);
		DatabaseManager.getInstance().closeWordDatabase();
	}

	@Override
	public int updata(M m) {
		String table = MyApplication.property.tableName;
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		ContentValues values = new ContentValues();
		fillContentValues(m, values);
		int row = db.update(table, values, WordDBHelper.TABLE_ID + "=?",
				new String[] { getId(m) });
		DatabaseManager.getInstance().closeWordDatabase();
		return row;
	}

	@Override
	public int updataAll(List<M> ms) {
		int rows = 0;
		String table = MyApplication.property.tableName;
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		ContentValues values;
		for (M m : ms) {
			values = new ContentValues();
			fillContentValues(m, values);
			rows += db.update(table, values, WordDBHelper.TABLE_ID + "=?",
					new String[] { getId(m) });
		}
		DatabaseManager.getInstance().closeWordDatabase();
		return rows;
	}
	@Override
	public List<M> findByCondition(String selection, String[] selectionArgs,
			String orderBy) {
		return findByCondition(null, selection, selectionArgs, null, null,
				orderBy);
	}

	public List<M> findByCondition(String[] columns, String selection,
			String[] selectionArgs, String orderBy) {
		return findByCondition(columns, selection, selectionArgs, null, null,
				orderBy);
	}

	public List<M> findByCondition(String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy) {
		String table = MyApplication.property.tableName;
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		List<M> result;
		Cursor cursor = null;
		try {
			cursor = db.query(table, columns, selection, selectionArgs,groupBy, having, orderBy);
			if (cursor != null) {
				result = new ArrayList<M>();
				while (cursor.moveToNext()) {
					M m = getInstance();
					fillEntity(cursor, m);
					// 此处省略3*n行代码
					result.add(m);
				}
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
			DatabaseManager.getInstance().closeWordDatabase();
		}
		return null;

	}
	
	/**
	 * 分页查找
	 * @return
	 */
	public List<M> getPartWords(int start, int num) {
		String table = MyApplication.property.tableName;
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		//select * from db where limit 0,5
		String sql = "select "+ 
				WordDBHelper.TABLE_ID+" , "+
				WordDBHelper.TABLE_WORD_FIRST+" , "+
				WordDBHelper.TABLE_WORD_EN+" , "+
				WordDBHelper.TABLE_WORD_LOCAL+" , "+
				WordDBHelper.TABLE_WORD_REMENBER+" , "+
				WordDBHelper.TABLE_WORD_CHINA+
				" from " + table +" limit "+num+" offset "+start;
		MyUtil.LOG_I(TAG, sql);
		Cursor cursor = db.rawQuery(sql, null);
		List<M> result = new ArrayList<M>();
		try {
			if (cursor != null) {
				while (cursor.moveToNext()) {
					M m = getInstance();
					fillEntity(cursor, m);
					// 此处省略3*n行代码
					result.add(m);
				}
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
			DatabaseManager.getInstance().closeWordDatabase();
		}
		return null;
	}
	
	@Override
	public List<M> findSort() {
		String table = MyApplication.property.tableName;
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		String sql = "select "+ 
				WordDBHelper.TABLE_ID+" , "+
				WordDBHelper.TABLE_WORD_FIRST+" , "+
				WordDBHelper.TABLE_WORD_EN+" , "+
				WordDBHelper.TABLE_WORD_LOCAL+" , "+
				WordDBHelper.TABLE_WORD_REMENBER+" , "+
				WordDBHelper.TABLE_WORD_CHINA+
				" from " + table ;
		MyUtil.LOG_I(TAG, sql);
		Cursor cursor = db.rawQuery(sql, null);
		List<M> result = new ArrayList<M>();
		try {
			if (cursor != null) {
				while (cursor.moveToNext()) {
					M m = getInstance();
					fillEntity(cursor, m);
					// 此处省略3*n行代码
					result.add(m);
				}
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
			DatabaseManager.getInstance().closeWordDatabase();
		}
		return null;
	}
	
	/**
	 * 不带table参数的getTotalCount是在选中某词库时，调用
	 */
	@Override
	public int getTotalCount() {
		String table = MyApplication.property.tableName;
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		Cursor cursor = db.rawQuery("select count(*) from " + table, null);
		cursor.moveToNext();
		int count = cursor.getInt(0);
		cursor.close();
		DatabaseManager.getInstance().closeWordDatabase();
		return count;
	}
	@Override
	public int getTotalCount(int remenber) {
		String table = MyApplication.property.tableName;
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		Cursor cursor = db.rawQuery("select count(*) from " + table + " where "
				+ WordDBHelper.TABLE_WORD_REMENBER + " =" + remenber, null);
		cursor.moveToNext();
		int count = cursor.getInt(0);
		cursor.close();
		DatabaseManager.getInstance().closeWordDatabase();
		return count;
	}
	public int getTotalCount(String table, int remenber) {
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		Cursor cursor = db.rawQuery("select count(*) from " + table + " where "
				+ WordDBHelper.TABLE_WORD_REMENBER + " =" + remenber, null);
		cursor.moveToNext();
		int count = cursor.getInt(0);
		cursor.close();
		DatabaseManager.getInstance().closeWordDatabase();
		return count;
	}
	public int getTotalCount(String table) {
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		Cursor cursor = db.rawQuery("select count(*) from " + table, null);
		cursor.moveToNext();
		int count = cursor.getInt(0);
		cursor.close();
		DatabaseManager.getInstance().closeWordDatabase();
		return count;
	}

	@Override
	public List<M> findShunxu(int num) {
		String table = MyApplication.property.tableName;
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		Cursor cursor = null;
		Cursor cursorAdd = null;
		// 比较日期，如果记录日期和今天不同，说明是新的一天，相同时直接获取今天的单词
		boolean isoldDate = MyApplication.property.isOldData();
		if (isoldDate) {
			cursor = db.rawQuery("select * from " + table + " where "
					+ WordDBHelper.TABLE_WORD_REM_DATE + " = ? ",
					new String[] { MyUtil.date2Str(new Date(), Constant.DATE_DB) });
			//如果查询历史少于需要的数量，可能是提高了单词量，需要补齐
			if(cursor.getCount()<num)
				cursorAdd = db.rawQuery("select * from " + table + " where "
						+ WordDBHelper.TABLE_WORD_REMENBER + " = ? limit ?",
						new String[] { String.valueOf(0), String.valueOf(num-cursor.getCount()) });
			else if(cursor.getCount()>num){
				// 隔了一天后，把以前出现过的且没有标记为以背的单词日期置为空
				db.execSQL("update " + table + " set "
						+ WordDBHelper.TABLE_WORD_REM_DATE + " = \"\" " + " where "
						+ WordDBHelper.TABLE_WORD_REM_DATE + " != \"\" " + " and "
						+ WordDBHelper.TABLE_WORD_REMENBER + " = 0");
				cursor = db.rawQuery("select * from " + table + " where "
						+ WordDBHelper.TABLE_WORD_REM_DATE + " = ? ",
						new String[] { MyUtil.date2Str(new Date(), Constant.DATE_DB) });
				MyUtil.LOG_V(TAG, "将以前的未记单词时间置为空后，还有"+cursor.getCount()+"条,这些都是今天记住的");
				if(cursor.getCount()>=num){
					
				}else{
					cursorAdd = db.rawQuery("select * from " + table + " where "
							+ WordDBHelper.TABLE_WORD_REMENBER + " = ? limit ?",
							new String[] { String.valueOf(0), String.valueOf(num-cursor.getCount()) });
					MyUtil.LOG_V(TAG, "再从数据库重新查询"+cursorAdd.getCount()+"条");
				}
				//记录新的日期
				MyApplication.property.update_oldDate();
			}
		} else {
			// 隔了一天后，把以前出现过的且没有标记为以背的单词日期置为空
			db.execSQL("update " + table + " set "
					+ WordDBHelper.TABLE_WORD_REM_DATE + " = \"\" " + " where "
					+ WordDBHelper.TABLE_WORD_REM_DATE + " != \"\" " + " and "
					+ WordDBHelper.TABLE_WORD_REMENBER + " = 0");
			// 获取新的随机单词任务
			cursor = db.rawQuery("select * from " + table + " where "
					+ WordDBHelper.TABLE_WORD_REMENBER + " = ? limit ?",
					new String[] { String.valueOf(0), String.valueOf(num) });
			//记录新的日期
			MyApplication.property.update_oldDate();
		}
		List<M> result = new ArrayList<M>();
		try {
			if (cursor != null) {
				while (cursor.moveToNext()) {
					M m = getInstance();
					fillEntity(cursor, m);
					// 此处省略3*n行代码
					result.add(m);
				}
			}
			if (cursorAdd != null) {
				while (cursorAdd.moveToNext()) {
					M m = getInstance();
					fillEntity(cursorAdd, m);
					// 此处省略3*n行代码
					result.add(m);
				}
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
			if (cursorAdd != null)
				cursorAdd.close();
			DatabaseManager.getInstance().closeWordDatabase();
		}
		return null;
	}
	//随机查询单词任务
	@Override
	public List<M> findRandom(int num) {
		String table = MyApplication.property.tableName;
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		Cursor cursor = null;
		Cursor cursorAdd = null;
		// 比较日期，如果记录日期和今天不同，说明是新的一天，相同时直接获取今天的单词
		boolean isoldDate = MyApplication.property.isOldData();
		if (isoldDate) {
			cursor = db.rawQuery("select * from " + table + " where "
					+ WordDBHelper.TABLE_WORD_REM_DATE + " = ? ",
					new String[] { MyUtil.date2Str(new Date(), Constant.DATE_DB) });
			//如果查询历史少于需要的数量，可能是提高了单词量，需要补齐
			if(cursor.getCount()<num){
				MyUtil.LOG_V(TAG, "查询历史单词"+cursor.getCount()+"条,总共需要"+num+"条");
				cursorAdd = db.rawQuery("select * from " + table + " where "
						+ WordDBHelper.TABLE_WORD_REMENBER + " = ? order by RANDOM() limit ?",
						new String[] { String.valueOf(0), String.valueOf(num-cursor.getCount()) });
				MyUtil.LOG_V(TAG, "再从数据库重新查询"+cursorAdd.getCount()+"条");
			} else if(cursor.getCount()>num){
				MyUtil.LOG_V(TAG, "查询历史单词"+cursor.getCount()+"条,只需要"+num+"条");
				//可能减少了单词量
				db.execSQL("update " + table + " set "
						+ WordDBHelper.TABLE_WORD_REM_DATE + " = \"\" " + " where "
						+ WordDBHelper.TABLE_WORD_REM_DATE + " != \"\" " + " and "
						+ WordDBHelper.TABLE_WORD_REMENBER + " = 0");
				cursor = db.rawQuery("select * from " + table + " where "
						+ WordDBHelper.TABLE_WORD_REM_DATE + " = ? ",
						new String[] { MyUtil.date2Str(new Date(), Constant.DATE_DB) });
				MyUtil.LOG_V(TAG, "将以前的未记单词时间置为空后，还有"+cursor.getCount()+"条,这些都是今天记住的");
				if(cursor.getCount()>=num){
					
				}else{
					cursorAdd = db.rawQuery("select * from " + table + " where "
							+ WordDBHelper.TABLE_WORD_REMENBER + " = ? order by RANDOM() limit ?",
							new String[] { String.valueOf(0), String.valueOf(num-cursor.getCount()) });
					MyUtil.LOG_V(TAG, "再从数据库重新查询"+cursorAdd.getCount()+"条");
				}
				//记录新的日期
				MyApplication.property.update_oldDate();
			}
		} else {
			// 隔了一天后，把以前出现过的且没有标记为以背的单词日期置为空
			db.execSQL("update " + table + " set "
					+ WordDBHelper.TABLE_WORD_REM_DATE + " = \"\" " + " where "
					+ WordDBHelper.TABLE_WORD_REM_DATE + " != \"\" " + " and "
					+ WordDBHelper.TABLE_WORD_REMENBER + " = 0");
			// 获取新的随机单词任务
			cursor = db.rawQuery("select * from " + table + " where "
					+ WordDBHelper.TABLE_WORD_REMENBER + " = ? order by RANDOM() limit ?",
					new String[] { String.valueOf(0), String.valueOf(num) });
			//记录新的日期
			MyApplication.property.update_oldDate();
		}
		List<M> result = new ArrayList<M>();
		try {
			if (cursor != null) {
				while (cursor.moveToNext()) {
					M m = getInstance();
					fillEntity(cursor, m);
					// 此处省略3*n行代码
					result.add(m);
				}
			}
			if (cursorAdd != null) {
				while (cursorAdd.moveToNext()) {
					M m = getInstance();
					fillEntity(cursorAdd, m);
					// 此处省略3*n行代码
					result.add(m);
				}
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
			if (cursorAdd != null)
				cursorAdd.close();
			DatabaseManager.getInstance().closeWordDatabase();
		}
		return null;
	}
	
	//随机查询复习任务
	@Override
	public List<M> findFxRandom(int num) {
		String table = MyApplication.property.tableName;
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		Cursor cursor = null;
		Cursor cursorAdd = null;
		// 比较日期，如果记录日期和今天不同，说明是新的一天，相同时直接获取今天的单词
		boolean isoldDate = MyApplication.property.isOldFxData();
		if (isoldDate) {
			cursor = db.rawQuery("select * from " + table + " where "+ WordDBHelper.TABLE_WORD_FXDATE + " = ? ",
					new String[] { MyUtil.date2Str(new Date(), Constant.DATE_DB) });
			//如果查询历史少于需要的数量，可能是提高了单词量，需要补齐
			if(cursor.getCount()<num){
				MyUtil.LOG_V(TAG, "查询历史单词"+cursor.getCount()+"条,总共需要"+num+"条");
				cursorAdd = db.rawQuery("select * from " + table + " where "
						+ WordDBHelper.TABLE_WORD_REMENBER + " = ? and "
						+ WordDBHelper.TABLE_WORD_FX + " = ? and "
						+ WordDBHelper.TABLE_WORD_FXDATE+" = \"\" order by RANDOM() limit ?",
						new String[] { String.valueOf(1),String.valueOf(0), String.valueOf(num-cursor.getCount()) });
				MyUtil.LOG_V(TAG, "再从数据库重新查询"+cursorAdd.getCount()+"条");
			} else if(cursor.getCount()>num){
				MyUtil.LOG_V(TAG, "查询历史单词"+cursor.getCount()+"条,只需要"+num+"条");
				//可能减少了单词量
				db.execSQL("update " + table + " set "
						+ WordDBHelper.TABLE_WORD_FXDATE + " = \"\" " + " where "
						+ WordDBHelper.TABLE_WORD_FXDATE + " != \"\" " + " and "
						+ WordDBHelper.TABLE_WORD_FX + " = 0");
				cursor = db.rawQuery("select * from " + table + " where "
						+ WordDBHelper.TABLE_WORD_FXDATE + " = ? ",
						new String[] { MyUtil.date2Str(new Date(), Constant.DATE_DB) });
				MyUtil.LOG_V(TAG, "将以前的未记单词时间置为空后，还有"+cursor.getCount()+"条,这些都是今天已经复习了的");
				if(cursor.getCount()>=num){
					
				}else{
					cursorAdd = db.rawQuery("select * from " + table + " where "
							+ WordDBHelper.TABLE_WORD_REMENBER + " = ? and "
							+ WordDBHelper.TABLE_WORD_FX + " = ? order by RANDOM() limit ?",
							new String[] { String.valueOf(1),String.valueOf(0), String.valueOf(num-cursor.getCount()) });
					MyUtil.LOG_V(TAG, "再从数据库重新查询"+cursorAdd.getCount()+"条");
				}
				//记录新的日期
				MyApplication.property.update_oldDate();
			}
		} else {
			// 隔了一天后，把以前出现过的且没有标记为以背的单词日期置为空
			db.execSQL("update " + table + " set "
					+ WordDBHelper.TABLE_WORD_FXDATE + " = \"\" " + " where "
					+ WordDBHelper.TABLE_WORD_FXDATE + " != \"\" " + " and "
					+ WordDBHelper.TABLE_WORD_FX + " = 0");
			// 获取新的随机单词任务
			cursor = db.rawQuery("select * from " + table + " where "
					+ WordDBHelper.TABLE_WORD_REMENBER + " = ? and "
					+ WordDBHelper.TABLE_WORD_FX + " = ? order by RANDOM() limit ?",
					new String[] {String.valueOf(1), String.valueOf(0), String.valueOf(num) });
			//记录新的日期
			MyApplication.property.update_oldfxDate();
		}
		List<M> result = new ArrayList<M>();
		try {
			if (cursor != null) {
				while (cursor.moveToNext()) {
					M m = getInstance();
					fillEntity(cursor, m);
					// 此处省略3*n行代码
					result.add(m);
				}
			}
			if (cursorAdd != null) {
				while (cursorAdd.moveToNext()) {
					M m = getInstance();
					fillEntity(cursorAdd, m);
					// 此处省略3*n行代码
					result.add(m);
				}
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
			if (cursorAdd != null)
				cursorAdd.close();
			DatabaseManager.getInstance().closeWordDatabase();
		}
		return null;
	}

	//单词练习，搜索3个错误答案
	@Override
	public List<M> findTestRandom(int id) {
		String table = MyApplication.property.tableName;
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		Cursor cursor = db.rawQuery("select * from " + table + " where "+WordDBHelper.TABLE_ID + " != ? "+" order by RANDOM() limit ?",
							new String[] {String.valueOf(id) , String.valueOf(3) });
		List<M> result = new ArrayList<M>();
		try {
			if (cursor != null) {
				while (cursor.moveToNext()) {
					M m = getInstance();
					fillEntity(cursor, m);
					// 此处省略3*n行代码
					result.add(m);
				}
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
			DatabaseManager.getInstance().closeWordDatabase();
		}
		return null;
	}
	
	public List<M> findByCondition(String table, String selection,String[] selectionArgs) {
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		List<M> result;
		Cursor cursor = null;
		try {
			cursor = db.query(table, null, selection, selectionArgs,null, null, null);
			if (cursor != null) {
				result = new ArrayList<M>();
				while (cursor.moveToNext()) {
					M m = getInstance();
					fillEntity(cursor, m);
					// 此处省略3*n行代码
					result.add(m);
				}
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
			DatabaseManager.getInstance().closeWordDatabase();
		}
		return null;

	}
	
	/**
	 * 从本地单词中查找
	 */
	@Override
	public List<M> searchWords(String en, int num) {
		int tempNum = 0;
		List<M> listg = null;
		List<M> list4 = null;
		List<M> list6 = null;
		List<M> list8 = null;
		//数量不够从其他词库中查询
		List<M> result = new ArrayList<M>();
		listg = findByCondition(WordDBHelper.TABLE_WORDg, WordDBHelper.TABLE_WORD_EN + " LIKE '"+en+"%' LIMIT ?", new String[]{(num-tempNum)+""});
		if(listg!=null){
			System.out.println("从"+WordDBHelper.TABLE_WORDg+"查询到："+listg.size());
			result.addAll(listg);
			tempNum += listg.size();
			if(tempNum>=num){
				return result;
			}
		}
		
		list4 = findByCondition(WordDBHelper.TABLE_WORD4, WordDBHelper.TABLE_WORD_EN + " LIKE '"+en+"%' LIMIT ?", new String[]{(num-tempNum)+""});
		if(list4!=null){
			System.out.println("从"+WordDBHelper.TABLE_WORD4+"查询到："+list4.size());
			tempNum += list4.size();
			result.addAll(list4);
			if(tempNum>=num){
				return result;
			}
		}
	
		list6 = findByCondition(WordDBHelper.TABLE_WORD6, WordDBHelper.TABLE_WORD_EN + " LIKE '"+en+"%' LIMIT ?", new String[]{(num-tempNum)+""});
		if(list6!=null){
			System.out.println("从"+WordDBHelper.TABLE_WORD6+"查询到："+list6.size());
			tempNum += list6.size();
			result.addAll(list6);
			if(tempNum>=num){
				return result;
			}
		}
	
		list8 = findByCondition(WordDBHelper.TABLE_WORD8, WordDBHelper.TABLE_WORD_EN + " LIKE '"+en+"%' LIMIT ?", new String[]{(num-tempNum)+""});
		if(list8!=null){
			System.out.println("从"+WordDBHelper.TABLE_WORD8+"查询到："+list8.size());
			tempNum += list8.size();
			result.addAll(list8);
			if(tempNum>=num){
				return result;
			}
		}
	
		return result;
	}
	
	/**
	 * 
	 */
	public M searchWord(String en) {
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		Cursor cursor = db.rawQuery("select * from " + WordDBHelper.TABLE_WORDg + 
				" where "+ WordDBHelper.TABLE_WORD_EN + " = ?",	new String[] {en});
//		MyUtil.LOG_E(TAG, "高中查询到："+cursor.getCount());
		if(cursor.getCount()>0){
			cursor.moveToNext();
			M m = getInstance();
			fillEntity(cursor, m);
			return m;
		}

		cursor = db.rawQuery("select * from " + WordDBHelper.TABLE_WORD4 + 
				" where "+ WordDBHelper.TABLE_WORD_EN + " = ?",	new String[] {en});
//		MyUtil.LOG_E(TAG, "4级查询到："+cursor.getCount());
		if(cursor.getCount()>0){
			cursor.moveToNext();
			M m = getInstance();
			fillEntity(cursor, m);
			return m;
		}
	

		cursor = db.rawQuery("select * from " + WordDBHelper.TABLE_WORD6 + 
				" where "+ WordDBHelper.TABLE_WORD_EN + " = ?",	new String[] {en});
//		MyUtil.LOG_E(TAG, "6级查询到："+cursor.getCount());
		if(cursor.getCount()>0){
			cursor.moveToNext();
			M m = getInstance();
			fillEntity(cursor, m);
			return m;
		}

		cursor = db.rawQuery("select * from " + WordDBHelper.TABLE_WORD8 + 
				" where "+ WordDBHelper.TABLE_WORD_EN + " = ?",	new String[] {en});
//		MyUtil.LOG_E(TAG, "8级查询到："+cursor.getCount());
		if(cursor.getCount()>0){
			cursor.moveToNext();
			M m = getInstance();
			fillEntity(cursor, m);
			return m;
		}
		return null;
	}
	
	public void deleteTB(String name) {
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		db.execSQL("DROP TABLE " + name);
		DatabaseManager.getInstance().closeWordDatabase();
	}

	/**
	 * 分页查找
	 */
	/*
	 * @Override public List<M> findLimit(int index) { SQLiteDatabase db =
	 * helper.getReadableDatabase(); List<M> result; Cursor cursor = null; try {
	 * cursor = db.rawQuery("select * from "+table+" limit 20 offset ?", new
	 * String[]{String.valueOf(index)}); if (cursor != null) { result = new
	 * ArrayList<M>(); while (cursor.moveToNext()) { M m = getInstance();
	 * fillEntity(cursor, m); // 此处省略3*n行代码 result.add(m); } return result; } }
	 * catch (Exception e) { e.printStackTrace(); } finally { if (cursor !=
	 * null) cursor.close(); db.close(); } return null; }
	 */
	@Override
	public List<M> findAll() {
		String table = MyApplication.property.tableName;
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		List<M> result;
		Cursor cursor = null;
		try {
			cursor = db.query(table, null, null, null, null, null, null);
			if (cursor != null) {
				result = new ArrayList<M>();
				while (cursor.moveToNext()) {
					M m = getInstance();
					fillEntity(cursor, m);
					// 此处省略3*n行代码
					result.add(m);
				}
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
			DatabaseManager.getInstance().closeWordDatabase();
		}
		return null;
	}

	/******************************************************/
	// 通用化的问题清单
	// 问题一：表名获取
	// 问题二：需要将数据源News添加到目标ContentValues身上
	// 问题三：将数据源Cursor数据添加目标News身上
	// 问题四：News主键是谁
	// 问题五：当前操作的实体创建

	/**
	 * 问题二：需要将数据源News添加到目标ContentValues身上
	 * 
	 * @param m
	 *            :数据源
	 * @param values
	 *            :目标
	 */
	private void fillContentValues(M m, ContentValues values) {
		// values.put(DBHelper.TABLE_NEWS_TITLE, news.getTitle());

		// 表中列与实体中字段一一对应关系
		// for all public fields
		// m.getClass().getFields();
		// objects for all fields
		Field[] fields = m.getClass().getDeclaredFields();
		for (Field item : fields) {
			// 如果是私有的字段
			item.setAccessible(true);

			Column column = item.getAnnotation(Column.class);
			if (column != null) {
				String key = column.value();
				try {
					// sqlite 存储 字符串 特殊：主键+自增
					ID id = item.getAnnotation(ID.class);
					if (id != null) {
						if (id.autoincrement()) {
							// 主键+自增 的情况不能设置数据
						} else {
							String value = item.get(m).toString();// m.title
							values.put(key, value);
						}
					} else {
						String value = item.get(m) == null ? "" : item.get(m)
								.toString();// m.title
						values.put(key, value);
					}

				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * 问题三：将数据源Cursor数据添加目标News身上
	 * 
	 * @param cursor
	 * @param m
	 */
	protected void fillEntity(Cursor cursor, M m) {
		// int columnIndex = cursor.getColumnIndex(DBHelper.TABLE_NEWS_TITLE);
		// String title = cursor.getString(columnIndex);
		// news.setTitle(title);

		Field[] fields = m.getClass().getDeclaredFields();
		for (Field item : fields) {
			item.setAccessible(true);
			Column column = item.getAnnotation(Column.class);
			if (column != null) {
				// 得到m的属性对应表中那一列
				int columnIndex = cursor.getColumnIndex(column.value());
				if(columnIndex<0)
					continue;
				// 主键+自增 int long
				String value = cursor.getString(columnIndex);
				try {
					if (item.getType() == int.class) {
						item.set(m, Integer.parseInt(value));
					} else if (item.getType() == long.class) {
						item.set(m, Long.parseLong(value));
					} else {
						item.set(m, value);
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}

			}
		}

	}

	/**
	 * 问题四：News主键是谁
	 * 
	 * @param m
	 * @return
	 */
	private String getId(M m) {
		// 主键
		// 获取所有的Field，头上有ID注解，循环
		Field[] fields = m.getClass().getDeclaredFields();
		for (Field item : fields) {
			item.setAccessible(true);
			ID id = item.getAnnotation(ID.class);
			if (id != null) {
				try {
					return item.get(m).toString();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return "";
	}

	/**
	 * 问题五：当前操作的实体创建
	 * 
	 * @return
	 */
	protected M getInstance() {
		// ①当前正在运行的孩子是谁
		// DaoSupport 子类操作
		// Object.getClass() 返回此 Object 的"运行时"类。
		// String name = super.getClass().getName();
		//
		// Log.i(TAG, name);

		// ②获取父类：getSuperClass
		// 支持泛型的父类
		// getClass().getSuperclass();//class com.senselock.dao.base.DaoSupport
		Type superclass = getClass().getGenericSuperclass();// com.senselock.dao.base.DaoSupport<com.senselock.dao.domain.News>

		// JDK 让泛型实现一个接口：参数化的类型
		if (superclass instanceof ParameterizedType) {
			// ③获取到泛型中得参数
			Type[] arguments = ((ParameterizedType) superclass)
					.getActualTypeArguments();// 泛型的公共操作 [class
												// com.senselock.dao.domain.News]
			Class target = (Class) arguments[0];
			try {
				// ④创建实体的对象
				return (M) target.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return null;
	}
}
