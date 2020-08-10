package com.openxu.fanyi.bean;

import java.util.List;

public class FanyiResult {
//	{"from":"en","to":"zh","trans_result":[{"src":"Hello","dst":"\u4f60\u597d"}]}

	//{"error_code":"52003","error_msg":"UNAUTHORIZED USER"}

	private int error_code;
	private String error_msg;

	public int getError_code() {
		return error_code;
	}

	public void setError_code(int error_code) {
		this.error_code = error_code;
	}

	public String getError_msg() {
		return error_msg;
	}

	public void setError_msg(String error_msg) {
		this.error_msg = error_msg;
	}

	private String from;
	private String to;
	private List<Fanyi>  trans_result;

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public List<Fanyi> getTrans_result() {
		return trans_result;
	}

	public void setTrans_result(List<Fanyi> trans_result) {
		this.trans_result = trans_result;
	}

	public class Fanyi {
		private String src;
		private String dst;
		public String getSrc() {
			return src;
		}
		public void setSrc(String src) {
			this.src = src;
		}
		public String getDst() {
			return dst;
		}
		public void setDst(String dst) {
			this.dst = dst;
		}

	}
	
}
