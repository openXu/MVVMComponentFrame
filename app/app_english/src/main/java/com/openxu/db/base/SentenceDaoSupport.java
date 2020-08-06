package com.openxu.db.base;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.openxu.db.DatabaseManager;
import com.openxu.db.annotation.Column;
import com.openxu.db.annotation.ID;
import com.openxu.db.annotation.TableName;
import com.openxu.db.helper.WordDBHelper;
import com.openxu.utils.MyUtil;

/**
 * 数据库操作实现类的公共部分
 * @param <M>
 */
public abstract class SentenceDaoSupport<M> implements DAO<M> {
	private String TAG = "SentenceDaoSupport";

	public SentenceDaoSupport() {
		super();
	}

	@Override
	public long insert(M m) {
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		ContentValues values = new ContentValues();
		fillContentValues(m, values);
		long id = db.insert(getTableName(), null, values);
		DatabaseManager.getInstance().closeWordDatabase();
		return id;
	}

	@Override
	public void insertAll(List<M> list) {
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		for (M m : list) {
			ContentValues values = new ContentValues();
			fillContentValues(m, values);
			long id = db.insert(getTableName(), null, values);
		}
		DatabaseManager.getInstance().closeWordDatabase();
	}

	@Override
	public int delete(Serializable id) {
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		int row = db.delete(getTableName(), WordDBHelper.TABLE_ID + "=?",
				new String[] { id.toString() });
		DatabaseManager.getInstance().closeWordDatabase();
		return row;
	}

	public void deleteAll() {
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		db.execSQL("delete from " + getTableName());
		DatabaseManager.getInstance().closeWordDatabase();
	}

	@Override
	public int updata(M m) {
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		ContentValues values = new ContentValues();
		fillContentValues(m, values);
		int row = db.update(getTableName(), values, WordDBHelper.TABLE_ID + "=?",
				new String[] { getId(m) });
		DatabaseManager.getInstance().closeWordDatabase();
		return row;
	}

	@Override
	public int updataAll(List<M> ms) {
		int rows = 0;
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		ContentValues values;
		for (M m : ms) {
			values = new ContentValues();
			fillContentValues(m, values);
			rows += db.update(getTableName(), values, WordDBHelper.TABLE_ID + "=?",
					new String[] { getId(m) });
		}
		DatabaseManager.getInstance().closeWordDatabase();
		return rows;
	}

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
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		List<M> result;
		Cursor cursor = null;
		try {
			cursor = db.query(getTableName(), columns, selection, selectionArgs,
					groupBy, having, orderBy);
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
	 * ②、显示全部：下拉分页
	 * @param showNum
	 * @param findNum 
	 * @param hasMore 是否还有更多消息
	 * @return
	 */
	public List<M> findAllPart(int showNum, int findNum, StringBuffer hasMore, boolean isLoadMore) {
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		//①、总共多少条
		Cursor cursor = db.rawQuery("select count(*) from " + getTableName(), null);
		cursor.moveToNext();
		int count = cursor.getInt(0);
		cursor.close();
		int startIndex;
		if((showNum+findNum) >= count){
			if(isLoadMore)
				findNum = count - showNum;
			else
				findNum = count;
			startIndex = 0;
			hasMore.append(false);
		}else{
			startIndex = count - showNum - findNum;
			if(!isLoadMore)
				findNum = showNum+findNum;
			hasMore.append(true);
		}
		MyUtil.LOG_W(TAG, "总共"+count +" 条消息， 从"+startIndex+" 开始查询，查询"+findNum+"条");
		//②、从哪里开始查询，查询多少条
	    cursor = db.rawQuery("select * from " + getTableName()+" limit ? offset ?",
				new String[] {String.valueOf(findNum), String.valueOf(startIndex) });
		try {
			List<M> result;
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
			DatabaseManager.getInstance().closeWordDatabase();;
		}
		return null;
	}

	/**
	 * 获取数据库一共有多少条记录
	 * @return int 总条目个数
	 */
	public int getTotalCount() {
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		Cursor cursor = db.rawQuery("select count(*) from " + getTableName(), null);
		cursor.moveToNext();
		int count = cursor.getInt(0);
		cursor.close();
		DatabaseManager.getInstance().closeWordDatabase();
		return count;
	}

	@Override
	public List<M> findAll() {
		SQLiteDatabase db = DatabaseManager.getInstance().openWordDatabase();
		List<M> result;
		Cursor cursor = null;
		try {
			cursor = db.query(getTableName(), null, null, null, null, null, null);
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
	 * 问题一：表名获取
	 * 
	 * @return
	 */
	protected String getTableName() {
		// M--News
		// 实体与数据库中表一一对应
		// 方案一：获取实体的简单名称“News”，缺少灵活性
		// 方案二：利用注解，指定实体对应的数据库表

		// 步骤：
		// ①获取到实体
		M m = getInstance();
		// ②获取到类上的注解
		TableName tableName = m.getClass().getAnnotation(TableName.class);
		// ③获取到的注解中存放的数据
		if (tableName != null) {
			return tableName.value();
		}
		return "";
	}
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
