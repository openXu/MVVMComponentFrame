package com.openxu.db.bean;

public class OpenSymbol {
	
	//中文-->英文
	public String ph_zh;  
	
	//英文-->中文
	public String parts;    //中文
	public String ph_en;   
	public String ph_en_mp3;   
	public String ph_am;   
	public String ph_am_mp3;
	public String getParts() {
		return parts;
	}
	public void setParts(String parts) {
		this.parts = parts;
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
	
}
