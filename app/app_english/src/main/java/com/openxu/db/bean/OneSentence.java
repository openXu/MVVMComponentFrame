package com.openxu.db.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.openxu.db.annotation.Column;
import com.openxu.db.annotation.ID;
import com.openxu.db.annotation.TableName;
import com.openxu.db.helper.WordDBHelper;

/**
 * 金山词霸每日一句
 */

@TableName(WordDBHelper.TABLE_SENTENCE)
public class OneSentence implements Parcelable{
	@ID(autoincrement = true)
	@Column(WordDBHelper.TABLE_ID)
	public int _id;    
	@Column(WordDBHelper.TABLE_SENTENCE_SID)
	private String sid;              //		'sid':'' #每日一句ID    
	@Column(WordDBHelper.TABLE_SENTENCE_TTS)
	private String tts;              //		'tts': '' #音频地址   
	@Column(WordDBHelper.TABLE_SENTENCE_CON)
	private String content;          //		'content':'' #英文内容
	@Column(WordDBHelper.TABLE_SENTENCE_NOT)
	private String note;             //		'note': '' #中文内容
	@Column(WordDBHelper.TABLE_SENTENCE_LOVE)
	private String love;              //		'love': '' #每日一句喜欢个数
	@Column(WordDBHelper.TABLE_SENTENCE_TRANS)
	private String translation;      //		'translation':'' #词霸小编
	
//	private String picture;          //		'picture': '' #图片地址
	@Column(WordDBHelper.TABLE_SENTENCE_PIC)
	private String picture2;          //		'picture2': '' #大图片地址
//	private String caption;          //		'caption':'' #标题
	@Column(WordDBHelper.TABLE_SENTENCE_DATE)
	private String dateline;          //		'dateline':'' #时间
	@Column(WordDBHelper.TABLE_SENTENCE_SPV)
	private String s_pv;            //		's_pv':'' #浏览数
//	private String sp_pv;          //		'sp_pv':'' #语音评测浏览数
//	private String tags;            //		'tags':'' #相关标签
	@Column(WordDBHelper.TABLE_SENTENCE_FXIMG)
	private String fenxiang_img;   //		'fenxiang_img':'' #合成图片，建议分享微博用的
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public String getTts() {
		return tts;
	}
	public void setTts(String tts) {
		this.tts = tts;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getLove() {
		return love;
	}
	public void setLove(String love) {
		this.love = love;
	}
	public String getTranslation() {
		return translation;
	}
	public void setTranslation(String translation) {
		this.translation = translation;
	}
	public String getPicture2() {
		return picture2;
	}
	public void setPicture2(String picture2) {
		this.picture2 = picture2;
	}
	public String getDateline() {
		return dateline;
	}
	public void setDateline(String dateline) {
		this.dateline = dateline;
	}
	public String getS_pv() {
		return s_pv;
	}
	public void setS_pv(String s_pv) {
		this.s_pv = s_pv;
	}
	public String getFenxiang_img() {
		return fenxiang_img;
	}
	public void setFenxiang_img(String fenxiang_img) {
		this.fenxiang_img = fenxiang_img;
	}
	@Override
	public String toString() {
		return "OneSentence [_id=" + _id + ", sid=" + sid + ", tts=" + tts
				+ ", content=" + content + ", note=" + note + ", love=" + love
				+ ", translation=" + translation + ", picture2=" + picture2
				+ ", dateline=" + dateline + ", s_pv=" + s_pv + "]";
	}
	
	// 序列化
		@Override
		public int describeContents() {
			return 0;
		}
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			String[] strs = new String[] { sid, tts, content, note, love, translation,
					picture2, dateline, s_pv, fenxiang_img};
			int[] ints = new int[] {_id};
			dest.writeStringArray(strs);
			dest.writeIntArray(ints);
		}

		public static final Creator<OneSentence> CREATOR = new Creator<OneSentence>() {
			@Override
			public OneSentence createFromParcel(Parcel source) {
				OneSentence sentence = new OneSentence();
				int[] ints = new int[1];
				String[] strs = new String[10];
				source.readStringArray(strs);
				source.readIntArray(ints);
				sentence._id = ints[0];
				sentence.sid = strs[0];
				sentence.tts = strs[1];
				sentence.content = strs[2];
				sentence.note = strs[3];
				sentence.love = strs[4];
				sentence.translation = strs[5];
				sentence.picture2 = strs[6];
				sentence.dateline = strs[7];
				sentence.s_pv = strs[8];
				sentence.fenxiang_img = strs[9];
				return sentence;
			}

			@Override
			public OneSentence[] newArray(int size) {
				return null;
			}

		};
	
	
}
