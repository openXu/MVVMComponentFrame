package com.openxu.db.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.openxu.db.DatabaseManager;
import com.openxu.db.bean.User;
import com.openxu.db.helper.UserDBHelper;

public class UserDaoImpl {

	private String TAG = "UserDaoImpl";

	public UserDaoImpl() {
		super();
	}

	public long insert(User user) {
		SQLiteDatabase db = DatabaseManager.getInstance().openUserDatabase();
		ContentValues values = new ContentValues();
		values.put(UserDBHelper.TABLE_USER_OBJID, user.getObjectId());
		values.put(UserDBHelper.TABLE_USER_NAME, user.getUsername());
		values.put(UserDBHelper.TABLE_USER_PASW, user.getPs());
		values.put(UserDBHelper.TABLE_USER_CHATNAME, user.getChatName());
		values.put(UserDBHelper.TABLE_USER_CHATPASW, user.getChatPsw());
		values.put(UserDBHelper.TABLE_USER_POINT, user.getPoint());
		values.put(UserDBHelper.TABLE_USER_JINGYAN, user.getJingyan());
		values.put(UserDBHelper.TABLE_USER_MAIL, user.getEmail());
		values.put(UserDBHelper.TABLE_USER_PHONE, user.getMobilePhoneNumber());
		values.put(UserDBHelper.TABLE_USER_ICON, user.getIcon());
		values.put(UserDBHelper.TABLE_USER_X1, user.getSex());
		values.put(UserDBHelper.TABLE_USER_X2, user.getSexset());
		long id = db.insert(UserDBHelper.TABLE_USER, null, values);
		DatabaseManager.getInstance().closeUserDatabase();
		user.setId((int)id);
		return id;
	}

	public void deleteAll() {
		SQLiteDatabase db = DatabaseManager.getInstance().openUserDatabase();
		db.execSQL("delete from "+UserDBHelper.TABLE_USER);
		DatabaseManager.getInstance().closeUserDatabase();
	}
	public int updata(User user) {
		SQLiteDatabase db = DatabaseManager.getInstance().openUserDatabase();
		ContentValues values = new ContentValues();
		values.put(UserDBHelper.TABLE_USER_OBJID, user.getObjectId());
		values.put(UserDBHelper.TABLE_USER_NAME, user.getUsername());
		values.put(UserDBHelper.TABLE_USER_PASW, user.getPs());
		values.put(UserDBHelper.TABLE_USER_CHATNAME, user.getChatName());
		values.put(UserDBHelper.TABLE_USER_CHATPASW, user.getChatPsw());
		values.put(UserDBHelper.TABLE_USER_POINT, user.getPoint());
		values.put(UserDBHelper.TABLE_USER_JINGYAN, user.getJingyan());
		values.put(UserDBHelper.TABLE_USER_MAIL, user.getEmail());
		values.put(UserDBHelper.TABLE_USER_PHONE, user.getMobilePhoneNumber());
		values.put(UserDBHelper.TABLE_USER_ICON, user.getIcon());
		values.put(UserDBHelper.TABLE_USER_X1, user.getSex());
		values.put(UserDBHelper.TABLE_USER_X2, user.getSexset());
		int row = db.update(UserDBHelper.TABLE_USER, values, UserDBHelper.TABLE_ID+ "=?", new String[] { user.getId()+"" });
		DatabaseManager.getInstance().closeUserDatabase();
		return row;
	}

	public List<User> findAll() {
		SQLiteDatabase db = DatabaseManager.getInstance().openUserDatabase();
		List<User> result = new ArrayList<User>();;
		Cursor cursor = null;
		try {
			cursor = db.query(UserDBHelper.TABLE_USER, null, null, null, null, null,null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					User user = new User();
					int columnIndex = cursor.getColumnIndex(UserDBHelper.TABLE_USER_OBJID);
					String value = cursor.getString(columnIndex);
					user.setObjectId(value);
					columnIndex = cursor.getColumnIndex(UserDBHelper.TABLE_USER_NAME);
					value = cursor.getString(columnIndex);
					user.setUsername(value);
					columnIndex = cursor.getColumnIndex(UserDBHelper.TABLE_USER_PASW);
					value = cursor.getString(columnIndex);
					user.setPs(value);
					
					columnIndex = cursor.getColumnIndex(UserDBHelper.TABLE_USER_CHATNAME);
					value = cursor.getString(columnIndex);
					user.setChatName(value);
					columnIndex = cursor.getColumnIndex(UserDBHelper.TABLE_USER_CHATPASW);
					value = cursor.getString(columnIndex);
					user.setChatPsw(value);
					columnIndex = cursor.getColumnIndex(UserDBHelper.TABLE_USER_POINT);
					value = cursor.getString(columnIndex);
					user.setPoint(Integer.parseInt(value));
					columnIndex = cursor.getColumnIndex(UserDBHelper.TABLE_USER_JINGYAN);
					value = cursor.getString(columnIndex);
					user.setJingyan(Integer.parseInt(value));
					columnIndex = cursor.getColumnIndex(UserDBHelper.TABLE_USER_MAIL);
					value = cursor.getString(columnIndex);
					user.setEmail(value);
					columnIndex = cursor.getColumnIndex(UserDBHelper.TABLE_USER_PHONE);
					value = cursor.getString(columnIndex);
					user.setMobilePhoneNumber(value);
					columnIndex = cursor.getColumnIndex(UserDBHelper.TABLE_USER_ICON);
					value = cursor.getString(columnIndex);
					user.setIcon(value);
					columnIndex = cursor.getColumnIndex(UserDBHelper.TABLE_ID);
					value = cursor.getString(columnIndex);
					user.setId( Integer.parseInt(value));
					columnIndex = cursor.getColumnIndex(UserDBHelper.TABLE_USER_X1);
					value = cursor.getString(columnIndex);
					if(!TextUtils.isEmpty(value)){
						user.setSex(Boolean.parseBoolean(value));
					}else{
						user.setSex(true);
					}
					columnIndex = cursor.getColumnIndex(UserDBHelper.TABLE_USER_X2);
					value = cursor.getString(columnIndex);
					if(TextUtils.isEmpty(value)){
						user.setSexset(0);
					}else{
						user.setSexset(Integer.parseInt(value));
					}
					result.add(user);
				}
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
			DatabaseManager.getInstance().closeUserDatabase();
		}
		return null;
	}

}
