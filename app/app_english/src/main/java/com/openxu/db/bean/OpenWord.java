package com.openxu.db.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.openxu.db.annotation.Column;
import com.openxu.db.annotation.ID;
import com.openxu.db.helper.WordDBHelper;

public class OpenWord implements Parcelable {
	@ID(autoincrement = true)
	@Column(WordDBHelper.TABLE_ID)
	public int _id;
	@Column(WordDBHelper.TABLE_WORD_FIRST)
	public String first;
	@Column(WordDBHelper.TABLE_WORD_EN)
	public String english;
	@Column(WordDBHelper.TABLE_WORD_LEVEL)
	public String lev;
	@Column(WordDBHelper.TABLE_WORD_LOCAL)
	public int isLocal;
	@Column(WordDBHelper.TABLE_WORD_ERROR)
	public int isError;
	@Column(WordDBHelper.TABLE_WORD_REMENBER)
	public int remenber;
	@Column(WordDBHelper.TABLE_WORD_REM_DATE)
	public String date;
	@Column(WordDBHelper.TABLE_WORD_EXCHANGE)
	public String exchange;   //其他形式
	@Column(WordDBHelper.TABLE_WORD_PH_EN)
	public String ph_en;   //英式
	@Column(WordDBHelper.TABLE_WORD_PH_EN3)
	public String ph_en_mp3;   //英式
	@Column(WordDBHelper.TABLE_WORD_PH_AM)
	public String ph_am;   //美式
	@Column(WordDBHelper.TABLE_WORD_PH_AM3)
	public String ph_am_mp3;   //美式
	@Column(WordDBHelper.TABLE_WORD_CHINA)
	public String parts;   //中文
	@Column(WordDBHelper.TABLE_WORD_SENTS)
	public String sents;   //例句
	@Column(WordDBHelper.TABLE_WORD_ADDDATE)
	public String addDate;   //添加日期
	@Column(WordDBHelper.TABLE_WORD_FXDATE)
	public String fxDate;   //复习日期
	@Column(WordDBHelper.TABLE_WORD_FX)
	public int fx;
	
	public OpenWord() {}

	public int get_id() {
		return _id;
	}

	public int getIsError() {
		return isError;
	}

	public void setIsError(int isError) {
		this.isError = isError;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getFirst() {
		return first;
	}

	public void setFirst(String first) {
		this.first = first;
	}

	public String getAddDate() {
		return addDate;
	}

	public void setAddDate(String addDate) {
		this.addDate = addDate;
	}

	public String getEnglish() {
		return english;
	}

	public void setEnglish(String english) {
		this.english = english;
	}

	public String getLev() {
		return lev;
	}

	public void setLev(String lev) {
		this.lev = lev;
	}

	public int getRemenber() {
		return remenber;
	}

	public void setRemenber(int remenber) {
		this.remenber = remenber;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public String getPh_en() {
		return ph_en;
	}

	public void setPh_en(String ph_en) {
		this.ph_en = ph_en;
	}

	public String getPh_en_mp3() {
		return ph_en_mp3;
	}

	public void setPh_en_mp3(String ph_en_mp3) {
		this.ph_en_mp3 = ph_en_mp3;
	}

	public String getPh_am() {
		return ph_am;
	}

	public void setPh_am(String ph_am) {
		this.ph_am = ph_am;
	}

	public String getPh_am_mp3() {
		return ph_am_mp3;
	}

	public void setPh_am_mp3(String ph_am_mp3) {
		this.ph_am_mp3 = ph_am_mp3;
	}

	public String getParts() {
		return parts;
	}

	public void setParts(String parts) {
		this.parts = parts;
	}

	public String getSents() {
		return sents;
	}

	public void setSents(String sents) {
		this.sents = sents;
	}

	public int getIsLocal() {
		return isLocal;
	}

	public void setIsLocal(int isLocal) {
		this.isLocal = isLocal;
	}
	
	public String getFxDate() {
		return fxDate;
	}

	public void setFxDate(String fxDate) {
		this.fxDate = fxDate;
	}

	public int getFx() {
		return fx;
	}

	public void setFx(int fx) {
		this.fx = fx;
	}

	public String getSortLetters() {
		if (first == null || first.length() == 0)
			first = "#";
		return first;
	}

	@Override
	public String toString() {
		return "OpenWord [_id=" + _id + ", first=" + first + ", english="
				+ english + ", lev=" + lev + ", isLocal=" + isLocal
				+ ", isError=" + isError + ", remenber=" + remenber + ", date="
				+ date + ", exchange=" + exchange + ", ph_en=" + ph_en
				+ ", ph_en_mp3=" + ph_en_mp3 + ", ph_am=" + ph_am
				+ ", ph_am_mp3=" + ph_am_mp3 + ", parts=" + parts + ", sents="
				+ sents + "]";
	}

	// 序列化
		@Override
		public int describeContents() {
			return 0;
		}
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			String[] strs = new String[] { first, english, lev, date, exchange, 
					ph_en, ph_en_mp3, ph_am, ph_am_mp3, parts, sents,addDate ,fxDate};
			int[] ints = new int[] { _id, isLocal, isError,remenber,fx };
			dest.writeStringArray(strs);
			dest.writeIntArray(ints);
		}

		public static final Creator<OpenWord> CREATOR = new Creator<OpenWord>() {
			@Override
			public OpenWord createFromParcel(Parcel source) {
				OpenWord word = new OpenWord();
				int[] ints = new int[5];
				String[] strs = new String[13];
				source.readStringArray(strs);
				source.readIntArray(ints);
				word._id = ints[0];
				word.isLocal = ints[1];
				word.isError = ints[2];
				word.remenber = ints[3];
				word.fx = ints[4];
				word.first = strs[0];
				word.english = strs[1];
				word.lev = strs[2];
				word.date = strs[3];
				word.exchange = strs[4];
				word.ph_en = strs[5];
				word.ph_en_mp3 = strs[6];
				word.ph_am = strs[7];
				word.ph_am_mp3 = strs[8];
				word.parts = strs[9];
				word.sents = strs[10];
				word.addDate = strs[11];
				word.fxDate = strs[12];
				return word;
			}

			@Override
			public OpenWord[] newArray(int size) {
				return null;
			}

		};
}
