package com.openxu.bean;

import android.graphics.Color;

/**
 * Author: openXu
 * Time: 2020/4/30 11:13
 * class: User
 * Description:
 */
public class User {

    private String userName;
    private String userPwd;

    public int color = Color.BLUE;
    public int size = 20;
    public String imageUrl = "";
    public User(String userName, String userPwd) {
        this.userName = userName;
        this.userPwd = userPwd;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }
}
