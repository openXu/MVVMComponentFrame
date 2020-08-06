package com.openxu.core.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.openxu.core.bean.User;
import com.openxu.core.utils.XLog;

public class SharedData {

    private Context context;
    private static final String SP_NAME = "openXuSp";
    private static SharedData instance;

    public static SharedData getInstance(Context context) {
        if(instance==null)
            instance = new SharedData(context);
        return instance;
    }

    private SharedData(Context context) {
        this.context = context;
        getUser();
    }

    /**
     * key
     */
    private static final String KEY_AUTO_LOGIN = "auto_login";//是否自动登录
    private static final String KEY_CURRENT_USER = "current_userinfo";//当前登录用户信息

    /************************************用户登录信息**********************************/
    //当前登录的用户信息，缓存，避免每次使用都需要重新加载
    private User user;


    public User getUser() {
        if (user == null || TextUtils.isEmpty(user.getUserID())) {
            String userJson = getData(KEY_CURRENT_USER, String.class);
            XLog.w("获取持久化用户信息：" + userJson);
            if (!TextUtils.isEmpty(userJson)) {
                user = new Gson().fromJson(userJson, User.class);
            } else {
                //容错
                user = new User();
            }
        }
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        String userJson = new Gson().toJson(user);
        XLog.i("持久化用户信息：" + userJson);
        saveData(KEY_CURRENT_USER, userJson);
    }


    public boolean getAutoLogin() {
        return getData(KEY_AUTO_LOGIN, Boolean.class);
    }

    public void setAutoLogin(boolean autoLogin) {
        saveData(KEY_AUTO_LOGIN, autoLogin);
    }


    public <T> T getData(String key, Class clazz) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        Object obj;
        if (clazz == String.class)
            obj = sharedPreferences.getString(key, "");
        else if (clazz == Integer.class)
            obj = sharedPreferences.getInt(key, 0);
        else if (clazz == Float.class)
            obj = sharedPreferences.getFloat(key, 0);
        else if (clazz == Long.class)
            obj = sharedPreferences.getLong(key, 0);
        else if (clazz == Boolean.class)
            obj = sharedPreferences.getBoolean(key, false);
        else
            obj = sharedPreferences.getString(key, "");
        return (T) obj;
    }

    public void saveData(String key, Object data) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (data instanceof String)
            editor.putString(key, (String) data);
        else if (data instanceof Integer)
            editor.putInt(key, (int) data);
        else if (data instanceof Float)
            editor.putFloat(key, (float) data);
        else if (data instanceof Long)
            editor.putLong(key, (long) data);
        else if (data instanceof Boolean)
            editor.putBoolean(key, (boolean) data);
        else
            editor.putString(key, (String) data);
        editor.commit();
    }

    public void clear() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
        user = null;
    }


}

