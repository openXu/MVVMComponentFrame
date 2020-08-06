package com.openxu.db.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.openxu.db.annotation.Column;
import com.openxu.db.annotation.ID;
import com.openxu.db.annotation.TableName;
import com.openxu.db.helper.WordDBHelper;

@TableName(WordDBHelper.TABLE_SENTENCE)
public class WordBook implements Parcelable {
	@ID(autoincrement = true)
	@Column(WordDBHelper.TABLE_ID)
	public int _id;
	@Column(WordDBHelper.TABLE_MYWORD_NAME)
	public String name;
	@Column(WordDBHelper.TABLE_MYWORD_CREATEDATE)
	public String createDate;
	@Column(WordDBHelper.TABLE_MYWORD_TABLE)
	public String wordTable;
	
	public int num;

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public String getWordTable() {
		return wordTable;
	}

	public void setWordTable(String wordTable) {
		this.wordTable = wordTable;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	@Override
	public String toString() {
		return "WordBook [_id=" + _id + ", name=" + name + ", createDate="
				+ createDate + ", wordTable=" + wordTable +
				 "]";
	}

	// 序列化
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		String[] strs = new String[] { name, createDate, wordTable };
		int[] ints = new int[] { _id};
		dest.writeStringArray(strs);
		dest.writeIntArray(ints);
	}

	public static final Creator<WordBook> CREATOR = new Creator<WordBook>() {
		@Override
		public WordBook createFromParcel(Parcel source) {
			WordBook book = new WordBook();
			int[] ints = new int[1];
			String[] strs = new String[3];
			source.readStringArray(strs);
			source.readIntArray(ints);
			book._id = ints[0];
			book.name = strs[0];
			book.createDate = strs[1];
			book.wordTable = strs[2];
			return book;
		}

		@Override
		public WordBook[] newArray(int size) {
			return null;
		}

	};

}
