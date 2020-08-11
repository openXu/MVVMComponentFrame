package com.openxu.db.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class Word implements Parcelable {

	/**以下字段为数据库表字段*/
	@Id(autoincrement = true)
	public Long _id;
	public String first;
	public String english;
	public String level;
	public String isLocal;
	public String love;
	public String isError;
	public String remenber;
	public String date;
	public String exchange;   //其他形式
	public String ph_en;   //英式
	public String ph_en_mp3;   //英式
	public String ph_am;   //美式
	public String ph_am_mp3;   //美式
	public String parts;   //中文
	public String sents;   //例句
	public String addDate;   //添加日期
	public String fxDate;   //复习日期
	public String fx;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeValue(this._id);
		dest.writeString(this.first);
		dest.writeString(this.english);
		dest.writeString(this.level);
		dest.writeString(this.isLocal);
		dest.writeString(this.love);
		dest.writeString(this.isError);
		dest.writeString(this.remenber);
		dest.writeString(this.date);
		dest.writeString(this.exchange);
		dest.writeString(this.ph_en);
		dest.writeString(this.ph_en_mp3);
		dest.writeString(this.ph_am);
		dest.writeString(this.ph_am_mp3);
		dest.writeString(this.parts);
		dest.writeString(this.sents);
		dest.writeString(this.addDate);
		dest.writeString(this.fxDate);
		dest.writeString(this.fx);
	}

	public Long get_id() {
		return this._id;
	}

	public void set_id(Long _id) {
		this._id = _id;
	}

	public String getFirst() {
		return this.first;
	}

	public void setFirst(String first) {
		this.first = first;
	}

	public String getEnglish() {
		return this.english;
	}

	public void setEnglish(String english) {
		this.english = english;
	}

	public String getLevel() {
		return this.level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getIsLocal() {
		return this.isLocal;
	}

	public void setIsLocal(String isLocal) {
		this.isLocal = isLocal;
	}

	public String getLove() {
		return this.love;
	}

	public void setLove(String love) {
		this.love = love;
	}

	public String getIsError() {
		return this.isError;
	}

	public void setIsError(String isError) {
		this.isError = isError;
	}

	public String getRemenber() {
		return this.remenber;
	}

	public void setRemenber(String remenber) {
		this.remenber = remenber;
	}

	public String getDate() {
		return this.date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getExchange() {
		return this.exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public String getPh_en() {
		return this.ph_en;
	}

	public void setPh_en(String ph_en) {
		this.ph_en = ph_en;
	}

	public String getPh_en_mp3() {
		return this.ph_en_mp3;
	}

	public void setPh_en_mp3(String ph_en_mp3) {
		this.ph_en_mp3 = ph_en_mp3;
	}

	public String getPh_am() {
		return this.ph_am;
	}

	public void setPh_am(String ph_am) {
		this.ph_am = ph_am;
	}

	public String getPh_am_mp3() {
		return this.ph_am_mp3;
	}

	public void setPh_am_mp3(String ph_am_mp3) {
		this.ph_am_mp3 = ph_am_mp3;
	}

	public String getParts() {
		return this.parts;
	}

	public void setParts(String parts) {
		this.parts = parts;
	}

	public String getSents() {
		return this.sents;
	}

	public void setSents(String sents) {
		this.sents = sents;
	}

	public String getAddDate() {
		return this.addDate;
	}

	public void setAddDate(String addDate) {
		this.addDate = addDate;
	}

	public String getFxDate() {
		return this.fxDate;
	}

	public void setFxDate(String fxDate) {
		this.fxDate = fxDate;
	}

	public String getFx() {
		return this.fx;
	}

	public void setFx(String fx) {
		this.fx = fx;
	}

	public Word() {
	}

	protected Word(Parcel in) {
		this._id = (Long) in.readValue(Long.class.getClassLoader());
		this.first = in.readString();
		this.english = in.readString();
		this.level = in.readString();
		this.isLocal = in.readString();
		this.love = in.readString();
		this.isError = in.readString();
		this.remenber = in.readString();
		this.date = in.readString();
		this.exchange = in.readString();
		this.ph_en = in.readString();
		this.ph_en_mp3 = in.readString();
		this.ph_am = in.readString();
		this.ph_am_mp3 = in.readString();
		this.parts = in.readString();
		this.sents = in.readString();
		this.addDate = in.readString();
		this.fxDate = in.readString();
		this.fx = in.readString();
	}

	@Generated(hash = 1581525055)
	public Word(Long _id, String first, String english, String level,
			String isLocal, String love, String isError, String remenber, String date,
			String exchange, String ph_en, String ph_en_mp3, String ph_am,
			String ph_am_mp3, String parts, String sents, String addDate, String fxDate,
			String fx) {
		this._id = _id;
		this.first = first;
		this.english = english;
		this.level = level;
		this.isLocal = isLocal;
		this.love = love;
		this.isError = isError;
		this.remenber = remenber;
		this.date = date;
		this.exchange = exchange;
		this.ph_en = ph_en;
		this.ph_en_mp3 = ph_en_mp3;
		this.ph_am = ph_am;
		this.ph_am_mp3 = ph_am_mp3;
		this.parts = parts;
		this.sents = sents;
		this.addDate = addDate;
		this.fxDate = fxDate;
		this.fx = fx;
	}

	public static final Creator<Word> CREATOR = new Creator<Word>() {
		@Override
		public Word createFromParcel(Parcel source) {
			return new Word(source);
		}

		@Override
		public Word[] newArray(int size) {
			return new Word[size];
		}
	};
}
