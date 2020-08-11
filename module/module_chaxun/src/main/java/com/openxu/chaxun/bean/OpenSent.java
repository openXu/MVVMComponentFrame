package com.openxu.chaxun.bean;

public class OpenSent {
	
	public String orig;    //词性
	public String trans;   //意思
	public String getOrig() {
		return orig;
	}
	public void setOrig(String orig) {
		this.orig = orig;
	}
	public String getTrans() {
		return trans;
	}
	public void setTrans(String trans) {
		this.trans = trans;
	}
	@Override
	public String toString() {
		return "OpenSent [orig=" + orig + ", trans=" + trans + "]";
	}
	

}
