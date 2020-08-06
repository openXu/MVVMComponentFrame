package com.openxu.db.bean;

import cn.bmob.im.bean.BmobChatUser;
public class User extends BmobChatUser{

	private static final long serialVersionUID = 1L;
	private int id;
	private String icon;
	private int point;   //积分
	private int jingyan;  //经验值
	private String ps;   //密码
	
	private String chatName;   //聊天用户名
	private String chatPsw;     //聊天密码
	
	/**
	 * //性别-true-男
	 */
	private boolean sex;
	private int sexset;  //0未设置
	public User(){
		super();
		setTableName("hjdc_user");
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSex() {
		return sex+"";
	}

	public void setSex(boolean sex) {
		this.sex = sex;
	}

	public int getSexset() {
		return sexset;
	}

	public void setSexset(int sexset) {
		this.sexset = sexset;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public int getJingyan() {
		return jingyan;
	}

	public void setJingyan(int jingyan) {
		this.jingyan = jingyan;
	}

	public String getPs() {
		return ps;
	}

	public void setPs(String ps) {
		this.ps = ps;
		setPassword(ps);
	}

	public String getChatName() {
		return chatName;
	}

	public void setChatName(String chatName) {
		this.chatName = chatName;
	}

	public String getChatPsw() {
		return chatPsw;
	}

	public void setChatPsw(String chatPsw) {
		this.chatPsw = chatPsw;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", objectID=" + getObjectId() + ", icon=" + icon + ", point=" + point
				+ ", email=" + getEmail()+ ", phone=" + getMobilePhoneNumber()
				+ ", jingyan=" + jingyan + ", ps=" + ps+", chatName="+chatName+",chatPsw="+chatPsw
				+ "]";
	}
	


	
}
