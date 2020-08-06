package com.openxu.db.base;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.openxu.db.DatabaseManager;
import com.openxu.db.annotation.Column;
import com.openxu.db.annotation.ID;
import com.openxu.db.bean.OpenWord;
import com.openxu.db.bean.WordBook;
import com.openxu.db.helper.WordDBHelper;
import com.openxu.ui.MyApplication;
import com.openxu.utils.MyUtil;

public class MyWordDaoSupport{
private String TAG = "MyWordDaoSupport";
	public MyWordDaoSupport() {
		super();
	}

	public String createTableName(){
		return "a"+System.currentTimeMillis();
	}
	/*************************单词本*******************************/
	//往单词本中添加一条
	public long insertBook(WordBook book) {
		String table = WordDBHelper.TABLE_MYWORD;
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		ContentValues values = new ContentValues();
		fillContentValues(book, values);
		long id = db.insert(table, null, values);
		DatabaseManager.getInstance().closeWordDatabase();
		return id;
	}
	//创建单词本后，为该单词本创建一个单词表
	public boolean createBook(String tablename){
		String table = WordDBHelper.TABLE_MYWORD;
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		db.execSQL("CREATE TABLE " + tablename + " (" 
	            + WordDBHelper.TABLE_ID+ " integer primary key autoincrement, " 
				+ WordDBHelper.TABLE_WORD_FIRST+ " TEXT, " 
				+ WordDBHelper.TABLE_WORD_EN+ " TEXT, " 
				+ WordDBHelper.TABLE_WORD_LEVEL+ " TEXT, " 
				+ WordDBHelper.TABLE_WORD_LOCAL+ " TEXT, " 
				+ WordDBHelper.TABLE_WORD_ERROR+ " TEXT, " 
				+ WordDBHelper.TABLE_WORD_REMENBER+ " TEXT, " 
				+ WordDBHelper.TABLE_WORD_REM_DATE+ " TEXT, " 
				+ WordDBHelper.TABLE_WORD_EXCHANGE+ " TEXT, " 
				+ WordDBHelper.TABLE_WORD_PH_EN+ " TEXT, " 
				+ WordDBHelper.TABLE_WORD_PH_EN3+ " TEXT, " 
				+ WordDBHelper.TABLE_WORD_PH_AM+ " TEXT, " 
				+ WordDBHelper.TABLE_WORD_PH_AM3+ " TEXT, " 
				+ WordDBHelper.TABLE_WORD_CHINA+ " TEXT, " 
				+ WordDBHelper.TABLE_WORD_SENTS+ " TEXT, " 
				+ WordDBHelper.TABLE_WORD_ADDDATE+ " TEXT, " 
				+ WordDBHelper.TABLE_WORD_FXDATE+ " TEXT, " 
				+ WordDBHelper.TABLE_WORD_FX+ " TEXT)") ;
		DatabaseManager.getInstance().closeWordDatabase();
		return true;
	}
	public int deleteBook(Serializable id) {
		String table = WordDBHelper.TABLE_MYWORD;
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		int row = db.delete(table, WordDBHelper.TABLE_ID + "=?",
				new String[] { id.toString() });
		DatabaseManager.getInstance().closeWordDatabase();
		return row;
	}
	public int updataBook(WordBook book) {
		String table = WordDBHelper.TABLE_MYWORD;
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		ContentValues values = new ContentValues();
		fillContentValues(book, values);
		int row = db.update(table, values, WordDBHelper.TABLE_ID + "=?", new String[] { book.get_id()+"" });
		DatabaseManager.getInstance().closeWordDatabase();
		return row;
	}
	public List<WordBook> findAllBook() {
		//查询单词本
		String table = WordDBHelper.TABLE_MYWORD;
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		List<WordBook> result;
		Cursor cursor = null;
		try {
			cursor = db.query(table, null, null, null, null, null, null);
			if (cursor != null) {
				result = new ArrayList<WordBook>();
				while (cursor.moveToNext()) {
					WordBook book = new WordBook();
					fillEntity(cursor, book);
					// 此处省略3*n行代码
					result.add(book);
				}
				//查询单词本中单词数量
				for(WordBook wordBook : result){
					try{
					Cursor cursor1 = db.rawQuery("select count(*) from " + wordBook.getWordTable(), null);
					cursor1.moveToNext();
					wordBook.num = cursor1.getInt(0);
					cursor1.close();
					}catch(Exception e){}
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

	public OpenWord searchWord(String en, List<WordBook> mybook) {
		OpenWord word = null;
		List<WordBook> books = findAllBook();
		if(books!=null&&books.size()>0){
			for(WordBook book : books){
				List<OpenWord> words = findByCondition(book.getWordTable(), WordDBHelper.TABLE_WORD_EN + "=?", new String[] {en}, null);
				if(words!=null&&words.size()>0){
					mybook.add(book);
					word = words.get(0);
					MyUtil.LOG_E(TAG, "我的单词本查询到："+word);
				}
			}
		}
		return word;
	}
	/********************************************************/
	public long insert(String table, OpenWord word) {
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		ContentValues values = new ContentValues();
		fillContentValues(word, values);
		long id = db.insert(table, null, values);
		DatabaseManager.getInstance().closeWordDatabase();
		return id;
	}
	public int delete(String table, Serializable id) {
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		int row = db.delete(table, WordDBHelper.TABLE_ID + "=?", new String[] { id.toString() });
		DatabaseManager.getInstance().closeWordDatabase();
		return row;
	}
	
	public int delete(String table, String english) {
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		int row = db.delete(table, WordDBHelper.TABLE_WORD_EN + "=?", new String[] { english });
		DatabaseManager.getInstance().closeWordDatabase();
		return row;
	}

	public void deleteAll(String table) {
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		db.execSQL("delete from " + table);
		DatabaseManager.getInstance().closeWordDatabase();
	}

	public int updata(String table, OpenWord word) {
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		ContentValues values = new ContentValues();
		fillContentValues(word, values);
		int row = db.update(table, values, WordDBHelper.TABLE_ID + "=?",
				new String[] { word.get_id()+"" });
		DatabaseManager.getInstance().closeWordDatabase();
		return row;
	}

	public List<OpenWord> findByCondition(String table, String selection, String[] selectionArgs,
			String orderBy) {
		return findByCondition(table, null, selection, selectionArgs, null, null,
				orderBy);
	}

	public List<OpenWord> findByCondition(String table, String[] columns, String selection,
			String[] selectionArgs, String orderBy) {
		return findByCondition(table, columns, selection, selectionArgs, null, null,
				orderBy);
	}

	public List<OpenWord> findByCondition(String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy) {
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		List<OpenWord> result;
		Cursor cursor = null;
		try {
			cursor = db.query(table, columns, selection, selectionArgs,groupBy, having, orderBy);
			if (cursor != null) {
				result = new ArrayList<OpenWord>();
				while (cursor.moveToNext()) {
					OpenWord word = new OpenWord();
					fillEntity(cursor, word);
					// 此处省略3*n行代码
					result.add(word);
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
	
	
	
	public int getTotalCount(String table) {
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		Cursor cursor = db.rawQuery("select count(*) from " + table, null);
		cursor.moveToNext();
		int count = cursor.getInt(0);
		cursor.close();
		DatabaseManager.getInstance().closeWordDatabase();
		return count;
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
	 */
	private void fillContentValues(WordBook word, ContentValues values) {
		Field[] fields = word.getClass().getDeclaredFields();
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
							String value = item.get(word).toString();// m.title
							values.put(key, value);
						}
					} else {
						String value = item.get(word) == null ? "" : item.get(word)
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
	private void fillContentValues(OpenWord book, ContentValues values) {
		Field[] fields = book.getClass().getDeclaredFields();
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
							String value = item.get(book).toString();// m.title
							values.put(key, value);
						}
					} else {
						String value = item.get(book) == null ? "" : item.get(book)
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
	protected void fillEntity(Cursor cursor, WordBook book) {
		// int columnIndex = cursor.getColumnIndex(DBHelper.TABLE_NEWS_TITLE);
		// String title = cursor.getString(columnIndex);
		// news.setTitle(title);

		Field[] fields = book.getClass().getDeclaredFields();
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
						item.set(book, Integer.parseInt(value));
					} else if (item.getType() == long.class) {
						item.set(book, Long.parseLong(value));
					} else {
						item.set(book, value);
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}

			}
		}

	}
	protected void fillEntity(Cursor cursor, OpenWord word) {
		// int columnIndex = cursor.getColumnIndex(DBHelper.TABLE_NEWS_TITLE);
		// String title = cursor.getString(columnIndex);
		// news.setTitle(title);

		Field[] fields = word.getClass().getDeclaredFields();
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
						item.set(word, Integer.parseInt(value));
					} else if (item.getType() == long.class) {
						item.set(word, Long.parseLong(value));
					} else {
						item.set(word, value);
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}

			}
		}

	}

}
