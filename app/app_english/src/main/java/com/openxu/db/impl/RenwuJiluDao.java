package com.openxu.db.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.openxu.db.DatabaseManager;
import com.openxu.db.annotation.Column;
import com.openxu.db.bean.RenwuJilu;
import com.openxu.ui.MyApplication;


public class RenwuJiluDao{

	/**
	 * 获取根据背单词日期组织并排序的记录集合
	 * @return
	 */
	public List<RenwuJilu> getDateGroup() {
		List<RenwuJilu> result = new ArrayList<RenwuJilu>();
		String table = MyApplication.property.tableName;
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		String sql = "SELECT COUNT(*) , date FROM "+ table +"  where date != \"\" and remenber = 1 GROUP BY date ORDER BY date ASC";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				RenwuJilu jilu = fillEntity(cursor);
				result.add(jilu);
			}
			return result;
		}
		cursor.close();
		DatabaseManager.getInstance().closeWordDatabase();
		return result;
	}

	private RenwuJilu fillEntity(Cursor cursor) {
		RenwuJilu jilu = new RenwuJilu();
		Field[] fields = jilu.getClass().getDeclaredFields();
		for (Field item : fields) {
			item.setAccessible(true);
			Column column = item.getAnnotation(Column.class);
			if (column != null) {
				// 得到m的属性对应表中那一列
				int columnIndex = cursor.getColumnIndex(column.value());
				String value = cursor.getString(columnIndex);
				try {
					if (item.getType() == int.class) {
						item.set(jilu, Integer.parseInt(value));
					} else if (item.getType() == long.class) {
						item.set(jilu, Long.parseLong(value));
					} else {
						item.set(jilu, value);
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}

			}
		}
		return jilu;
	}
	
	/**
	 * 获取最近日期被词记录用于复习
	 * @return
	 */
	public List<RenwuJilu> getFuxiWord() {
		List<RenwuJilu> result = new ArrayList<RenwuJilu>();
		String table = MyApplication.property.tableName;
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		String sql = "SELECT COUNT(*) , date FROM "+ table +"  where date != \"\" and remenber = 1 GROUP BY date ORDER BY date DESC limit 1";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				RenwuJilu jilu = fillEntity(cursor);
				result.add(jilu);
			}
			return result;
		}
		cursor.close();
		DatabaseManager.getInstance().closeWordDatabase();
		return result;
	}
	
	
}
