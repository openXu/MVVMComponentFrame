package com.openxu.db.bean;

import com.openxu.db.annotation.Column;


public class RenwuJilu {

	@Column("COUNT(*)")
	public int count;
	@Column("date")
	public String date;
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	@Override
	public String toString() {
		return "RenwuJilu [count=" + count + ", date=" + date + "]";
	}
	
	
}
